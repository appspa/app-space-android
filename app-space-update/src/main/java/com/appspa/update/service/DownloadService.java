/*
 * Copyright (C) 2018 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.appspa.update.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.appspa.update.AppSpace;
import com.appspa.update.R;
import com.appspa.update._AppSpace;
import com.appspa.update.entity.DownloadEntity;
import com.appspa.update.entity.UpdateEntity;
import com.appspa.update.entity.UpdateError;
import com.appspa.update.logs.UpdateLog;
import com.appspa.update.proxy.IUpdateHttpService;
import com.appspa.update.utils.ApkUtils;
import com.appspa.update.utils.FileUtils;
import com.appspa.update.utils.PatchUtils;
import com.appspa.update.utils.UpdateUtils;

import java.io.File;

import static com.appspa.update.entity.UpdateError.ERROR.DOWNLOAD_FAILED;
import static com.appspa.update.entity.UpdateError.ERROR.FAIL_PATCH;

/**
 * APK下载服务
 *
 * @author treexi
 * @since 2018/7/5 上午11:15
 */
public class DownloadService extends Service {

    private static final int DOWNLOAD_NOTIFY_ID = 1000;

    private static boolean sIsRunning = false;

    private static final String CHANNEL_ID = "space_channel_id";
    private static final CharSequence CHANNEL_NAME = "space_channel_name";

    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;

    //=====================绑定服务============================//

    /**
     * 绑定服务
     *
     * @param connection 服务连接
     */
    public static void bindService(ServiceConnection connection) {
        Intent intent = new Intent(AppSpace.getContext(), DownloadService.class);
        AppSpace.getContext().startService(intent);
        AppSpace.getContext().bindService(intent, connection, Context.BIND_AUTO_CREATE);
        sIsRunning = true;
    }

    /**
     * 停止下载服务
     *
     * @param contentText
     */
    private void stop(String contentText) {
        if (mBuilder != null) {
            mBuilder.setContentTitle(UpdateUtils.getAppName(DownloadService.this))
                    .setContentText(contentText);
            Notification notification = mBuilder.build();
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            mNotificationManager.notify(DOWNLOAD_NOTIFY_ID, notification);
        }
        close();
    }

    /**
     * 关闭服务
     */
    private void close() {
        sIsRunning = false;
        stopSelf();
    }

    //=====================生命周期============================//

    /**
     * 下载服务是否在运行
     *
     * @return 是否在运行
     */
    public static boolean isRunning() {
        return sIsRunning;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        sIsRunning = true;
        return new DownloadBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        sIsRunning = false;
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        mNotificationManager = null;
        mBuilder = null;
        super.onDestroy();
    }

    //========================下载通知===================================//

    /**
     * 创建通知
     */
    private void setUpNotification(@NonNull DownloadEntity downloadEntity) {
        if (!downloadEntity.isShowNotification()) {
            return;
        }

        initNotification();
    }

    /**
     * 初始化通知
     */
    private void initNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            //设置绕过免打扰模式
//            channel.setBypassDnd(false);
//            //检测是否绕过免打扰模式
//            channel.canBypassDnd();
//            //设置在锁屏界面上显示这条通知
//            channel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
//            channel.setLightColor(Color.GREEN);
//            channel.setShowBadge(true);
//            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            channel.enableVibration(false);
            channel.enableLights(false);
            mNotificationManager.createNotificationChannel(channel);
        }

        mBuilder = getNotificationBuilder();
        mNotificationManager.notify(DOWNLOAD_NOTIFY_ID, mBuilder.build());
    }

    private NotificationCompat.Builder getNotificationBuilder() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.space_start_download))
                .setContentText(getString(R.string.space_connecting_service))
                .setSmallIcon(R.drawable.spa_icon_app_update)
                .setLargeIcon(UpdateUtils.drawable2Bitmap(UpdateUtils.getAppIcon(DownloadService.this)))
                .setOngoing(true)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis());
    }

    /**
     * DownloadBinder中定义了一些实用的方法
     *
     * @author treexi
     * @since 2021/1/24 1:59 AM
     */
    public class DownloadBinder extends Binder {

        private FileDownloadCallBack mFileDownloadCallBack;

        private UpdateEntity mUpdateEntity;

        /**
         * 开始下载
         *
         * @param updateEntity     新app信息
         * @param downloadListener 下载监听
         */
        public void start(@NonNull UpdateEntity updateEntity, @Nullable OnFileDownloadListener downloadListener) {
            //下载
            mUpdateEntity = updateEntity;
            startDownload(updateEntity, mFileDownloadCallBack = new FileDownloadCallBack(updateEntity, downloadListener));
        }

        /**
         * 停止下载服务
         *
         * @param msg
         */
        public void stop(String msg) {
            if (mFileDownloadCallBack != null) {
                mFileDownloadCallBack.onCancel();
                mFileDownloadCallBack = null;
            }
            if (mUpdateEntity.getIUpdateHttpService() != null) {
                mUpdateEntity.getIUpdateHttpService().cancelDownload(mUpdateEntity.getCurDownloadEntity().getDownloadUrl());
            } else {
                UpdateLog.e("cancelDownload failed, mUpdateEntity.getIUpdateHttpService() is null!");
            }
            DownloadService.this.stop(msg);
        }

        /**
         * 显示通知
         */
        public void showNotification() {
            if (mBuilder == null && DownloadService.isRunning()) {
                initNotification();
            }
        }
    }

    /**
     * 下载模块
     */
    private void startDownload(@NonNull UpdateEntity updateEntity, @NonNull FileDownloadCallBack fileDownloadCallBack) {
        DownloadEntity downLoadEntity = updateEntity.getCurDownloadEntity();
        String apkUrl = downLoadEntity.getDownloadUrl();
        if (TextUtils.isEmpty(apkUrl)) {
            String contentText = getString(R.string.space_tip_download_url_error);
            stop(contentText);
            return;
        }
        String apkName = UpdateUtils.getFileNameByDownloadUrl(apkUrl);

        File apkCacheDir = FileUtils.getFileByPath(updateEntity.getCacheDir());
        if (apkCacheDir == null) {
            apkCacheDir = UpdateUtils.getDefaultDiskCacheDir();
        }
        try {
            if (!FileUtils.isFileExists(apkCacheDir)) {
                apkCacheDir.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String target = apkCacheDir + File.separator + updateEntity.getVersionName();

        UpdateLog.d("开始下载更新文件, 下载地址:" + apkUrl + ", 保存路径:" + target + ", 文件名:" + apkName);
        if (updateEntity.getIUpdateHttpService() != null) {
            updateEntity.getIUpdateHttpService().download(apkUrl, target, apkName, fileDownloadCallBack);
        } else {
            UpdateLog.e("startDownload failed, updateEntity.getIUpdateHttpService() is null!");
        }
    }

    /**
     * 文件下载处理
     */
    private class FileDownloadCallBack implements IUpdateHttpService.DownloadCallback {

        private final DownloadEntity mDownloadEntity;
        /**
         * 文件下载监听
         */
        private OnFileDownloadListener mOnFileDownloadListener;

        /**
         * 是否下载完成后自动安装
         */
        private final boolean mIsAutoInstall;

        private int mOldRate = 0;

        private boolean mIsCancel;

        private final Handler mMainHandler;

        FileDownloadCallBack(@NonNull UpdateEntity updateEntity, @Nullable OnFileDownloadListener listener) {
            mDownloadEntity = updateEntity.getCurDownloadEntity();
            mIsAutoInstall = updateEntity.isAutoInstall();
            mOnFileDownloadListener = listener;
            mMainHandler = new Handler(Looper.getMainLooper());
        }

        @Override
        public void onStart() {
            if (mIsCancel) {
                return;
            }

            //清空通知栏状态
            mNotificationManager.cancel(DOWNLOAD_NOTIFY_ID);
            mBuilder = null;

            //初始化通知栏
            setUpNotification(mDownloadEntity);
            dispatchOnStart();
        }

        private void dispatchOnStart() {
            if (UpdateUtils.isMainThread()) {
                if (mOnFileDownloadListener != null) {
                    mOnFileDownloadListener.onStart();
                }
            } else {
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mOnFileDownloadListener != null) {
                            mOnFileDownloadListener.onStart();
                        }
                    }
                });
            }
        }

        @Override
        public void onProgress(float progress, long total) {
            if (mIsCancel) {
                return;
            }

            int rate = Math.round(progress * 100);
            //做一下判断，防止自回调过于频繁，造成更新通知栏进度过于频繁，而出现卡顿的问题。
            if (canRefreshProgress(rate)) {
                dispatchOnProgress(progress, total);
                if (mBuilder != null) {
                    mBuilder.setContentTitle(getString(R.string.space_lab_downloading) + UpdateUtils.getAppName(DownloadService.this))
                            .setContentText(rate + "%")
                            .setProgress(100, rate, false)
                            .setWhen(System.currentTimeMillis());
                    Notification notification = mBuilder.build();
                    notification.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_ONLY_ALERT_ONCE;
                    mNotificationManager.notify(DOWNLOAD_NOTIFY_ID, notification);
                }
                //重新赋值
                mOldRate = rate;
            }
        }

        /**
         * 是否可以刷新进度
         *
         * @param newRate 最新进度
         * @return 是否可以刷新进度
         */
        private boolean canRefreshProgress(int newRate) {
            if (mBuilder != null) {
                // 系统通知栏对单个应用通知队列通长度进行了限制。
                // notify方法会将Notification加入系统的通知队列，当前应用发出的Notification数量超过50时，不再继续向系统的通知队列添加Notification，即造成了notificationManagerCompat.notify(TAG, NOTIFY_ID, notify)无效的现象。
                return Math.abs(newRate - mOldRate) >= 4;
            } else {
                return Math.abs(newRate - mOldRate) >= 1;
            }
        }

        private void dispatchOnProgress(final float progress, final long total) {
            if (UpdateUtils.isMainThread()) {
                if (mOnFileDownloadListener != null) {
                    mOnFileDownloadListener.onProgress(progress, total);
                }
            } else {
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mOnFileDownloadListener != null) {
                            mOnFileDownloadListener.onProgress(progress, total);
                        }
                    }
                });
            }
        }

        @Override
        public void onSuccess(final File file) {
            File apkFile;
            if (mDownloadEntity != null && mDownloadEntity.isPatch()) {
                if (!mDownloadEntity.isFileValid(file)) {
                    _AppSpace.onUpdateError(UpdateError.ERROR.FAIL_OLD_MD5, "Apk file verify failed, please check whether the MD5 value you set is correct！");
                    return;
                }
                String newApkPath = file.getAbsolutePath().replace(".patch", ".apk");
                long currentTime = System.currentTimeMillis();
                int result = patchApk(DownloadService.this, file, newApkPath);
                UpdateLog.d("InstallApk patchApk time" + (System.currentTimeMillis() - currentTime ));
                if (result == 0) {
                    apkFile = new File(newApkPath);
                }else {
                    onError(FAIL_PATCH, new Throwable("patch apk error"));
                    return;
                }
            }else {
                apkFile = file;
            }
            if (UpdateUtils.isMainThread()) {
                handleOnSuccess(apkFile);
            } else {
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        handleOnSuccess(apkFile);
                    }
                });
            }
        }

        private int patchApk(Context context, File patchFile, String newAPKPath) {
            String oldApkSource = ApkUtils.getSourceApkPath(context, context.getPackageName());
            return PatchUtils.patch(oldApkSource, newAPKPath, patchFile.getAbsolutePath());
        }

        private void handleOnSuccess(File file) {
            if (mIsCancel) {
                return;
            }
            if (mOnFileDownloadListener != null) {
                if (!mOnFileDownloadListener.onCompleted(file)) {
                    close();
                    return;
                }
            }

            UpdateLog.d("更新文件下载完成, 文件路径:" + file.getAbsolutePath());
            try {
                if (UpdateUtils.isAppOnForeground(DownloadService.this)) {
                    //App前台运行
                    mNotificationManager.cancel(DOWNLOAD_NOTIFY_ID);

                    if (mIsAutoInstall) {
                        _AppSpace.startInstallApk(DownloadService.this, file, mDownloadEntity);
                    } else {
                        showDownloadCompleteNotification(file);
                    }
                } else {
                    showDownloadCompleteNotification(file);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //下载完自杀
            close();
        }

        @Override
        public void onError(Throwable throwable) {
            onError(DOWNLOAD_FAILED, throwable);
        }

        public void onError(int code,Throwable throwable) {
            if (mIsCancel) {
                return;
            }
            _AppSpace.onUpdateError(code, throwable != null ? throwable.getMessage() : "unknown error!");
            //App前台运行
            dispatchOnError(throwable);
            try {
                mNotificationManager.cancel(DOWNLOAD_NOTIFY_ID);
                close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        private void dispatchOnError(final Throwable throwable) {
            if (UpdateUtils.isMainThread()) {
                if (mOnFileDownloadListener != null) {
                    mOnFileDownloadListener.onError(throwable);
                }
            } else {
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mOnFileDownloadListener != null) {
                            mOnFileDownloadListener.onError(throwable);
                        }
                    }
                });
            }
        }

        /**
         * 取消下载
         */
        void onCancel() {
            mOnFileDownloadListener = null;
            mIsCancel = true;
        }
    }

    private void showDownloadCompleteNotification(File file) {
        //App后台运行
        //更新参数,注意flags要使用FLAG_UPDATE_CURRENT
        Intent installAppIntent = ApkUtils.getInstallAppIntent(file);
        PendingIntent contentIntent = PendingIntent.getActivity(DownloadService.this, 0, installAppIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (mBuilder == null) {
            mBuilder = getNotificationBuilder();
        }
        mBuilder.setContentIntent(contentIntent)
                .setContentTitle(UpdateUtils.getAppName(DownloadService.this))
                .setContentText(getString(R.string.space_download_complete))
                .setProgress(0, 0, false)
                //                        .setAutoCancel(true)
                .setDefaults((Notification.DEFAULT_ALL));
        Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.notify(DOWNLOAD_NOTIFY_ID, notification);
    }
}
