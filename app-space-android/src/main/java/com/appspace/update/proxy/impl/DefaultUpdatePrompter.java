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

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.appspace.update.entity.PromptEntity;
import com.appspace.update.entity.UpdateEntity;
import com.appspace.update.widget.UpdateDialog;
import com.appspace.update.widget.UpdateDialogActivity;
import com.appspace.update.widget.UpdateDialogFragment;
import com.appspace.update.logs.UpdateLog;
import com.appspace.update.proxy.IPrompterProxy;
import com.appspace.update.proxy.IUpdatePrompter;
import com.appspace.update.proxy.IUpdateProxy;

/**
 * 默认的更新提示器
 *
 * @author treexi
 * @since 2018/7/2 下午4:05
 */
public class DefaultUpdatePrompter implements IUpdatePrompter {

    /**
     * 显示版本更新提示
     *
     * @param updateEntity 更新信息
     * @param updateProxy  更新代理
     * @param promptEntity 提示界面参数
     */
    @Override
    public void showPrompt(@NonNull UpdateEntity updateEntity, @NonNull IUpdateProxy updateProxy, @NonNull PromptEntity promptEntity) {
        Context context = updateProxy.getContext();
        if (context == null) {
            UpdateLog.e("showPrompt failed, context is null!");
            return;
        }
        beforeShowPrompt(updateEntity, promptEntity);
        UpdateLog.d("[DefaultUpdatePrompter] showPrompt, " + promptEntity);
        if (context instanceof FragmentActivity) {
            UpdateDialogFragment.show(((FragmentActivity) context).getSupportFragmentManager(), updateEntity, getPrompterProxy(updateProxy), promptEntity);
        } else if (context instanceof Activity) {
            UpdateDialog.newInstance(context, updateEntity, getPrompterProxy(updateProxy), promptEntity).show();
        } else {
            UpdateDialogActivity.show(context, updateEntity, getPrompterProxy(updateProxy), promptEntity);
        }
    }

    /**
     * 显示版本更新提示之前的处理【可自定义属于自己的显示逻辑】
     *
     * @param updateEntity 更新信息
     * @param promptEntity 提示界面参数
     */
    protected void beforeShowPrompt(@NonNull UpdateEntity updateEntity, @NonNull PromptEntity promptEntity) {
        // 如果是强制更新的话，默认设置是否忽略下载异常为true，保证即使是下载异常也不退出提示。
        if (updateEntity.isForce()) {
            promptEntity.setIgnoreDownloadError(true);
        }
    }

    /**
     * 构建版本更新提示器代理【可自定义属于自己的业务逻辑】
     *
     * @param updateProxy 版本更新代理
     * @return 版本更新提示器代理
     */
    protected IPrompterProxy getPrompterProxy(@NonNull IUpdateProxy updateProxy) {
        return new DefaultPrompterProxyImpl(updateProxy);
    }

}
