package com.appspa.update.proxy.impl;

import android.text.TextUtils;

import com.appspa.update.utils.Md5Utils;
import com.appspa.update.proxy.IFileEncryptor;

import java.io.File;

/**
 * 默认的文件加密计算使用的是MD5加密
 *
 * @author treexi
 * @since 2019-09-06 14:21
 */
public class DefaultFileEncryptor implements IFileEncryptor {
    /**
     * 加密文件
     *
     * @param file
     * @return
     */
    @Override
    public String encryptFile(File file) {
        return Md5Utils.getFileMD5(file);
    }

    /**
     * 检验文件是否有效（加密是否一致）
     *
     * @param encrypt 加密值, 如果encrypt为空，直接认为是有效的
     * @param file    需要校验的文件
     * @return 文件是否有效
     */
    @Override
    public boolean isFileValid(String encrypt, File file) {
        return TextUtils.isEmpty(encrypt) || encrypt.equalsIgnoreCase(encryptFile(file));
    }

}
