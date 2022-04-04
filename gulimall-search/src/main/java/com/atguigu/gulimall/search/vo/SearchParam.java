package com.atguigu.gulimall.search.vo;

import lombok.Data;

import java.util.List;

/**
 * @Author Dali
 * @Date 2022/4/3 15:36
 * @Version 1.0
 * @Description 封装页面可能传递过来的所有查询条件
 */
@Data
public class SearchParam {
	/**
	 * 页面传递过来的全文匹配关键字
	 */
	private String keyword;

	/**
	 * 三级分类id
	 */
	private Long catalog3Id;

	/**
	 * 排序条件：
	 * sort=saleCount_asc/desc 销量
	 * sort=skuPrice_asc/desc  sku
	 * sort=hotScore_asc/desc  热度
	 */
	private String sort;

	/**
	 * 好多的过滤条件
	 * hasStock(是否有货),skuPrice区间，brandId，catalog3Id,attrs
	 */
	private Integer hasStock = 1;    //是否只显示有货0、1
	private String skuPrice;    //价格区间
	private List<Long> brandId;        //按照品牌id进行查询、可以多选
	private List<String> attrs;        //按照属性进行筛选
	private Integer pageNum = 1;       //按照页码筛选 默认第一页
}
