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

package com.appspa.demo.entity;

import java.io.Serializable;

/**
 * 自定义版本检查的结果
 *
 * @author treexi
 * @since 2018/7/11 上午1:03
 */
public class CustomResult implements Serializable {

    public boolean success;
    public VersionInfo data;


    public static class VersionInfo implements Serializable {

        //是否有更新
        public boolean hasUpdate;
        //是否强制更新
        public boolean isForce;
        //是否可忽略
        public boolean isIgnorable;
        //是否静默下载
        public boolean isSilent;
        //md5用户文件校验
        public String md5;
        //版本code
        public int versionCode;
        //版本名称
        public String versionName;
        //更新日志
        public String changeLog;
        //下载Url
        public String downloadUrl;
        //文件大小
        public long size;
        //增量包信息
        public PatchInfo patchInfo;

    }

    public static class PatchInfo implements Serializable {

        //md5用户文件校验
        public String md5;
        //版本code
        public int sVersionCode;
        //版本code
        public int tVersionCode;
        //说明
        public String tip;
        //下载Url
        public String downloadUrl;
        //文件大小
        public long size;

    }
}
