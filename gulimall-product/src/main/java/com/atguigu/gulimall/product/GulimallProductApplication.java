package com.atguigu.gulimall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 1.整合MyBatis-Plus
 *      1.导入依赖：
 *      <dependency>
 *             <groupId>com.baomidou</groupId>
 *             <artifactId>mybatis-plus-boot-starter</artifactId>
 *             <version>3.3.2</version>
 *      </dependency>
 *      2.配置：参照MyBatis-Plus官网
 *          1.配置数据源
 *            1).导入数据库驱动
 *            2).在application.yml配置数据源相关信息
 *          2.配置MyBatis-Plus相关信息
 *            1).使用@MapperScan
 *            2).告诉MyBatis-Plus，sql映射文件位置在哪里
 *
 * 2、逻辑删除
 * 1)、配置全局的逻辑删除规则(省略)
 * 2)、配置逻辑删除的组件Bean (省略)
 * 3)、给Bean加上逻辑删除注解@TableLogic
 *
 *
 * 3、JSR303
 *      1)、给Bean添加校验注解: javax. val idation. constraints,并定义自己的message提示
 *      2)、开启校验功能@Valid
 *          效果:校验错误以后会有默认的响应;
 *      3)、给校验的bean后紧跟一个BindingResult, 就可以获取到校验的结果
 *      4)、分组校验(用来完成多场景的复杂校验)
 *          1）、 @NotBlank(message = "品牌名必须提交", groups = {AddGroup.class, UpdateGroup.class})
 *          给校验注解标注什么情况需要进行校验
 *          2）、在controller中添加注解@Validated({AddGroup.class}
 *          3)、默认没有指定分组的校验在解@NotBlank,在分组校验情况@Validated({AddGroup.class}下不生效，只会在@Validated生效;
 * 4、统一的异常处理
 * @ControllerAdvice
 *      1)、编写异常处理类，使用@ControllerAdvice。
 *      2)、使用@ExceptionHandler标注方法可以处理的异常。
 *
 * 5)、自定义校验
 *    1)、编写一个自定义的校验注解
 *    2)、编写一个自定义的校验器    ConstraintValidator
 *    3)、关联自定义的校验器和自定义的校验注解
 *    @Documented
 *    @Constraint(validatedBy = {ListValueConstraintValidator.class})【可以指定多个不同的校验器，适配不同类型的校验】
 *    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
 *    @Retention(RUNTIME)
 *
 * 5、模板引擎
 *      1)、thymeleaf-starter:关闭缓存（为了能够实时显示数据）
 *      2)、静态资源都放在static文件夹下就可以按照路径直接访问
 *      3)、页面放在templates下，直接访问
 *              SpringBoot,访问项目的时候，默认会找index
 *      4)、如果页面修改之后不想重启服务器，而且还要实时更新页面内容怎么办？
 *          1、引入dev-tools
 *          2)、修改元贝面controller shift f9重新自动编详下贝面，如果是代码配置，还是推荐重启服务
 *
 * 6、整合redis
 *      1)、引入data-redis-starter
 *      2)、简单配置redis的host等信息
 *      3)、使用SpringBoot自动配置好的StringRedisTemplate来操作redis
 *      redis-》(简单理解为一个map)Map;存放数据key,数据值value
 *
 * 7、整合redisson作为分布式锁等功能框架
 *      1）、引入redisson依赖（暂时引入的是原生的依赖包，还需要配置；想省事引入redisson-starter）
 *      <!-- https://mvnrepository.com/artifact/org.redisson/redisson -->
 *          <dependency>
 *              <groupId>org.redisson</groupId>
 *              <artifactId>redisson</artifactId>
 *              <version>3.12.0</version>
 *          </dependency>
 *      2)、配置redisson(参考官方文档：https://github.com/redisson/redisson/wiki/2.-%E9%85%8D%E7%BD%AE%E6%96%B9%E6%B3%95)
 * 8、整合SpringCache简化开发
 *      1、引入依赖
 *          spring-boot-starter-cache、spring-boot-starter-data-redis
*       2、写配置
 *          (1)、自动配置了哪些
 *              CacheAutoConfiguration会导入 RedisCacheConfiguration;
 *              自动配好了缓存管理器RedisCacheManager
 *          (2)、配置使用redis作为缓存   spring.cache.type=redis
 *          (3)、测试使用缓存：几个常用注解
 *              @Cacheable: Triggers cache population.: 触发将数据保存到缓存的操作
 *              @CacheEvict: Triggers cache eviction.: 触发将数据从缓存删除的操作
 *              @CachePut: Updates the cache without interfering with the method execution.: 不影响方法执行更新缓存
 *              @Caching: Regroups multiple cache operations to be applied on a method.:组合以上多个操作
 *              @CacheConfig: Shares some common cache-related settings at class-level.: 在类级别共享缓存的相同配置
 *              1、开启缓存功能  @EnableCaching
 *              2、只需要使用注解就能完成缓存操作
 *			(4)、原理:
 *			 CacheAutoConfiguration -> RedisCacheConfiguration - >
 *			 自动配置了RedisCacheManager- >初始化所有的缓存->每个缓存决定使用什么配置
 *			 ->如果redisCacheConfiguration有就用已有的，没有就用默认配置
 *			 ->想改缓存的配置，只需要给容器中放一个RedisCacheConfiguration即 可
 *           ->就会应用到当前RedisCacheManager管理的所有缓存分区中
 */
@EnableCaching
@EnableFeignClients(basePackages = "com.atguigu.gulimall.product.feign")
@EnableDiscoveryClient
@MapperScan("com.atguigu.gulimall.product.dao")
@SpringBootApplication
public class GulimallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallProductApplication.class, args);
    }

}
