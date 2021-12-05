package com.atguigu.common.to.es;

import jdk.internal.util.xml.impl.Attrs;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author Dali
 * @Date 2021/12/4 9:36
 * @Version 1.0
 * @Description
 */

/**
 * PUT product
 * {
 *     "mappings":{
 *         "properties": {
 *             "skuId":{ "type": "long" },
 *             "spuId":{ "type": "keyword" },  # 不可分词
 *             "skuTitle": {
 *                 "type": "text",
 *                 "analyzer": "ik_smart"  # 中文分词器
 *             },
 *             "skuPrice": { "type": "keyword" },  # 保证精度问题
 *             "skuImg"  : { "type": "keyword" },  # 视频中有false
 *             "saleCount":{ "type":"long" },
 *             "hasStock": { "type": "boolean" },
 *             "hotScore": { "type": "long"  },
 *             "brandId":  { "type": "long" },
 *             "catalogId": { "type": "long"  },
 *             "brandName": {"type": "keyword"}, # 视频中有false
 *             "brandImg":{
 *                 "type": "keyword",
 *                 "index": false,  # 不可被检索，不生成index，只用做页面使用
 *                 "doc_values": false # 不可被聚合，默认为true
 *             },
 *             "catalogName": {"type": "keyword" }, # 视频里有false
 *             "attrs": {
 *                 "type": "nested",
 *                 "properties": {
 *                     "attrId": {"type": "long"  },
 *                     "attrName": {
 *                         "type": "keyword",
 *                         "index": false,
 *                         "doc_values": false
 *                     },
 *                     "attrValue": {"type": "keyword" }
 *                 }
 *             }
 *         }
 *     }
 * }
 */
@Data
public class SkuEsModel {
    private Long skuId;
    private Long spuId;
    private String skuTitle;
    private BigDecimal skuPrice;
    private String skuImg;
    private Long saleCount;
    /**
     * 是否拥有库存
     */
    private boolean hasStock;
    /**
     * 热度评分
     */
    private Long hotScore;
    /**
     * 品牌id
     */
    private Long brandId;
    /**
     * 分类id
     */
    private Long catalogId;
    private String brandName;
    private String brandImg;
    private String catalogName;
    /**
     * 商品规格属性信息
     */
    private List<Attrs> attrs;

    @Data
    public static class Attrs{
        private Long attrId;
        private String attrName;
        private String attrValue;
    }
}
