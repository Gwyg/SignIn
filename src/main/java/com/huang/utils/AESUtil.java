package com.huang.utils;


import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class AESUtil {

    private static String key = "NznPDCHAp+SDfndw6OvT0g==";

    public static String encrypt(String data) {
        AES aes = new AES(Base64.decode(key));
        byte[] encrypt = aes.encrypt(data.getBytes());
        return Base64.encode(encrypt);
    }

    public static String decrypt(String data) {
        AES aes = new AES(Base64.decode(key));
        byte[] decrypt = aes.decrypt(Base64.decode(data));
        return new String(decrypt);
    }

    public static String decrypt(String data, String key) {
        AES aes = SecureUtil.aes(Base64.decode(key));
        byte[] decrypt = aes.decrypt(Base64.decode(data));
        return new String(decrypt);
    }

}


