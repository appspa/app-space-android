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

package com.appspa.update;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;

import com.appspa.update.entity.UpdateError;
import com.appspa.update.listener.OnInstallListener;
import com.appspa.update.listener.OnUpdateFailureListener;
import com.appspa.update.logs.ILogger;
import com.appspa.update.logs.UpdateLog;
import com.appspa.update.proxy.IFileEncryptor;
import com.appspa.update.proxy.IUpdateChecker;
import com.appspa.update.proxy.IUpdateDownloader;
import com.appspa.update.proxy.IUpdateHttpService;
import com.appspa.update.proxy.IUpdateParser;
import com.appspa.update.proxy.IUpdatePrompter;
import com.appspa.update.proxy.impl.DefaultFileEncryptor;
import com.appspa.update.proxy.impl.DefaultUpdatePrompter;
import com.appspa.update.utils.ApkUtils;
import com.appspa.update.listener.impl.DefaultInstallListener;
import com.appspa.update.listener.impl.DefaultUpdateFailureListener;
import com.appspa.update.proxy.impl.DefaultUpdateChecker;
import com.appspa.update.proxy.impl.DefaultUpdateDownloader;
import com.appspa.update.proxy.impl.DefaultUpdateParser;

import java.util.Map;
import java.util.TreeMap;

/**
 * 版本更新的入口
 *
 * @author treexi
 * @since 2018/6/29 下午7:47
 */
public class AppSpace {

    private Application mContext;
    private static AppSpace sInstance;

    //========全局属性==========//
    /**
     * 请求参数【比如apk-key或者versionCode等】
     */
    Map<String, Object> mParams;
    /**
     * 是否使用的是Get请求
     */
    boolean mIsGet;
    /**
     * 是否只在wifi下进行版本更新检查
     */
    boolean mIsWifiOnly;
    /**
     * 是否是自动版本更新模式【无人干预,有版本更新直接下载、安装】
     */
    boolean mIsAutoMode;
    /**
     * 是否是增量更新模式
     */
    boolean mIsPatchMode;
    /**
     * 下载的apk文件缓存目录
     */
    String mApkCacheDir;
    //========全局更新实现接口==========//
    /**
     * 版本更新网络请求服务API
     */
    IUpdateHttpService mUpdateHttpService;
    /**
     * 版本更新检查器【有默认】
     */
    IUpdateChecker mUpdateChecker;
    /**
     * 版本更新解析器【有默认】
     */
    IUpdateParser mUpdateParser;
    /**
     * 版本更新提示器【有默认】
     */
    IUpdatePrompter mUpdatePrompter;
    /**
     * 版本更新下载器【有默认】
     */
    IUpdateDownloader mUpdateDownloader;
    /**
     * 文件加密器【有默认】
     */
    IFileEncryptor mFileEncryptor;
    /**
     * APK安装监听【有默认】
     */
    OnInstallListener mOnInstallListener;
    /**
     * 更新出错监听【有默认】
     */
    OnUpdateFailureListener mOnUpdateFailureListener;

    //===========================初始化===================================//

    private AppSpace() {
        mIsGet = false;
        mIsWifiOnly = true;
        mIsAutoMode = false;

        mUpdateChecker = new DefaultUpdateChecker();
        mUpdateParser = new DefaultUpdateParser();
        mUpdateDownloader = new DefaultUpdateDownloader();
        mUpdatePrompter = new DefaultUpdatePrompter();
        mFileEncryptor = new DefaultFileEncryptor();
        mOnInstallListener = new DefaultInstallListener();
        mOnUpdateFailureListener = new DefaultUpdateFailureListener();
    }

    /**
     * 获取版本更新的入口
     *
     * @return 版本更新的入口
     */
    public static AppSpace get() {
        if (sInstance == null) {
            synchronized (AppSpace.class) {
                if (sInstance == null) {
                    sInstance = new AppSpace();
                }
            }
        }
        return sInstance;
    }

    /**
     * 初始化
     *
     * @param application 应用上下文
     */
    public void init(Application application) {
        mContext = application;
        UpdateError.init(mContext);
    }

    private Application getApplication() {
        testInitialize();
        return mContext;
    }

    private void testInitialize() {
        if (mContext == null) {
            throw new ExceptionInInitializerError("请先在全局Application中调用 XUpdate.get().init() 初始化！");
        }
    }

    public static Context getContext() {
        return get().getApplication();
    }

    //===========================对外版本更新api===================================//

    /**
     * 获取版本更新构建者
     *
     * @param context 上下文
     * @return 版本更新构建者
     */
    public static UpdateManager.Builder newBuild(@NonNull Context context) {
        return new UpdateManager.Builder(context);
    }

    /**
     * 获取版本更新构建者
     *
     * @param context   上下文
     * @param updateUrl 版本更新检查的地址
     * @return 版本更新构建者
     */
    public static UpdateManager.Builder newBuild(@NonNull Context context, String updateUrl) {
        return new UpdateManager.Builder(context)
                .updateUrl(updateUrl);
    }

    //===========================属性设置===================================//

    /**
     * 设置全局的apk更新请求参数
     *
     * @param key   键
     * @param value 值
     * @return this
     */
    public AppSpace param(@NonNull String key, @NonNull Object value) {
        if (mParams == null) {
            mParams = new TreeMap<>();
        }
        UpdateLog.d("设置全局参数, key:" + key + ", value:" + value.toString());
        mParams.put(key, value);
        return this;
    }

    /**
     * 设置全局的apk更新请求参数
     *
     * @param params apk更新请求参数
     * @return this
     */
    public AppSpace params(@NonNull Map<String, Object> params) {
        logForParams(params);
        mParams = params;
        return this;
    }

    private void logForParams(@NonNull Map<String, Object> params) {
        StringBuilder sb = new StringBuilder("设置全局参数:{\n");
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            sb.append("key = ")
                    .append(entry.getKey())
                    .append(", value = ")
                    .append(entry.getValue().toString())
                    .append("\n");
        }
        sb.append("}");
        UpdateLog.d(sb.toString());
    }


    /**
     * 设置全局版本更新网络请求服务API
     *
     * @param updateHttpService 版本更新网络请求服务API
     * @return this
     */
    public AppSpace setIUpdateHttpService(@NonNull IUpdateHttpService updateHttpService) {
        UpdateLog.d("设置全局更新网络请求服务:" + updateHttpService.getClass().getCanonicalName());
        mUpdateHttpService = updateHttpService;
        return this;
    }

    /**
     * 设置全局版本更新检查
     *
     * @param updateChecker 版本更新检查器
     * @return this
     */
    public AppSpace setIUpdateChecker(@NonNull IUpdateChecker updateChecker) {
        mUpdateChecker = updateChecker;
        return this;
    }

    /**
     * 设置全局版本更新的解析器
     *
     * @param updateParser 版本更新的解析器
     * @return this
     */
    public AppSpace setIUpdateParser(@NonNull IUpdateParser updateParser) {
        mUpdateParser = updateParser;
        return this;
    }

    /**
     * 设置全局版本更新提示器
     *
     * @param updatePrompter 版本更新提示器
     * @return this
     */
    public AppSpace setIUpdatePrompter(IUpdatePrompter updatePrompter) {
        mUpdatePrompter = updatePrompter;
        return this;
    }

    /**
     * 设置全局版本更新下载器
     *
     * @param updateDownLoader 版本更新下载器
     * @return this
     */
    public AppSpace setIUpdateDownLoader(@NonNull IUpdateDownloader updateDownLoader) {
        mUpdateDownloader = updateDownLoader;
        return this;
    }

    /**
     * 设置是否使用的是Get请求
     *
     * @param isGet 是否使用的是Get请求
     * @return this
     */
    public AppSpace isGet(boolean isGet) {
        UpdateLog.d("设置全局是否使用的是Get请求:" + isGet);
        mIsGet = isGet;
        return this;
    }

    /**
     * 设置是否只在wifi下进行版本更新检查
     *
     * @param isWifiOnly 是否只在wifi下进行版本更新检查
     * @return this
     */
    public AppSpace isWifiOnly(boolean isWifiOnly) {
        UpdateLog.d("设置全局是否只在wifi下进行版本更新检查:" + isWifiOnly);
        mIsWifiOnly = isWifiOnly;
        return this;
    }

    /**
     * 设置是否是自动版本更新模式【无人干预,有版本更新直接下载、安装】
     *
     * @param isAutoMode 是否是自动版本更新模式
     * @return this
     */
    public AppSpace isAutoMode(boolean isAutoMode) {
        UpdateLog.d("设置全局是否是自动版本更新模式:" + isAutoMode);
        mIsAutoMode = isAutoMode;
        return this;
    }

    /**
     * 设置是否是增量模式模式
     *
     * @param isPatchMode 是否是增量模式
     * @return this
     */
    public AppSpace isPatchMode(boolean isPatchMode) {
        UpdateLog.d("设置全局是否是自动版本更新模式:" + isPatchMode);
        mIsPatchMode = isPatchMode;
        return this;
    }

    /**
     * 设置apk的缓存路径
     *
     * @param apkCacheDir apk的缓存路径
     * @return this
     */
    public AppSpace setApkCacheDir(String apkCacheDir) {
        UpdateLog.d("设置全局apk的缓存路径:" + apkCacheDir);
        mApkCacheDir = apkCacheDir;
        return this;
    }

    /**
     * 设置是否支持静默安装
     *
     * @param supportSilentInstall 是否支持静默安装
     * @return this
     */
    public AppSpace supportSilentInstall(boolean supportSilentInstall) {
        ApkUtils.setSupportSilentInstall(supportSilentInstall);
        return this;
    }

    /**
     * 设置是否是debug模式
     *
     * @param isDebug 是否是debug模式
     * @return this
     */
    public AppSpace debug(boolean isDebug) {
        UpdateLog.debug(isDebug);
        return this;
    }

    /**
     * 设置日志打印接口
     *
     * @param logger 日志打印接口
     * @return this
     */
    public AppSpace setILogger(@NonNull ILogger logger) {
        UpdateLog.setLogger(logger);
        return this;
    }

    //===========================apk安装监听===================================//


    /**
     * 设置文件加密器
     *
     * @param fileEncryptor 文件加密器
     * @return this
     */
    public AppSpace setIFileEncryptor(IFileEncryptor fileEncryptor) {
        mFileEncryptor = fileEncryptor;
        return this;
    }

    /**
     * 设置安装监听
     *
     * @param onInstallListener 安装监听
     * @return this
     */
    public AppSpace setOnInstallListener(OnInstallListener onInstallListener) {
        mOnInstallListener = onInstallListener;
        return this;
    }

    //===========================更新出错===================================//

    /**
     * 设置更新出错的监听
     *
     * @param onUpdateFailureListener 更新出错的监听
     * @return this
     */
    public AppSpace setOnUpdateFailureListener(@NonNull OnUpdateFailureListener onUpdateFailureListener) {
        mOnUpdateFailureListener = onUpdateFailureListener;
        return this;
    }


}
