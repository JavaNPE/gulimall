package com.atguigu.gulimall.ware.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author Dali
 * @Date 2021/11/19 23:03
 * @Version 1.0
 * @Description： 远程调用（服务）的不同写法
 */
@FeignClient("gulimall-product")
public interface ProductFeignService {
    /**
     * /product/skuinfo/info/{skuId}
     * 和
     * /api/product/skuinfo/info/{skuId}
     * 二者有什么不同？？？
     *
     * 1、让所有请求过网关
     *      1.1 @FeignClient("gulimall-gateway") ： 给gulimall-gateway所在的机器发请求；
     *      1.2 请求路径配置成 @RequestMapping("/api/product/skuinfo/info/{skuId}")这样
     * 2、直接让后台指定服务处理：
     *      2.1 @FeignClient("gulimall-product")
     *      2.1 请求路径配置成 @RequestMapping("/product/skuinfo/info/{skuId}")
     * <p>
     * 信息
     */
    @RequestMapping("/product/skuinfo/info/{skuId}")
    public R info(@PathVariable("skuId") Long skuId);
}
