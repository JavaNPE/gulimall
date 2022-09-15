package com.atguigu.gulimall.product.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author HedianTea
 * @email daki9981@qq.com
 * @date 2022/9/15 11:12
 */
@ConfigurationProperties(prefix = "gulimall.thread")
@Data
public class ThreadPoolConfigProperties {
    private int coreSize;
    private int maxSize;
    private long keepAliveTime;
}
