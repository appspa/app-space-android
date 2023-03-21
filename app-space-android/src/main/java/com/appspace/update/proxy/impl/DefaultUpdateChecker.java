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

package com.appspace.update.proxy.impl;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.appspace.update.entity.UpdateEntity;
import com.appspace.update.entity.UpdateError;
import com.appspace.update.listener.IUpdateParseCallback;
import com.appspace.update.proxy.IUpdateChecker;
import com.appspace.update.proxy.IUpdateHttpService;
import com.appspace.update.proxy.IUpdateProxy;
import com.appspace.update.service.DownloadService;
import com.appspace.update.utils.UpdateUtils;
import com.appspace.update._AppSpace;

import java.util.Map;

/**
 * 默认版本更新检查者
 *
 * @author treexi
 * @since 2018/7/2 下午10:21
 */
public class DefaultUpdateChecker implements IUpdateChecker {

    @Override
    public void onBeforeCheck() {

    }

    @Override
    public void checkVersion(boolean isGet, @NonNull final String url, @NonNull Map<String, Object> params, final @NonNull IUpdateProxy updateProxy) {
        if (DownloadService.isRunning() || _AppSpace.getCheckUrlStatus(url) || _AppSpace.isPrompterShow(url)) {
            updateProxy.onAfterCheck();
            _AppSpace.onUpdateError(UpdateError.ERROR.CHECK_UPDATING);
            return;
        }

        _AppSpace.setCheckUrlStatus(url, true);

        if (isGet) {
            updateProxy.getIUpdateHttpService().asyncGet(url, params, new IUpdateHttpService.Callback() {
                @Override
                public void onSuccess(String result) {
                    onCheckSuccess(url, result, updateProxy);
                }

                @Override
                public void onError(Throwable error) {
                    onCheckError(url, updateProxy, error);
                }
            });
        } else {
            updateProxy.getIUpdateHttpService().asyncPost(url, params, new IUpdateHttpService.Callback() {
                @Override
                public void onSuccess(String result) {
                    onCheckSuccess(url, result, updateProxy);
                }

                @Override
                public void onError(Throwable error) {
                    onCheckError(url, updateProxy, error);
                }
            });
        }
    }

    @Override
    public void onAfterCheck() {

    }

    /**
     * 查询成功
     *
     * @param url         查询地址
     * @param result      查询结果
     * @param updateProxy 更新代理
     */
    private void onCheckSuccess(String url, String result, @NonNull IUpdateProxy updateProxy) {
        _AppSpace.setCheckUrlStatus(url, false);
        updateProxy.onAfterCheck();
        if (!TextUtils.isEmpty(result)) {
            processCheckResult(result, updateProxy);
        } else {
            _AppSpace.onUpdateError(UpdateError.ERROR.CHECK_JSON_EMPTY);
        }
    }

    /**
     * 查询失败
     *
     * @param url         查询地址
     * @param updateProxy 更新代理
     * @param error       错误
     */
    private void onCheckError(String url, @NonNull IUpdateProxy updateProxy, Throwable error) {
        _AppSpace.setCheckUrlStatus(url, false);
        updateProxy.onAfterCheck();
        _AppSpace.onUpdateError(UpdateError.ERROR.CHECK_NET_REQUEST, error.getMessage());
    }

    @Override
    public void processCheckResult(final @NonNull String result, final @NonNull IUpdateProxy updateProxy) {
        try {
            if (updateProxy.isAsyncParser()) {
                //异步解析
                updateProxy.parseJson(result, new IUpdateParseCallback() {
                    @Override
                    public void onParseResult(UpdateEntity updateEntity) {
                        try {
                            UpdateUtils.processUpdateEntity(updateEntity, result, updateProxy);
                        } catch (Exception e) {
                            e.printStackTrace();
                            _AppSpace.onUpdateError(UpdateError.ERROR.CHECK_PARSE, e.getMessage());
                        }
                    }
                });
            } else {
                //同步解析
                UpdateUtils.processUpdateEntity(updateProxy.parseJson(result), result, updateProxy);
            }
        } catch (Exception e) {
            e.printStackTrace();
            _AppSpace.onUpdateError(UpdateError.ERROR.CHECK_PARSE, e.getMessage());
        }
    }

    @Override
    public void noNewVersion(Throwable throwable) {
        _AppSpace.onUpdateError(UpdateError.ERROR.CHECK_NO_NEW_VERSION, throwable != null ? throwable.getMessage() : null);
    }
}
