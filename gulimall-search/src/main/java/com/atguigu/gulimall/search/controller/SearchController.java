package com.atguigu.gulimall.search.controller;

import com.atguigu.gulimall.search.service.MallSearchService;
import com.atguigu.gulimall.search.vo.SearchParam;
import com.atguigu.gulimall.search.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
	public String listPage(SearchParam param, Model model) {

		// 1、根据传递过来的页面查询参数，去es中检索商品
		SearchResult result = mallSearchService.search(param);
		model.addAttribute("result", result);
		return "index";
	}
}
