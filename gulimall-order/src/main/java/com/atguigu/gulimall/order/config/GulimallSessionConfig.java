package com.atguigu.gulimall.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * @author HedianTea
 * @email daki9981@qq.com
 * @date 2022/9/15 10:39
 */
@Configuration
public class GulimallSessionConfig {

    @Bean
    public CookieSerializer cookieSerializer() {

        DefaultCookieSerializer cookiesSerializer = new DefaultCookieSerializer();

        cookiesSerializer.setDomainName("gulimall.com");
        cookiesSerializer.setCookieName("GULISESSION");
        return cookiesSerializer;
    }

    /**
     * session的序列化机制
     *
     * @return
     */
    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }

}
