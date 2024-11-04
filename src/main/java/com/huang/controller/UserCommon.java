package com.huang.controller;

import com.huang.crawler.SignIn;
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
    // 键为统一身份认证码，值为密码
    public static HashMap<String, String> userMap = new HashMap<>();
    // 键为统一身份认证码，值为session
    public static HashMap<String, String> sessionMap = new HashMap<>();
    // 键为统一身份认证码，值为姓名
    public static HashMap<String, String> usernameMap = new HashMap<>();

    @PostConstruct
    public void init() {
        log.info("UserCommon正在初始化...");
        // 保存的用户
        userMap.put("1687256", "yh351248");
        usernameMap.put("1687256", "超级无敌大帅逼");    // 黄渝淮
//        usernameMap.put("1687265","超级无敌大帅逼");
        userMap.put("1689092", "HHeLiBeBCNOFNe6");
        usernameMap.put("1689092", "陈熙阳小虾米");  // 陈熙阳
        userMap.put("1680097", "Chb20040728");
        usernameMap.put("1680097", "陈海彬");           // 陈海彬
        userMap.put("1689133", "71304leiylj");
        usernameMap.put("1689133", "雷超");           // 雷超
        userMap.put("1688438", "Yinxu618753");
        usernameMap.put("1688438", "殷旭");            // 殷旭
        userMap.put("1688325", "Lcx615740618.");   // 刘成兴
        usernameMap.put("1688325", "刘大卷");
        userMap.put("1679611", "tanghuayanglex@gmail.com");// 唐华阳
        usernameMap.put("1679611", "唐华阳");
        userMap.put("1688704", "rlyshr0928.");       //冉慧
        usernameMap.put("1688704", "冉慧");
        userMap.put("1686965", "1104850836L");       //刘雨
        usernameMap.put("1686965", "刘雨");
        userMap.put("1689101", "guoli19740408");      //徐航
        usernameMap.put("1689101", "徐航");
        userMap.put("1686989", "18223397910hzy");    //胡左眼
        usernameMap.put("1686989", "胡左眼");
        userMap.put("1680098", "2433232+-");         //马金鹏
        usernameMap.put("1680098", "马金鹏");
        userMap.put("1687463", "lzm251225");          //李志美
        usernameMap.put("1687463", "荔枝");
        userMap.put("1686509", "zxc3240858086");         //蒋建华
        usernameMap.put("1686509", "蒋建华");
        userMap.put("1688324", "longxu200574");         //龙旭
        usernameMap.put("1688324", "龙旭");
        userMap.put("1680000","3785412Thy@#");    //仝昊宇
        usernameMap.put("1680000","thy");

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
