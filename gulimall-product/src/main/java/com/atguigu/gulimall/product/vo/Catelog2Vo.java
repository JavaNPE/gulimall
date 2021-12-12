package com.atguigu.gulimall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author Dali
 * @Date 2021/12/12 16:05
 * @Version 1.0
 * @Description
 */
//2级分类vo
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Catelog2Vo {
    private String catalog1Id;  //1级父分类id
    private List<Catelog3Vo> catalog3List;  //三级子分类
    private String id;
    private String name;

    /**
     * 三级分类vo
     * "catalog2Id": "1",
     * "id": "1",
     * "name": "电子书"
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Catelog3Vo {
        private String catalog2Id;  //父分类，2级分类id
        private String id;
        private String name;
    }
}
