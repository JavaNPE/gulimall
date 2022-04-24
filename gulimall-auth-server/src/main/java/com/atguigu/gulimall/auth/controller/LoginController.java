package com.atguigu.gulimall.auth.controller;

import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.common.exception.BizCodeEnume;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.auth.feign.ThirdPartFeignService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Author Dali
 * @Date 2022/4/23 17:34
 * @Version 1.0
 * @Description
 */
@Controller
public class LoginController {
    @Autowired
    ThirdPartFeignService thirdPartFeignService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

//    @GetMapping("/login.html")
//    public String loginPage() {
//        return "login";
//    }
//
//    /**
//     * http://auth.gulimall.com/reg.html
//     *
//     * @return
//     */
//    @GetMapping("/reg.html")
//    public String regPage() {
//        return "reg";
//    }

    @ResponseBody
    @PostMapping("/sms/sendCode")
    public R sendCode(@RequestParam("phone") String phone) {

        // TODO 思考1：接口防刷
        // redis根据key 获取 value值
        String redisCode = stringRedisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone);
        if (StringUtils.isNotBlank(redisCode)) {
            long l = Long.parseLong(redisCode.split("_")[1]);
            if (System.currentTimeMillis() - l < 60000) {
                // 60s内不在重复发送短信验证码
                return R.error(BizCodeEnume.SMS_CODE_EXCEPTION.getCode(), BizCodeEnume.SMS_CODE_EXCEPTION.getMsg());
            }
        }

        // 需要考虑：1、防止前端页面重复刷新，每次刷新之后都可以获取一次验证码发送操作
        // 思考2：验证码使用时需要再次校验是否一致，使用redis临时存储code验证码;
        //      存入redis时 key->phone, value->code
        // 自动生成随机短信验证码
        String code = UUID.randomUUID().toString().substring(0, 5) + "_" + System.currentTimeMillis();

        // redis缓存验证码：设置redis中的key和value值，以及存入到redis中验证码的过期时间（10分钟有效）
        stringRedisTemplate.opsForValue()
                .set(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone, code, 10, TimeUnit.MINUTES);
        thirdPartFeignService.sendCode(phone, code);
        return R.ok();
    }
}