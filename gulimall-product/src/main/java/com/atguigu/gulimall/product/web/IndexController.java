package com.atguigu.gulimall.product.web;

import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.vo.Catelog2Vo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * @Author Dali
 * @Date 2021/12/12 10:44
 * @Version 1.0
 * @Description: 与web访问相关的controller
 */
@Controller
public class IndexController {

	@Autowired
	CategoryService categoryService;

	/**
	 * 无论我们是访问：
	 * <p>
	 * [http://localhost:10000/]
	 * 还是
	 * [http://localhost:10000/index.html（暂时不显示，需要我们做映射）]
	 * 都可以访问我们的首页
	 *
	 * @return
	 */
	@GetMapping({"/", "/index.html"})
	public String indexPage(Model model) {

		//TODO 1、查出所有的一级分类
		List<CategoryEntity> categoryEntities = categoryService.getLevel1Categorys();

		// 视图解析器进行拼串
		// 源码中含有默认后缀：classpath:/templates/
		// 源码中含有默认后缀：.html    然后进行拼串

		model.addAttribute("categorys", categoryEntities);
		return "index";         //所以我们只需返回一个index即可
	}

	//index/catalog.json   , Map是一个JSON对象？
	@ResponseBody
	@GetMapping("/index/catalog.json")
	public Map<String, List<Catelog2Vo>> getCatalogJson() {

		//查出所有的分类
		Map<String, List<Catelog2Vo>> catalogJson = categoryService.getCatalogJson();
		return catalogJson;
	}

	@ResponseBody
	@GetMapping("/hello")
	public String hello() {
		return "hello";
	}
}
