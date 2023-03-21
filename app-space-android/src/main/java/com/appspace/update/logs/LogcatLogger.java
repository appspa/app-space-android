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

package com.appspace.update.logs;

import androidx.annotation.NonNull;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 默认Logcat日志记录
 *
 * @author treexi
 * @since 2018/6/29 下午7:57
 */
public class LogcatLogger implements ILogger {

    /**
     * logcat里日志的最大长度.
     */
    private static final int MAX_LOG_LENGTH = 4000;

    /**
     * 打印信息
     *
     * @param priority 优先级
     * @param tag      标签
     * @param message   信息
     * @param t        出错信息
     */
    @Override
    public void log(int priority, String tag, String message, Throwable t) {
        if (message != null && message.length() == 0) {
            message = null;
        }
        if (message == null) {
            if (t == null) {
                return; // Swallow message if it's null and there's no throwable.
            }
            message = getStackTraceString(t);
        } else {
            if (t != null) {
                message += "\n" + getStackTraceString(t);
            }
        }

        log(priority, tag, message);
    }

    private String getStackTraceString(Throwable t) {
        // Don't replace this with Log.getStackTraceString() - it hides
        // UnknownHostException, which is not what we want.
        StringWriter sw = new StringWriter(256);
        PrintWriter pw = new PrintWriter(sw, false);
        t.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }


    /**
     * 使用LogCat输出日志，字符长度超过4000则自动换行.
     *
     * @param priority 优先级
     * @param tag      标签
     * @param message  信息
     */
    public void log(int priority, String tag, String message) {
        int subNum = message.length() / MAX_LOG_LENGTH;
        if (subNum > 0) {
            int index = 0;
            for (int i = 0; i < subNum; i++) {
                int lastIndex = index + MAX_LOG_LENGTH;
                String sub = message.substring(index, lastIndex);
                logSub(priority, tag, sub);
                index = lastIndex;
            }
            logSub(priority, tag, message.substring(index, message.length()));
        } else {
            logSub(priority, tag, message);
        }
    }


    /**
     * 使用LogCat输出日志.
     *
     * @param priority 优先级
     * @param tag      标签
     * @param sub      信息
     */
    private void logSub(int priority, @NonNull String tag, @NonNull String sub) {
        switch (priority) {
            case Log.VERBOSE:
                Log.v(tag, sub);
                break;
            case Log.DEBUG:
                Log.d(tag, sub);
                break;
            case Log.INFO:
                Log.i(tag, sub);
                break;
            case Log.WARN:
                Log.w(tag, sub);
                break;
            case Log.ERROR:
                Log.e(tag, sub);
                break;
            case Log.ASSERT:
                Log.wtf(tag, sub);
                break;
            default:
                Log.v(tag, sub);
                break;
        }
    }

}
