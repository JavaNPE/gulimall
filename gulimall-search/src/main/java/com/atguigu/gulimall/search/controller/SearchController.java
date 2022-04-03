package com.atguigu.gulimall.search.controller;

import com.atguigu.gulimall.search.service.MallSearchService;
import com.atguigu.gulimall.search.vo.SearchParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @Author Dali
 * @Date 2022/3/30 22:03
 * @Version 1.0
 * @Description
 */
@Controller
public class SearchController {

	@Autowired
	MallSearchService mallSearchService;

	/**
	 * 自动将页面提交过来的所有请求查询参数封装成指定的对象
	 *
	 * @param param
	 * @return
	 */
	@GetMapping("/list.html")
	public String listPage(SearchParam param) {

		Object result = mallSearchService.search(param);
		return "index";
	}
}
