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

package com.appspa.demo.utils;

import android.content.Context;

import com.appspa.demo.R;

/**
 * @author treexi
 * @since 2018/7/30 上午11:48
 */
public class SettingSPUtils  {

    private static SettingSPUtils sInstance;

    private SettingSPUtils() {

    }

    /**
     * 获取实例
     * @return
     */
    public static SettingSPUtils get() {
        if (sInstance == null) {
            synchronized (SettingSPUtils.class) {
                if (sInstance == null) {
                    sInstance = new SettingSPUtils();
                }
            }
        }
        return sInstance;
    }


    /**
     * 获取服务器地址
     *
     * @return
     */
    public String getServiceURL() {
        return "";
//        return getString(getString(R.string.service_api_key), getString(R.string.default_service_api));
    }

    /**
     * 获取服务器地址
     *
     * @return
     */
    public boolean setServiceURL(String apiUrl) {
        return true;
//        return putString(getString(R.string.service_api_key), apiUrl);
    }


}
