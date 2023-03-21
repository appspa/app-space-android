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

package com.appspace.xupdatedemo.entity;

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

        public boolean active;

        public String packageHash;

        public String updateMode;


        public int versionCode;

        public String versionName;

        public String changeLog;

        public String downloadUrl;

        public long size;

    }
}
