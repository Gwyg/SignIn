package com.huang.controller;


import com.huang.crawler.SignIn;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/signin")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class SessionController {

    private final SignIn signIn;

    @GetMapping("/getsession")
    public String getsession(@RequestParam(required = true)String unicode,
                             @RequestParam(required = true)String password){
        if(UserCommon.sessionMap.containsKey(unicode)){
            log.info("用户：{}请求session：{}",unicode,UserCommon.sessionMap.get(unicode));
            return UserCommon.sessionMap.get(unicode);
        }
        if(!UserCommon.userMap.containsKey(unicode)){
            log.info("新用户：{} 登录系统",unicode);
            UserCommon.userMap.put(unicode,password);
        }
        log.info("session未缓存，直接执行爬虫脚本");
        String session = signIn.getsession(unicode,password);
        UserCommon.sessionMap.put(unicode,session);
        return session;
    }



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

    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }

    public String getSessionByPython(String unicode,String password){
        String[] command = {"python3","/root/python/getsession.py",unicode,password};
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(command);
        }catch (IOException e){
            return e.getMessage();
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = null;
        StringBuilder output = new StringBuilder();

        while (true) {
            try {
                if (!((line = reader.readLine()) != null)) break;
            } catch (IOException e) {
                return e.getMessage();
            }
            output.append(line);
        }

        try {
            int exitcode = process.waitFor();
        } catch (InterruptedException e) {
            return e.getMessage();
        }
        return output.toString();
    }
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

}
