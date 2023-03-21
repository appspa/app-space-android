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

package com.appspace.xupdatedemo.custom;

import android.app.AlertDialog;
import android.content.DialogInterface;

import androidx.annotation.NonNull;

import com.appspace.update.entity.UpdateEntity;
import com.appspace.update.service.OnFileDownloadListener;
import com.appspace.update.entity.PromptEntity;
import com.appspace.update.proxy.IUpdatePrompter;
import com.appspace.update.proxy.IUpdateProxy;
import com.appspace.update.utils.UpdateUtils;
import com.appspace.xupdatedemo.utils.HProgressDialogUtils;

import java.io.File;

/**
 * 自定义版本更新提示器
 *
 * @author treexi
 * @since 2018/7/12 下午3:48
 */
public class CustomUpdatePrompter implements IUpdatePrompter {

    /**
     * 显示自定义提示
     *
     * @param updateEntity
     * @param updateProxy
     */
    private void showUpdatePrompt(final @NonNull UpdateEntity updateEntity, final @NonNull IUpdateProxy updateProxy) {
        String updateInfo = UpdateUtils.getDisplayUpdateInfo(updateProxy.getContext(), updateEntity);

        AlertDialog.Builder builder = new AlertDialog.Builder(updateProxy.getContext())
                .setTitle(String.format("是否升级到%s版本？", updateEntity.getVersionName()))
                .setMessage(updateInfo)
                .setPositiveButton("升级", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateProxy.startDownload(updateEntity, new OnFileDownloadListener() {
                            @Override
                            public void onStart() {
                                HProgressDialogUtils.showHorizontalProgressDialog(updateProxy.getContext(), "下载进度", false);
                            }

                            @Override
                            public void onProgress(float progress, long total) {
                                HProgressDialogUtils.setProgress(Math.round(progress * 100));
                            }

                            @Override
                            public boolean onCompleted(File file) {
                                HProgressDialogUtils.cancel();
                                return true;
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                HProgressDialogUtils.cancel();
                            }
                        });
                    }
                });
        if (updateEntity.isIgnorable()) {
            builder.setNegativeButton("暂不升级", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    UpdateUtils.saveIgnoreVersion(updateProxy.getContext(), updateEntity.getVersionName());
                }
            }).setCancelable(true);
        } else  {
            builder.setCancelable(false);
        }
        builder.create().show();
    }

    /**
     * 显示版本更新提示
     *
     * @param updateEntity 更新信息
     * @param updateProxy  更新代理
     * @param promptEntity 提示界面参数
     */
    @Override
    public void showPrompt(@NonNull UpdateEntity updateEntity, @NonNull IUpdateProxy updateProxy, @NonNull PromptEntity promptEntity) {
        showUpdatePrompt(updateEntity, updateProxy);
    }
}
