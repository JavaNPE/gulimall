package com.atguigu.gulimall.search.vo;

import com.atguigu.common.to.es.SkuEsModel;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @Author Dali
 * @Date 2022/4/3 16:17
 * @Version 1.0
 * @Description
 */
@Data
@Builder
public class SearchResult {
	// 查询到的所有商品信息
	private List<SkuEsModel> products;
	/**
	 * 以下是分页信息
	 */
	private Integer pageNum;    //当前页面
	private Long total;            //总记录数
	private Integer totalPages;    //总页码

	private List<BrandVo> brands;    //当前查询到的结果，所有涉及到的品牌
	private List<CatalogVo> catalogs;    //当前查询到的结果，所有涉及到的分类
	private List<AttrVo> attrs;    //当前查询到的结果，所有涉及到的属性

	//=============以上是返回给页面的所有信息================

	@Data
	public static class BrandVo {
		private Long brandId;
		private String brandName;
		private String brandImg;
	}

	@Data
	public static class CatalogVo {
		private Long catalogId;
		private String catalogName;
	}

	@Data
	public static class AttrVo {
		private Long attrId;
		private String attrName;
		private List<String> attrValue;
	}
}
