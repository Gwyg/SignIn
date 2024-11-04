package com.huang.utils;


import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
public class RSAUtil {
    private  static RSA rsa;
    //服务器的私钥
    private static String privateKey;
    //传给前端的公钥
    public static String publicKey;

    static {
        rsa = new RSA();
        privateKey = rsa.getPrivateKeyBase64();
        publicKey = rsa.getPublicKeyBase64();
    }

    /**
     * 刷新密钥对的方法，会有定时任务来执行
     */
    public static void refreshKey() {
        rsa = new RSA();
        privateKey = rsa.getPrivateKeyBase64();
        publicKey = rsa.getPublicKeyBase64();
    }

    /**
     * 使用rsa进行加密
     * @param content 加密前数据
     * @return 加密后的数据
     */
    public static String encrypt(String content) {
        byte[] encrypt = rsa.encrypt(content, KeyType.PublicKey);
        return Base64.encode(encrypt);
    }

    /**
     * 解密前端传入的加密数据的方法，
     * @param str 前端传入的加密数据 AES的加密密钥
     * @return 解密之后的数据，如果解密失败则返回 null
     */
    public static String decrypt(String str)  {
        byte[] decrypt = rsa.decrypt(str, KeyType.PrivateKey);
        return new String(decrypt);
    }

}
