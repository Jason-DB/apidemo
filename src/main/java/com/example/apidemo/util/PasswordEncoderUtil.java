package com.example.apidemo.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//初始化密码，将生产的密码放到数据库中
public class PasswordEncoderUtil {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "1";
        String encodedPassword = encoder.encode(rawPassword);
        System.out.println(encodedPassword);
    }
}
