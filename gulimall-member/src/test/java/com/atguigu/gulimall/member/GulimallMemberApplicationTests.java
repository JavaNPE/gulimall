package com.atguigu.gulimall.member;

//import org.junit.jupiter.api.Test;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.Md5Crypt;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
public class GulimallMemberApplicationTests {

    @Test
    public void contextLoads() {
        // 使用原生的MD5进行加密
        String md5Password = DigestUtils.md5Hex("123");
        System.out.println("MD5原生加密后：" + md5Password + ",长度：" + md5Password.length());

        String md5Password2 = DigestUtils.md5Hex("fadsf8jjfias67_on@jfidasnffjaidsfj57&hfdhabf@67hji");
        System.out.println("MD5原生加密后：" + md5Password2 + ",长度：" + md5Password2.length());

        // MD5盐值加密：加的是$1$拼接的8位随机字符, 同一个明文生成的MD5盐值都不一样
        String s = Md5Crypt.apr1Crypt("123456".getBytes());
        // String md5Crypt = Md5Crypt.md5Crypt("123456".getBytes(), "$1$qqqqqqqq");

        System.out.println("MD5盐值加密后：" + s + "长度：" + s.length());

        // 使用Spring为我们封装好的工具生成盐值加密
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        // $2a$10$/Eqc6mGRjB/92etHK9pYKuUalgs64SnwwK11.W3wOK4GAiAeKa4hS
        // $2a$10$UPR0Y3AxfwfrULFQRs8j5uDMFL8EGzMoglDS1K7HFV1CVcNKVyLxK
        String encode = passwordEncoder.encode("123456");
        System.out.println("Spring工具生成：" + encode + "长度：" + encode.length());
        boolean matches = passwordEncoder.matches("123456",
                "$2a$10$/Eqc6mGRjB/92etHK9pYKuUalgs64SnwwK11.W3wOK4GAiAeKa4hS");

        System.out.println("spring：" + encode + ",是否一致：" + matches);
    }
}
