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

package com.appspa.update.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.appspa.update._AppSpace;

import java.io.File;

/**
 * 下载信息实体
 *
 * @author treexi
 * @since 2018/7/9 上午11:41
 */
public class DownloadEntity implements Parcelable {
    /**
     * 下载地址
     */
    private String mDownloadUrl;

    /**
     * 下载文件的加密值，用于校验，防止下载的apk/patch文件被替换【当然你也可以不使用MD5加密】
     */
    private String mMd5;
    /**
     * 目标版本
     */
    private String mWholeMd5;
    /**
     * 下载文件的大小【单位：KB】
     */
    private long mSize;
    //==========================//
    /**
     * 是否在通知栏上显示下载进度
     */
    private boolean mIsShowNotification;
    /**
     * 增量包说明
     */
    private String mTip;

    /**
     * 增量包当前版本
     */
    private int sVersionCode;
    /**
     * 增量包目标版本
     */
    private int tVersionCode;

    private boolean isPatch;

    public DownloadEntity() {

    }

    protected DownloadEntity(Parcel in) {
        mDownloadUrl = in.readString();
        mMd5 = in.readString();
        mWholeMd5 = in.readString();
        mSize = in.readLong();
        mIsShowNotification = in.readByte() != 0;
        mTip = in.readString();
        sVersionCode = in.readInt();
        tVersionCode = in.readInt();
        isPatch =  in.readByte() != 0;
    }

    public static final Creator<DownloadEntity> CREATOR = new Creator<DownloadEntity>() {
        @Override
        public DownloadEntity createFromParcel(Parcel in) {
            return new DownloadEntity(in);
        }

        @Override
        public DownloadEntity[] newArray(int size) {
            return new DownloadEntity[size];
        }
    };

    public String getDownloadUrl() {
        return mDownloadUrl;
    }

    public DownloadEntity setDownloadUrl(String downloadUrl) {
        mDownloadUrl = downloadUrl;
        return this;
    }
    public String getMd5() {
        return mMd5;
    }

    public DownloadEntity setMd5(String md5) {
        mMd5 = md5;
        return this;
    }

    public String getWholeMd5() {
        return mWholeMd5;
    }

    public DownloadEntity setWholeMd5(String md5) {
        mWholeMd5 = md5;
        return this;
    }
    public long getSize() {
        return mSize;
    }

    public DownloadEntity setSize(long size) {
        mSize = size;
        return this;
    }

    public boolean isShowNotification() {
        return mIsShowNotification;
    }

    public DownloadEntity setShowNotification(boolean showNotification) {
        mIsShowNotification = showNotification;
        return this;
    }

    public DownloadEntity setTip(String mTip) {
        this.mTip = mTip;
        return this;
    }

    public DownloadEntity setSVersionCode(int sVersionCode) {
        this.sVersionCode = sVersionCode;
        return this;
    }

    public DownloadEntity setTVersionCode(int tVersionCode) {
        this.tVersionCode = tVersionCode;
        return this;
    }

    public DownloadEntity setIsPatch(boolean isPatch) {
        this.isPatch = isPatch;
        return this;
    }


    /**
     * 验证文件是否有效【没设置mMd5默认不校验，直接有效】
     *
     * @param file 需要校验的文件
     * @return 文件是否有效
     */
    public boolean isFileValid(File file) {
        return _AppSpace.isFileValid(mMd5, file);
    }

    public boolean isPatch(){
        return isPatch;
    }


    @Override
    public String toString() {
        return "DownloadEntity{" +
                "mDownloadUrl='" + mDownloadUrl + '\'' +
                ", mMd5='" + mMd5 + '\'' +
                ", mTMd5='" + mWholeMd5 + '\'' +
                ", mSize=" + mSize + '\'' +
                ", mIsShowNotification=" + mIsShowNotification +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mDownloadUrl);
        dest.writeString(mMd5);
        dest.writeString(mWholeMd5);
        dest.writeLong(mSize);
        dest.writeByte((byte) (mIsShowNotification ? 1 : 0));
        dest.writeString(mTip);
        dest.writeInt(sVersionCode);
        dest.writeInt(tVersionCode);
        dest.writeByte((byte) (isPatch ? 1 : 0));
    }
}
