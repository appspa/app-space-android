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

package com.appspa.demo.custom;

import androidx.annotation.NonNull;

import com.appspa.update.entity.DownloadEntity;
import com.appspa.update.entity.UpdateEntity;
import com.appspa.update.listener.IUpdateParseCallback;
import com.appspa.update.proxy.IUpdateParser;
import com.appspa.demo.entity.CustomResult;
import com.google.gson.Gson;

/**
 * 自定义更新解析器
 *
 * @author treexi
 * @since 2023/3/25 下午3:46
 */
public class CustomUpdateParser implements IUpdateParser {
    @Override
    public UpdateEntity parseJson(String json) throws Exception {
        return getParseResult(json);
    }

    private UpdateEntity getParseResult(String json) {
        CustomResult result = new Gson().fromJson(json, CustomResult.class);
        if (result != null && result.success && result.data != null) {
            CustomResult.VersionInfo data = result.data;
            DownloadEntity mPatchDownloadEntity = null;
            if (data.patchInfo != null && data.patchInfo.downloadUrl != null) {
                mPatchDownloadEntity = new DownloadEntity();
                mPatchDownloadEntity.setDownloadUrl(data.patchInfo.downloadUrl);
                mPatchDownloadEntity.setTip(data.patchInfo.tip);
                mPatchDownloadEntity.setSize(data.patchInfo.size);
                mPatchDownloadEntity.setMd5(data.patchInfo.md5);
                mPatchDownloadEntity.setWholeMd5(data.patchInfo.tMd5);
                mPatchDownloadEntity.setIsPatch(true);
            }
            DownloadEntity downloadEntity = new DownloadEntity();
            downloadEntity.setMd5(data.md5);
            downloadEntity.setDownloadUrl(data.downloadUrl);
            downloadEntity.setSize(data.size);
            return new UpdateEntity()
                    .setHasUpdate(true)
                    .setForce(data.isForce)
                    .setIsIgnorable(data.isIgnorable)
                    .setIsSilent(data.isSilent)
                    .setVersionCode(data.versionCode)
                    .setVersionName(data.versionName)
                    .setUpdateContent(data.changeLog)
                    .setDownLoadEntity(downloadEntity)
                    .setPatchDownloadEntity(mPatchDownloadEntity);
        }
        return new UpdateEntity().setHasUpdate(false);
    }

    @Override
    public void parseJson(String json, @NonNull IUpdateParseCallback callback) throws Exception {
        //当isAsyncParser为 true时调用该方法, 所以当isAsyncParser为false可以不实现
        callback.onParseResult(getParseResult(json));
    }


    @Override
    public boolean isAsyncParser() {
        return true;
    }
}
