package com.atguigu.gulimall.search.service.impl;

import com.atguigu.gulimall.search.config.GulimallElasticSearchConfig;
import com.atguigu.gulimall.search.constant.EsConstant;
import com.atguigu.gulimall.search.service.MallSearchService;
import com.atguigu.gulimall.search.vo.SearchParam;
import com.atguigu.gulimall.search.vo.SearchResult;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @Author Dali
 * @Date 2022/4/3 15:41
 * @Version 1.0
 * @Description
 */
@Service
public class MallSearchServiceImpl implements MallSearchService {
	@Autowired
	private RestHighLevelClient client;

	//去es中进行检索
	@Override
	public SearchResult search(SearchParam param) {

		// 1、动态构建出查询需要的DSL语句
		SearchResult result = null;


		// 1、准备检索请求
		SearchRequest searchRequest = buildSearchRequest(param);
		try {
			// 2、执行检索请求
			SearchResponse response = client.search(searchRequest, GulimallElasticSearchConfig.COMMON_OPTIONS);

			// 3、分析响应数据封装成我们需要的格式
			result = buildSearchResult(response);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 准备检索请求
	 * 模糊匹配。过滤(按照属性,分类，品牌，价格区间，库存)，排序，分页，高亮，聚合分析
	 *
	 * @return
	 */
	private SearchRequest buildSearchRequest(SearchParam param) {
		// 构建DSL语句
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		SearchRequest searchRequest = new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, sourceBuilder);

		/*
		 * 查询：模糊匹配，过滤(按照属性，分类，品牌，价格区间，库存)
		 */
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		if (!StringUtils.isEmpty(param.getKeyword())) {
			boolQuery.must(QueryBuilders.matchQuery("skuTitle", param.getKeyword()));
		}

		if (param.getCatalog3Id() != null) {
			boolQuery.filter(QueryBuilders.termQuery("catalog3Id", param.getCatalog3Id()));
		}

		if (param.getBrandId() != null && param.getBrandId().size() > 0) {
			boolQuery.filter((QueryBuilders.termsQuery("brandId", param.getBrandId())));
		}

		if (param.getAttrs() != null && param.getAttrs().size() > 0) {

			for (String attrStr : param.getAttrs()) {
				BoolQueryBuilder nestedBoolQuery = QueryBuilders.boolQuery();
				// 格式：2_16G:8G
				String[] s = attrStr.split("_");
				String attrId = s[0];    // 检索的属性id
				String[] attrValues = s[1].split(":");    // 这个属性的检索用的值
				nestedBoolQuery.must(QueryBuilders.termQuery("attrs.attrId", attrId));
				nestedBoolQuery.must(QueryBuilders.termsQuery("attrs.attrValue", attrValues));

				// 每一个必须都得生成一个nested查询
				NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("attrs", nestedBoolQuery, ScoreMode.None);
				boolQuery.filter(nestedQuery);
			}

		}

		// 1:有库存，0：无库存
		boolQuery.filter(QueryBuilders.termQuery("hasStock", param.getHasStock() == 1));


		if (!StringUtils.isEmpty(param.getSkuPrice())) {
			// 价格区间：1_500/_500/500_
			RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("skuPrice");
			String[] s = param.getSkuPrice().split("_");
			if (s.length == 2) {
				// 区间 大于等于第一位，小于等于第二位
				rangeQuery.gte(s[0]).lte(s[1]);
			} else if (s.length == 1) {
				if (param.getSkuPrice().startsWith("_")) {
					rangeQuery.lte(s[1]);
				}

				if (param.getSkuPrice().endsWith("_")) {
					rangeQuery.gte(s[0]);
				}
			}
		}

		sourceBuilder.query(boolQuery);

		/*
		 * 排序，分页，高亮，
		 */
		// 2.1 排序
		if (StringUtils.isNotEmpty(param.getSort())) {
			String sort = param.getSort();
			// 格式：sort = hotScore_asc/desc
			String[] s = sort.split("_");
			SortOrder order = s[1].equalsIgnoreCase("asc") ? SortOrder.ASC : SortOrder.DESC;
			sourceBuilder.sort(s[0], order);
		}

		// 2.2 分页 例如：pageSize:5
		// pageNum:1 from:0  size:5 [0,1,2,3,4]
		// pageNum:2 from:5  size:5
		// from = (pageNum-1) * size
		sourceBuilder.from((param.getPageNum() - 1) * EsConstant.PRODUCT_PAGESIZE);
		sourceBuilder.size(EsConstant.PRODUCT_PAGESIZE);

		// 2.3 高亮显示
		if (StringUtils.isNotEmpty(param.getKeyword())) {
			HighlightBuilder builder = new HighlightBuilder();
			builder.field("skuTitle");
			builder.preTags("<b style='color:red'>");
			builder.postTags("</b>");
			sourceBuilder.highlighter(builder);
		}

		/**
		 *  聚合分析
		 */
		String s = sourceBuilder.toString();
		System.out.println("构建的DSL" + s);
		return searchRequest;
	}

	/**
	 * 构建结果数据
	 *
	 * @param response
	 * @return
	 */
	private SearchResult buildSearchResult(SearchResponse response) {
		return null;
	}
}
