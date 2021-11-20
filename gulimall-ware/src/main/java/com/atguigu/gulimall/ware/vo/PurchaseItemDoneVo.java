package com.atguigu.gulimall.ware.vo;

import lombok.Data;

/**
 * @Author Dali
 * @Date 2021/11/18 20:01
 * @Version 1.0
 * @Description
 */
@Data
public class PurchaseItemDoneVo {
    //itemId:1,status:4,reason:""
    private Long itemId;
    private Integer status;
    private String reason;
}
