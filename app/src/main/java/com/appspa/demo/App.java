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

package com.appspa.demo;

import android.app.Application;
import android.widget.Toast;

import com.appspa.update.AppSpace;
import com.appspa.update.entity.UpdateError;
import com.appspa.update.listener.OnUpdateFailureListener;
import com.appspa.update.utils.UpdateUtils;
import com.appspa.demo.http.OKHttpUpdateHttpService;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * @author treexi
 * @since 2018/7/9 下午2:15
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initOKHttpUtils();
        initUpdate();
    }

    private void initUpdate() {
        AppSpace.get()
                .debug(true)
                //默认设置只在wifi下检查版本更新
                .isWifiOnly(false)
                //默认设置使用get请求检查版本
                .isGet(true)
                //默认设置非自动模式，可根据具体使用配置
                .isAutoMode(false)
                //设置默认公共请求参数
                .param("currentMd5", UpdateUtils.getBaseApkMd5(this))//差量更新使用
                .param("currentVersionCode", UpdateUtils.getVersionCode(this))
                .param("appId", "641d82026c4a440df47b6be7")
//                .param("appKey", getPackageName())
                //设置版本更新出错的监听
                .setOnUpdateFailureListener(new OnUpdateFailureListener() {
                    @Override
                    public void onFailure(com.appspa.update.entity.UpdateError error) {
                        error.printStackTrace();
                        //对不同错误进行处理
                        if (error.getCode() != UpdateError.ERROR.CHECK_NO_NEW_VERSION) {
                            Toast.makeText(App.this, error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                //设置是否支持静默安装，默认是true
                .supportSilentInstall(false)
                //这个必须设置！实现网络请求功能。
                .setIUpdateHttpService(new OKHttpUpdateHttpService())
                //这个必须初始化
                .init(this);

    }


    private void initOKHttpUtils() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(20000L, TimeUnit.MILLISECONDS)
                .readTimeout(20000L, TimeUnit.MILLISECONDS)
                .build();
        OkHttpUtils.initClient(okHttpClient);
    }
}
