package com.huang.crawler;

import com.alibaba.fastjson.JSON;
import com.huang.domain.Course;
import com.huang.domain.Rollcalls;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import javax.lang.model.element.VariableElement;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
public class SignIn {

    InMemoryCookieJar cookie = new InMemoryCookieJar();
    OkHttpClient client = new OkHttpClient.Builder().cookieJar(cookie).connectionPool(new ConnectionPool()).build();

    /**
     * 爬虫，获取session
     *
     * @param unicode  统一认证码
     * @param password 密码
     * @return 获取到的 session
     */
    public String getsession(String unicode, String password) {
        if(password == null || password.isEmpty()){
            return "账号不存在";
        }
        try {
            Request re = new Request.Builder().url("http://lms.tc.cqupt.edu.cn/user/index").get().build();
            Element e = null;
            try(Response response = client.newCall(re).execute();) {
                if (response.body() == null) {
                    return "登录页面获取失败";
                }
                Document doc = null;
                try {
                    doc = Jsoup.parse(response.body().string());
                } catch (IOException ex) {
                    return "登录页面解析失败" + ex.getMessage();
                }
                e = doc.getElementById("execution");
                if (e == null) {
                    return "获取execution失败";
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex.getMessage());
            }
            String execution = e.attr("value");
            FormBody body = new FormBody.Builder()
                    .add("execution", execution)
                    .add("username", unicode)
                    .add("password", password)
                    .add("_eventId", "submit")
                    .add("cllt", "userNameLogin")
                    .add("dllt", "generalLogin").build();
            Request request = new Request.Builder()
                    .url("https://ids.cqupt.edu.cn/authserver/login?service=http://lms.tc.cqupt.edu.cn/user/index")
                    .post(body)
                    .build();
            try(Response response1 = client.newCall(request).execute()) {
                return response1.headers().get("X-SESSION-ID");
            } catch (Exception ex) {
                return "获取session请求发送失败" + ex.getMessage();
            }
        } finally {
            cookie.clear();
        }

    }

    /**
     * 获取签到列表
     *
     * @param session session
     * @return courseId rollCallId
     */
    public List<String> getAllClass(String session) {
        Request request = new Request.Builder()
                .addHeader("Accept-Language", "zh-Hans")
                .addHeader("Host", "lms.tc.cqupt.edu.cn")
                .addHeader("Origin", "http://mobile.tc.cqupt.edu.cn")
                .addHeader("Referer", "http://mobile.tc.cqupt.edu.cn/")
                .addHeader("X-Forwarded-User", "P338kFwtHL4GEPN3")
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("X-SESSION-ID", session)
                .url("http://lms.tc.cqupt.edu.cn/api/radar/rollcalls?api_version=1.10")
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.body() == null) {
                throw new Exception("签到列表为空");
            }
            String s = response.body().string();
            Rollcalls rollcalls = JSON.parseObject(s, Rollcalls.class);
            List<Course> rollcalls1 = rollcalls.getRollcalls();
            if (Objects.isNull(rollcalls1)) {
                throw new Exception("签到列表为空");
            }
            List<String> res = rollcalls1.stream().map(c -> c.getCourseID() + "," + c.getRollcallID()).collect(Collectors.toList());
            if(res == null || res.isEmpty()){
                return null;
            }
            return res;
        } catch (Exception e) {
            log.info("获取上课列表发送请求失败");
            return null;
        } finally {
            cookie.clear();
        }
    }

    /**
     * 获取签到需要的请求体
     *
     * @param courseId   courseId
     * @param rollCallId rollCallId
     * @param session    session
     * @return 请求体json
     * @throws Exception 抛出的异常
     */
    public String getData(String courseId, String rollCallId, String session) {

        Request request = new Request.Builder()
                .addHeader("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
                .addHeader("Host", "lms.tc.cqupt.edu.cn")
                .addHeader("Origin", "http://mobile.tc.cqupt.edu.cn")
                .addHeader("Referer", "http://mobile.tc.cqupt.edu.cn/")
                .addHeader("X-Forwarded-User", "P338kFwtHL4GEPN3")
                .addHeader("X-SESSION-ID", session)
                .url("http://lms.tc.cqupt.edu.cn/api/course/" + courseId + "/rollcall/" + rollCallId + "/qr_code")
                .get()
                .build();
        try(Response response = client.newCall(request).execute()) {
            if (response.body() == null) {
                log.info("获取data的请求发送失败：{}",response.message());
                throw new Exception("获取签到data失败");
            }
            return response.body().string();
        } catch (Exception e) {
            log.info("获取data的请求发送失败：{}",e.getMessage());
            throw new RuntimeException(e.getMessage());
        } finally {
            cookie.clear();
        }

    }

    /**
     * 签到接口
     *
     * @param data       请求体参数
     * @param rollCallId rollCallId
     * @param session    session
     * @return 签到情况
     * @throws IOException 异常
     */
    public String signIn(String data, String rollCallId, String session){
        Request request;
        try{

            RequestBody requestBody = RequestBody.create(data.getBytes());
            request = new Request.Builder()
                    .addHeader("Accept-Language", "zh-Hans")
                    .addHeader("Host", "lms.tc.cqupt.edu.cn")
                    .addHeader("Origin", "http://mobile.tc.cqupt.edu.cn")
                    .addHeader("Referer", "http://mobile.tc.cqupt.edu.cn/")
                    .addHeader("X-Forwarded-User", "P338kFwtHL4GEPN3")
                    .addHeader("X-Requested-With", "XMLHttpRequest")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("X-SESSION-ID", session)
                    .url("http://lms.tc.cqupt.edu.cn/api/rollcall/" + rollCallId + "/answer_qr_rollcall")
                    .put(requestBody)
                    .build();
        }catch (Exception exception){
            log.info("构建签到请求失败");
            return "签到失败：" + exception.getMessage();
        }
        try (Response response = client.newCall(request).execute();){
            if (!response.isSuccessful()) {
                log.info("签到请求发送失败：{}",response.message());
                return "签到失败：" + response.message();
            }
            return "签到成功";
        } catch (IOException ex) {
            log.info("签到请求发送失败：{}",ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        } finally {
            cookie.clear();
        }

    }

}
