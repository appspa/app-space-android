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

package com.appspace.xupdatedemo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.appspace.update.AppSpace;
import com.appspace.xupdatedemo.Constants;
import com.xuexiang.xupdatedemo.R;

/**
 * @author treexi
 * @since 2018/7/24 上午10:38
 */
public class UpdateActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_update:
                AppSpace.newBuild(this)
                        .updateUrl(Constants.DEFAULT_UPDATE_URL)
                        .update();
                break;
            case R.id.btn_support_background_update:
                AppSpace.newBuild(this)
                        .updateUrl(Constants.DEFAULT_UPDATE_URL)
                        .promptWidthRatio(0.7F)
                        .supportBackgroundUpdate(true)
                        .update();
                break;
            case R.id.btn_auto_update:
                AppSpace.newBuild(this)
                        .updateUrl(Constants.DEFAULT_UPDATE_URL)
                        //如果需要完全无人干预，自动更新，需要root权限【静默安装需要】
                        .isAutoMode(true)
                        .update();
                break;
            case R.id.btn_force_update:
                AppSpace.newBuild(this)
                        .updateUrl(Constants.FORCED_UPDATE_URL)
                        .update();
                break;
            default:
                break;
        }
    }
}
