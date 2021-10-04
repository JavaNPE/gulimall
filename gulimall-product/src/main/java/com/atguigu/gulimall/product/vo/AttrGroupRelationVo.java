package com.atguigu.gulimall.product.vo;

import lombok.Data;

/**
 * @Author Dali
 * @Date 2021/10/4 16:24
 * @Version 1.0
 * @Description: 12、删除属性与分组的关联关系：https://easydoc.net/s/78237135/ZUqEdvA4/qn7A2Fht
 */
@Data
public class AttrGroupRelationVo {
    //请求参数： [{"attrId":1,"attrGroupId":2}]
    private Long attrId;
    private Long attrGroupId;
}
