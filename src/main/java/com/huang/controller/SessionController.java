package com.huang.controller;


import com.huang.crawler.SignIn;
import com.huang.utils.AESUtil;
import com.huang.utils.RSAUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/signin")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class SessionController {

    private final SignIn signIn;

    /**
     * 刷新用户session的代码，有定时任务会每周执行
     */
    public void refreshSession(){
        UserCommon.userMap.forEach((key, value) -> {
            log.info("正在更新用户：{}的session",key);
            String session = signIn.getsession(key, value);
            if(session != null && session.startsWith("V2")){
                UserCommon.sessionMap.put(key, session);
            }
            log.info("用户：{}session更新完成：{}",key,session);
        });
    }

    /**
     * 测试接口
     * @return 测试字符串
     */
    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }

    /**
     * 签到接口
     * @param unicode 统一认证码
     * @return 签到情况
     * @throws Exception 运行过程中的异常
     */
    @GetMapping("/test")
    public String test(String unicode) throws Exception {
        if (unicode == null || unicode.isEmpty()){
            throw new Exception("统一认证码不能为空");
        }
        log.info("{}正在签到：{}",UserCommon.usernameMap.get(unicode),unicode);
        String session = UserCommon.sessionMap.get(unicode);
        if(session == null || !session.startsWith("V2")  || session.isEmpty()){
            log.info("账号session为空");
            String s = signIn.getsession(unicode, UserCommon.userMap.get(unicode));
            log.info("获取session：{}",s);
            if(!s.startsWith("V2")){
                return "用户不存在或账号密码错误";
            }
            UserCommon.sessionMap.put(unicode,s);
            session = s;
        }
        List<String> list = signIn.getAllClass(session);
        if(list == null || list.isEmpty()){
            return "没有签到任务";
        }
        String s = list.get(0);
        String[] split = s.split(",");
        String courseId = split[0];
        String rollCallId = split[1];
        String data = signIn.getData(courseId, rollCallId, session);
        String res = signIn.signIn(data, rollCallId, session);
        log.info("{} 签到成功",UserCommon.usernameMap.get(unicode));
        return res;
    }

    /**
     * 签到接口，进行加密
     * @param unicode 加密后的统一认证码
     * @param key 加密后的AES密钥
     * @return 签到的状态
     */
    @PostMapping("/safe")
    public String signIn(String unicode, String key) throws Exception {
        if (unicode == null || unicode.isEmpty()){
            throw new Exception("统一认证码不能为空");
        }
        //对统一认证码进行解密
        key = RSAUtil.decrypt(key);
        unicode = AESUtil.decrypt(unicode, key);
        log.info("{}正在签到：{}",UserCommon.usernameMap.get(unicode),unicode);
        String session = UserCommon.sessionMap.get(unicode);
        // session异常的情况，重新进行session的获取，三次尝试
        if(session == null || !session.startsWith("V2")){
            log.info("session为null，尝试重新获取session...");
            for (int i = 0; true; i++) {
                String s = signIn.getsession(unicode, UserCommon.userMap.get(unicode));
                if(s != null && s.startsWith("V2")){
                    session = s;
                    log.info("重新获取session成功！");
                    break;
                }
                if(i == 2){
                    throw new Exception("账号不存在，请重新输入");
                }
            }
        }
        // 获取签到列表，为空则返回没有签到任务
        List<String> list = signIn.getAllClass(session);
        if (list == null || list.isEmpty()){
            throw new Exception("没有签到任务");
        }
        String s = list.get(0);
        String[] split = s.split(",");
        String courseId = split[0];
        String rollCallId = split[1];
        // 获取签到接口的请求体数据
        String data = signIn.getData(courseId, rollCallId, session);
        String res = signIn.signIn(data, rollCallId, session);
        log.info("{}签到成功",UserCommon.usernameMap.get(unicode));
        return res;
    }

    /**
     * 获取服务端的RSA密钥
     * @return RSA公钥
     */
    @GetMapping("/getRSAKey")
    public String getRSAKey(){
        return RSAUtil.publicKey;
    }

    @GetMapping("/number")
    public String number(String unicode) throws Exception {
        if (unicode == null || unicode.isEmpty()){
            throw new Exception("统一认证码不能为空");
        }
        log.info("{}正在签到：{}",UserCommon.usernameMap.get(unicode),unicode);
        String session = UserCommon.sessionMap.get(unicode);
        if(session == null || !session.startsWith("V2")  || session.isEmpty()){
            log.info("账号session为空");
            String s = signIn.getsession(unicode, UserCommon.userMap.get(unicode));
            log.info("获取session：{}",s);
            if(!s.startsWith("V2")){
                return "用户不存在或账号密码错误";
            }
            UserCommon.sessionMap.put(unicode,s);
            session = s;
        }
        List<String> list = signIn.getAllClass(session);
        if(list == null || list.isEmpty()){
            return "没有签到任务";
        }
        String s = list.get(0);
        String[] split = s.split(",");
        String courseId = split[0];
        String rollCallId = split[1];
        String data = signIn.getNumberData(courseId, rollCallId, session);
        String res = signIn.signInNumber(data, rollCallId, session);
        log.info("{} 签到成功",UserCommon.usernameMap.get(unicode));
        return res;
    }

}
