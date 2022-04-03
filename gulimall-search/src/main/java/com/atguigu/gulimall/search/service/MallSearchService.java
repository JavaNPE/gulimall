package com.atguigu.gulimall.search.service;

import com.atguigu.gulimall.search.vo.SearchParam;

/**
 * @Author Dali
 * @Date 2022/4/3 15:41
 * @Version 1.0
 * @Description
 */
public interface MallSearchService {
	/**
	 *
	 * @param param 检索的所有参数
	 * @return 返回检索的结果
	 */
	Object search(SearchParam param);
}
