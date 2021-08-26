package com.atguigu.common.valid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author Dali
 * @Date 2021/8/26 14:44
 * @Version 1.0
 * @Description: 2)、编写一个自定义的校验器
 */
public class ListValueConstraintValidator implements ConstraintValidator<ListValue, Integer> {

    // 存储所有可能的值
    private Set<Integer> set = new HashSet<>();

    //初始化方法:你可以获取注解上的内容并进行处理
    @Override
    public void initialize(ListValue constraintAnnotation) {
        // 获取后端写好的限制
        // 这个vals就是ListValue注解里的vals，我们写的注解是@ListValue(vals={0,1})
        int[] vals = constraintAnnotation.vals();
        for (int val : vals) {
            set.add(val);
        }
    }

    //判断是否校验成功

    /**
     * @param value   需要校验的值
     * @param context
     * @return
     */
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {

        return set.contains(value);
    }
}