package com.atguigu.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author Dali
 * @Date 2021/11/6 21:35
 * @Version 1.0
 * @Description： 数据传输对象: 积分信息
 */
@Data
public class SpuBoundTo {
    private Long spuId;
    /**
     * 成长积分
     */
    private BigDecimal buyBounds;
    /**
     * 购物积分
     */
    private BigDecimal growBounds;
}
