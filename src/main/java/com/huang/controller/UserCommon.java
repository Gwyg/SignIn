package com.huang.controller;

import com.huang.crawler.SignIn;
import com.huang.domain.User;
import com.huang.mapper.UserMapper;
import com.huang.utils.AESUtil;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;

@Slf4j
@Component
@NoArgsConstructor
//@RequiredArgsConstructor
public class UserCommon {
    @Autowired
    private SignIn signIn;
    @Autowired
    UserMapper userMapper;
    // 键为统一身份认证码，值为密码
    public static HashMap<String, String> userMap = new HashMap<>();
    // 键为统一身份认证码，值为session
    public static HashMap<String, String> sessionMap = new HashMap<>();
    // 键为统一身份认证码，值为姓名
    public static HashMap<String, String> usernameMap = new HashMap<>();

    @PostConstruct
    public void init() {
        log.info("UserCommon正在初始化...");


        //初始化。获取每个用户的 session ，每周三会刷新 session
        userMap.forEach((k, v) -> {
            log.info("正在获取用户：{}，{}的session", usernameMap.get(k), k);
            String session = "";
            try {
                session = signIn.getsession(k, v);
            } catch (Exception ignored) {
                log.info("获取session失败");
            }
            if (!"".equals(session)) {
                if (session.startsWith("V2")) {
                    log.info("用户：{}的初始session：{}", k, session);
                    sessionMap.put(k, session);
                } else {
                    log.info("获取session失败：{}", session);
                }
            }
        });

    }

}
