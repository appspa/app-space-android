/*
 * Copyright (C) 2020 xuexiangjys(xuexiangjys@163.com)
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
 *
 */

package com.appspa.update.widget;

import androidx.annotation.NonNull;

import com.appspa.update.service.OnFileDownloadListener;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * 弱引用文件下载监听, 解决内存泄漏问题
 *
 * @author treexi
 * @since 2020/11/15 10:58 PM
 */
public class WeakFileDownloadListener implements OnFileDownloadListener {

    private WeakReference<IDownloadEventHandler> mDownloadHandlerRef;

    public WeakFileDownloadListener(@NonNull IDownloadEventHandler handler) {
        mDownloadHandlerRef = new WeakReference<>(handler);
    }

    @Override
    public void onStart() {
        if (getEventHandler() != null) {
            getEventHandler().handleStart();
        }
    }

    @Override
    public void onProgress(float progress, long total) {
        if (getEventHandler() != null) {
            getEventHandler().handleProgress(progress);
        }
    }

    @Override
    public boolean onCompleted(File file) {
        if (getEventHandler() != null) {
            return getEventHandler().handleCompleted(file);
        } else {
            // 下载好了，返回true，自动进行apk安装
            return true;
        }
    }

    @Override
    public void onError(Throwable throwable) {
        if (getEventHandler() != null) {
            getEventHandler().handleError(throwable);
        }
    }

    private IDownloadEventHandler getEventHandler() {
        return mDownloadHandlerRef != null ? mDownloadHandlerRef.get() : null;
    }
}
