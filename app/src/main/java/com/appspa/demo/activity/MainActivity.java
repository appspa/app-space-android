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

package com.appspa.demo.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.appspa.demo.Constants;
import com.appspa.demo.R;
import com.appspa.demo.custom.CustomUpdateParser;
import com.appspa.demo.utils.NotifyUtils;
import com.appspa.demo.utils.SettingSPUtils;
import com.appspa.update.AppSpace;

import java.util.List;

import okhttp3.HttpUrl;

public class MainActivity extends Activity implements View.OnClickListener{

    private EditText mEtServiceUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!NotifyUtils.isNotifyPermissionOpen(this)) {
            new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setMessage("通知权限未打开，是否前去打开？")
                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface d, int w) {
                            NotifyUtils.openNotifyPermissionSetting(MainActivity.this);
                        }
                    })
                    .setNegativeButton("否", null)
                    .show();
        }
        initView();
    }
    void initView(){
        mEtServiceUrl = findViewById(R.id.et_service_url);
        mEtServiceUrl.setText(Constants.CUSTOM_UPDATE_URL);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_save:
                String url = mEtServiceUrl.getText().toString().trim();
                if (parseBaseUrl(url)) {
                    SettingSPUtils.get().setServiceURL(url);
                }
                break;
            case R.id.btn_update:
                AppSpace.newBuild(this)
                        .updateUrl(Constants.CUSTOM_UPDATE_URL)
                        .updateParser(new CustomUpdateParser())
                        .update();
                break;
            case R.id.btn_auto_update:
                AppSpace.newBuild(this)
                        .isGet(false)
//                        .updateUrl(XUpdateServiceParser.getVersionCheckUrl())
//                        .updateParser(new XUpdateServiceParser())
                        //如果需要完全无人干预，自动更新，需要root权限【静默安装需要】
                        .isAutoMode(true)
                        .update();
                break;
            case R.id.btn_force_update:
                AppSpace.newBuild(this)
                        .isGet(false)
                        .param("appKey", "test3")
//                        .updateUrl(XUpdateServiceParser.getVersionCheckUrl())
//                        .updateParser(new XUpdateServiceParser())
                        .update();
                break;
            default:
                break;
        }
    }


    /**
     * 解析baseUrl
     *
     * @param baseUrl
     * @return true: 设置baseUrl成功
     */
    public static boolean parseBaseUrl(String baseUrl) {
        if (!TextUtils.isEmpty(baseUrl)) {
            HttpUrl httpUrl = HttpUrl.parse(baseUrl);
            if (httpUrl != null) {
                List<String> pathSegments = httpUrl.pathSegments();
                return "".equals(pathSegments.get(pathSegments.size() - 1));
            }
        }
        return false;
    }

}
