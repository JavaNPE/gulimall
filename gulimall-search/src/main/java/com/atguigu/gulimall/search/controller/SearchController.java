package com.atguigu.gulimall.search.controller;

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
	@GetMapping("/list.html")
	public String listPage() {
		return "index";
	}
}
