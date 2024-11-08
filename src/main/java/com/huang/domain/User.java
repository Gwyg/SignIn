package com.huang.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private int id;
    private String unicode; //统一认证码
    private String password; // 密码
    private String session; // 学在重邮的session
    private String name; //姓名
    private String nickname; // 昵称
    private String photo; // 教务在线照片
    private String headPhoto;// 头像
    private int isnotice; // 是否需要发邮件通知签到 1，需要 0不需要 默认为0
    private String email; // 发邮件通知签到的邮箱
}
