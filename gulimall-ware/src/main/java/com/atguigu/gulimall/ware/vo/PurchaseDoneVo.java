package com.atguigu.gulimall.ware.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Author Dali
 * @Date 2021/11/18 19:59
 * @Version 1.0
 * @Description: 07、完成采购-接口：/ware/purchase/done
 * <p>
 * 接口文档地址：https://easydoc.net/doc/75716633/ZUqEdvA4/cTQHGXbK
 */
@Data
public class PurchaseDoneVo {
    @NotNull
    private Long id;        //采购单id

    /**
     * items: [{itemId:1,status:4,reason:""}]//完成/失败的需求详情
     */
    private List<PurchaseItemDoneVo> items;
}
