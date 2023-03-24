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

package com.appspa.update.listener.impl;

import android.content.Context;

import androidx.annotation.NonNull;

import com.appspa.update._AppSpace;
import com.appspa.update.entity.DownloadEntity;
import com.appspa.update.entity.UpdateError;
import com.appspa.update.listener.OnInstallListener;
import com.appspa.update.utils.ApkUtils;

import java.io.File;
import java.io.IOException;

/**
 * 默认的apk安装监听【自定义安装监听可继承该类，并重写相应的方法】
 *
 * @author treexi
 * @since 2018/7/1 下午11:58
 */
public class DefaultInstallListener implements OnInstallListener {

    @Override
    public boolean onInstallApk(@NonNull Context context, @NonNull File apkFile, @NonNull DownloadEntity downloadEntity) {
        if (checkApkFile(downloadEntity, apkFile)) {
            return installApkFile(context, apkFile);
        } else {
            _AppSpace.onUpdateError(UpdateError.ERROR.INSTALL_FAILED, "Apk file verify failed, please check whether the MD5 value you set is correct！");
            return false;
        }
    }

    /**
     * 检验apk文件的有效性（默认是使用MD5进行校验,可重写该方法）
     *
     * @param downloadEntity 下载信息实体
     * @param apkFile        apk文件
     * @return apk文件是否有效
     */
    protected boolean checkApkFile(DownloadEntity downloadEntity, @NonNull File apkFile) {
        return downloadEntity != null && _AppSpace.isFileValid(downloadEntity.getWholeMd5(), apkFile);
    }

    /**
     * 安装apk文件【此处可自定义apk的安装方法,可重写该方法】
     *
     * @param context 上下文
     * @param apkFile apk文件
     * @return 是否安装成功
     */
    protected boolean installApkFile(Context context, File apkFile) {
        try {
            return ApkUtils.install(context, apkFile);
        } catch (IOException e) {
            _AppSpace.onUpdateError(UpdateError.ERROR.INSTALL_FAILED, "An error occurred while install apk:" + e.getMessage());
        }
        return false;
    }


    @Override
    public void onInstallApkSuccess() {

    }
}
