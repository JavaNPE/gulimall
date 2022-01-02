package com.atguigu.gulimall.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @Author Dali
 * @Date 2022/1/2 22:57
 * @Version 1.0
 * @Description: Redisson程序化的配置方法是通过构建Config对象实例来实现的
 * 官网地址：https://github.com/redisson/redisson/wiki/2.-%E9%85%8D%E7%BD%AE%E6%96%B9%E6%B3%95
 *
 * 2.6. 单Redis节点模式
 * 程序化配置方法：
 * 		// 默认连接地址 127.0.0.1:6379
 * 		RedissonClient redisson = Redisson.create();
 * 		Config config = new Config();
 * 		config.useSingleServer().setAddress("myredisserver:6379");
 * 		RedissonClient redisson = Redisson.create(config);
 */
@Configuration
public class MyRedissonConfig {

	/**
	 * 所有对Redisson的使用都是通过RedissonClient对象
	 * @return
	 * @throws IOException
	 */
	@Bean(destroyMethod = "shutdown")
	public RedissonClient redisson() throws IOException {

		// 1、创建配置
		// 若报错：Redis url should start with redis:// or rediss:// (for SSL connection)
		Config config = new Config();
		// 注意：如果redis设置了密码，在这里要加一个setPassword方法
		config.useSingleServer().setAddress("redis://192.168.56.10:6379");

		// 2、根据Config创建出RedissonClient实例
		RedissonClient redissonClient = Redisson.create(config);
		return redissonClient;
	}
}
