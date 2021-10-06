package com.atguigu.gulimall.product.vo;

import lombok.Data;

/**
 * @Author Dali
 * @Date 2021/10/6 21:51
 * @Version 1.0
 * @Description： Vo里面对应的都是接口文档里面的响应数据字段 ： 14、获取分类关联的品牌：https://easydoc.net/s/78237135/ZUqEdvA4/HgVjlzWV
 * <p>
 * 老师说，不是entity的全量数据都可以封装vo
 */
@Data
public class BrandVo {

    /**
     * "brandId":0
     * "brandName":"string",
     */
    private Long brandId;
    private String brandName;
}
