package com.atguigu.gulimall.ware;

import lombok.Data;

import java.util.List;

/**
 * @Author Dali
 * @Date 2021/11/15 20:23
 * @Version 1.0
 * @Description
 */
@Data
public class MergeVo {
    private Long purchaseId;     //:1, //整单id
    private List<Long> items;     //:[1,2,3,4] //合并项集合
}
