package com.atguigu.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author Dali
 * @Date 2021/11/6 22:36
 * @Version 1.0
 * @Description： 数据传输对象：sku的积分信息，优惠信息，和满减信息等
 */
@Data
public class SkuReductionTo {
    private Long skuId;
    private int fullCount;
    private BigDecimal discount;
    private int countStatus;
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private int priceStatus;
    private List<MemberPrice> memberPrice;
}
