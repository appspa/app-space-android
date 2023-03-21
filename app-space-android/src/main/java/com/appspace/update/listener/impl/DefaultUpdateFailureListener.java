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

package com.appspace.update.listener.impl;

import com.appspace.update.entity.UpdateError;
import com.appspace.update.listener.OnUpdateFailureListener;
import com.appspace.update.logs.UpdateLog;

/**
 * 默认的更新出错的处理(简单地打印日志）
 *
 * @author treexi
 * @since 2018/7/1 下午7:48
 */
public class DefaultUpdateFailureListener implements OnUpdateFailureListener {

    @Override
    public void onFailure(UpdateError error) {
        UpdateLog.e(error);
    }
}
