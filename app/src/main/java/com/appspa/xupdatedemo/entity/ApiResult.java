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

package com.appspa.xupdatedemo.entity;

import com.google.gson.annotations.SerializedName;

/**
 * 提供的默认的标注返回api
 *
 * @author treexi
 * @since 2018/5/22 下午4:22
 */
public class ApiResult<T> {
    public final static String CODE = "Code";
    public final static String MSG = "Msg";
    public final static String DATA = "Data";

    @SerializedName(value = CODE, alternate = {"code"})
    private int Code;
    @SerializedName(value = MSG, alternate = {"msg"})
    private String Msg;
    @SerializedName(value = DATA, alternate = {"data"})
    private T Data;

    public int getCode() {
        return Code;
    }

    public ApiResult setCode(int code) {
        Code = code;
        return this;
    }

    public String getMsg() {
        return Msg;
    }

    public ApiResult setMsg(String msg) {
        Msg = msg;
        return this;
    }

    public T getData() {
        return Data;
    }

    public ApiResult setData(T data) {
        Data = data;
        return this;
    }

    @Override
    public String toString() {
        return "ApiResult{" +
                "Code='" + Code + '\'' +
                ", Msg='" + Msg + '\'' +
                ", Data=" + Data +
                '}';
    }
}
