/*
 * Copyright (C) 2021 xuexiangjys(xuexiangjys@163.com)
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

package com.appspa.demo.fragment;

import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.base.XPageSimpleListFragment;
import com.appspa.update.AppSpace;
import com.appspa.demo.Constants;

import java.util.List;

/**
 * @author treexi
 * @since 2021/11/25 10:32 PM
 */
@Page(name = "基础使用")
public class BasicUseFragment extends XPageSimpleListFragment {

    @Override
    protected List<String> initSimpleData(List<String> lists) {
        lists.add("默认App更新");
        lists.add("默认App更新 + 支持后台更新");
        lists.add("版本更新(自动模式)");
        lists.add("强制版本更新");
        lists.add("可忽略版本更新");
        return lists;
    }

    @Override
    protected void onItemClick(int position) {
        switch (position) {
            case 0:
                AppSpace.newBuild(getActivity())
                        .updateUrl(Constants.DEFAULT_UPDATE_URL)
                        .update();
                break;
            case 1:
                AppSpace.newBuild(getActivity())
                        .updateUrl(Constants.DEFAULT_UPDATE_URL)
                        .supportBackgroundUpdate(true)
                        .update();
                break;
            case 2:
                AppSpace.newBuild(getActivity())
                        .updateUrl(Constants.DEFAULT_UPDATE_URL)
                        //如果需要完全无人干预，自动更新，需要root权限【静默安装需要】
                        .isAutoMode(true)
                        .update();
                break;
            case 3:
                AppSpace.newBuild(getActivity())
                        .updateUrl(Constants.FORCED_UPDATE_URL)
                        .update();
                break;
            case 4:
                AppSpace.newBuild(getActivity())
                        .updateUrl(Constants.IGNORE_UPDATE_URL)
                        .update();
                break;
            default:
                break;
        }
    }

}
