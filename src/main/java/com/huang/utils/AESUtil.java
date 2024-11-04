package com.huang.utils;


import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class AESUtil {

    public static String decrypt(String data, String key) {
        AES aes = SecureUtil.aes(Base64.decode(key));
        byte[] decrypt = aes.decrypt(Base64.decode(data));
        return new String(decrypt);
    }

}


