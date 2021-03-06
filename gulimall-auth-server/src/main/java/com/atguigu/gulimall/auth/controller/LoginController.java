package com.atguigu.gulimall.auth.controller;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.common.exception.BizCodeEnume;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.auth.feign.MemberFeignService;
import com.atguigu.gulimall.auth.feign.ThirdPartFeignService;
import com.atguigu.gulimall.auth.vo.UserLoginVo;
import com.atguigu.gulimall.auth.vo.UserRegistVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author Dali
 * @Date 2022/4/23 17:34
 * @Version 1.0
 * @Description
 */
@Controller
@Slf4j
public class LoginController {
    @Autowired
    ThirdPartFeignService thirdPartFeignService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    MemberFeignService memberFeignService;

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
        String code = UUID.randomUUID().toString().substring(0, 4);
        System.out.println("短信验证码为：" + code);
        String substring = code + "_" + System.currentTimeMillis();
        System.out.println("redis中存储的短信验证码为（含时间戳）：" + substring);

        // redis缓存验证码：设置redis中的key和value值，以及存入到redis中验证码的过期时间（10分钟有效）
        stringRedisTemplate.opsForValue()
                .set(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone, substring, 10, TimeUnit.MINUTES);
        // 短信发送（PS: 由于短信套餐用完了，暂时关闭短信服务，已控制台为准）
        // thirdPartFeignService.sendCode(phone, code);
        return R.ok();
    }

    /**
     * RedirectAttributes redirectAttributes：模拟重定向携带数据
     *
     * @param vo
     * @param result
     * @param redirectAttributes
     * @return
     */
    @PostMapping("/regist")
    public String regist(@Valid UserRegistVo vo, BindingResult result, RedirectAttributes redirectAttributes) {
        // Map<String, String> errors = new HashMap<>();
        /**
         * 方法一：将错误信息收集到map集合中
         * result.getFieldErrors().stream().map(fieldError -> {
         *                 String field = fieldError.getField();
         *                 String defaultMessage = fieldError.getDefaultMessage();
         *                 errors.put(field, defaultMessage);
         *                 return errors;
         */
        if (result.hasErrors()) {

            // 方法二：将错误信息收集到map集合中
            Map<String, String> errors = result.getFieldErrors().stream()
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));

            // model.addAttribute("errors", errors);
            redirectAttributes.addFlashAttribute("errors", errors);

            // 如果校验出错, 转发forward到注册页面 return "forward:/reg.html"; (后期回报错：Request method 'POST' not supported)
            //用户注册->/regist[post]----》转发/reg. html (路径映射默认都是get方式访问的。)
            return "redirect:http://auth.gulimall.com/reg.html";  // 使用thymeleaf直接跳转到reg.html页面
        }

        // 1、校验验证码：前端用户输入的验证码信息
        String code = vo.getCode();
        log.info("验证码为：" + code);
        // redis中存储的验证码信息
        String redisCodeStr = stringRedisTemplate.opsForValue()
                .get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());
        // redis缓存中存储了短信验证码
        if (StringUtils.isNotEmpty(redisCodeStr)) {
            log.info("redis中存储验证码为：" + redisCodeStr);
            // 则对用户输入的验证码和redis缓存中进行比对
            if (code.equals(redisCodeStr.split("_")[0])) {
                // 如果一致则将redis缓存中的验证码删除： 也就是所谓的“令牌机制”，用过之后随即删除。
                log.info("用户输入的验证码和Redis中存储的验证码一致");
                stringRedisTemplate.delete(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());
                // 验证码通过：真正注册-调用远程服务进行注册。
                log.info("验证码通过：真正注册-调用远程服务进行注册");
                R r = memberFeignService.regist(vo);
                if (r.getCode() == 0) {
                    // 成功 返回登录页面
                    log.info("注册成功，返回登录页面");
                    return "redirect:http://auth.gulimall.com/login.html";
                } else {
                    // 失败 依旧返回注册页面
                    log.info("注册失败，依旧停留在注册页面");
                    Map<String,String> errors = new HashMap<>();
                    errors.put("msg", r.getData("msg", new TypeReference<String>(){}));
                    redirectAttributes.addFlashAttribute("errrors", errors);
                    return "redirect:http://auth.gulimall.com/reg.html";
                }
            } else {
                // 验证不通过
                log.info("验证码校验不通过");
                Map<String, String> errors = new HashMap<>();
                errors.put("code", "验证码错误");
                redirectAttributes.addFlashAttribute("errors", errors);
                return "redirect:http://auth.gulimall.com/reg.html";
            }
        } else {
            log.info("验证码校验不通过");
            Map<String, String> errors = new HashMap<>();
            errors.put("code", "验证码错误");
            redirectAttributes.addFlashAttribute("errors", errors);

            // 如果验证码校验出错，转发到注册页面
            return "redirect:http://auth.gulimall.com/reg.html";  // 使用thymeleaf直接跳转到reg.html页面
        }


        /*// 注册成功返回到首页，重定redirect向到登陆页面
        return "redirect:/login.html";*/
    }

    /**
     * 登录请求： 此处传入的是key-value 不是json数据，所以没有用@RequestBody注解
     *
     * @param vo
     * @return
     */
    @PostMapping("/login")
    public String login(UserLoginVo vo, RedirectAttributes redirectAttributes) {
        // 进行远程登录
        R login = memberFeignService.login(vo);
        if (login.getCode() == 0) {
            log.info("登录成功");
            return "redirect:http://gulimall.com";
        } else {
            log.info("登录失败，重新登录");
            Map<String, String> errors = new HashMap<>();
            errors.put("msg", login.getData("msg", new TypeReference<String>(){}));
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.gulimall.com/login.html";
        }
    }
}
