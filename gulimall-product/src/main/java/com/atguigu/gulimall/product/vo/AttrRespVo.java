package com.atguigu.gulimall.product.vo;

import lombok.Data;

/**
 * @Author Dali
 * @Date 2021/10/1 12:23
 * @Version 1.0
 * @Description
 */
@Data
public class AttrRespVo extends AttrVo {
    /**
     * "catelogName": "手机/数码/手机", //所属分类名字
     * "groupName": "主体", //所属分组名字
     */
    private String catelogName;

    private String groupName;
}
