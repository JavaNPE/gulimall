package com.atguigu.gulimall.product.feign;

import com.atguigu.common.to.SkuReductionTo;
import com.atguigu.common.to.SpuBoundTo;
import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author Dali
 * @Date 2021/11/6 20:42
 * @Version 1.0
 * @Description
 */
@FeignClient("gulimall-coupon")
public interface CouponFeignService {
    /**
     * SpringCloud远程调用的逻辑：
     * 1、如果我们有一个service调用了CouponFeignService的saveSpuBounds(spuBoundTo)方法而且还给它传了一个对象，而不是一个基本数据类型的数据
     *      1.1 SpringCloud会做的第一步操作：使用@RequestBody注解将spuBoundTo这个对象转成json
     *      1.2 SpringCloud会去注册中心找到gulimall-coupon这个服务，给该服务的/coupon/spubounds/save发送请求，
     *          将上一步转的json放在请求体位置，发送请求；
     *      1.3 对方服务收到请求，其实收到的是请求体中的json数据
     *          (@RequestBody SpuBoundsEntity spuBounds)的作用：将请求体中的json转为SpuBoundsEntity
     * 小总结：  只要json数据模型是兼容的。双方服务无需使用同一个To
     *
     * @param spuBoundTo
     * @return
     */
    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundTo spuBoundTo);


    @PostMapping("/coupon/skufullreduction/saveinfo")
    R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);


    /**
     * com.atguigu.gulimall.coupon.controller.SpuBoundsController
     */
    //@PostMapping("/save")
    //@RequiresPermissions("coupon:spubounds:save")
    //只要接收到的json中的字段能够与SpuBoundsEntity对应没必要都写成(@RequestBody SpuBoundTo spuBoundTo)这种类型
    //public R save(@RequestBody SpuBoundsEntity spuBounds) { 只要json数据对的上（兼容），就可以使用这种
    //public R save(@RequestBody SpuBoundTo spuBoundTo) {
}
