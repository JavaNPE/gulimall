/**
 * Copyright 2021 json.cn
 */
package com.atguigu.gulimall.product.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Auto-generated: 2021-10-31 11:44:1
 *
 * @author json.cn (i@json.cn)
 * @website http://www.json.cn/java2pojo/
 */
@Data
public class SpuSaveVo {
    private String spuName;
    private String spuDescription;
    private Long catalogId;
    private Long brandId;
    private BigDecimal weight;
    private int publishStatus;
    private List<String> decript;
    private List<String> images;
    /**
     * 积分信息
     */
    private Bounds bounds;
    /**
     * spu的基本属性
     */
    private List<BaseAttrs> baseAttrs;
    private List<Skus> skus;
}