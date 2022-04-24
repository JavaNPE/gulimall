package com.atguigu.gulimall.auth.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author Dali
 * @Date 2022/4/24 8:40
 * @Version 1.0
 * @Description: 远程调用
 */
@FeignClient("gulimall-third-party")
public interface ThirdPartFeignService {

    @PostMapping("/sms/sendCode")
    public R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code);
}