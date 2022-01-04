package com.atguigu.gulimall.product.web;

import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.vo.Catelog2Vo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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

	@Autowired
	RedissonClient redisson;

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
		// 基于Redis的Redisson分布式可重入锁RLock
		// Java对象实现了java.util.concurrent.locks.Lock接口。
		// 1、获取一把锁，只要锁的名字一样，就是同一把锁
		RLock lock = redisson.getLock("my-lock");

		// 2、加锁
		lock.lock();	// 阻塞式等待。 默认加的锁都是30s的时间
		//1)、锁的自动续期，如果业务超长，运行期间自动给锁续上新的30s。不用担心业务时间长，锁自动过期被删掉
		//2)、加锁的业务只要运行完成，就不会给当前锁续期，即使不手动解锁， 锁默认在30s以后自动删除。
		try {
			System.out.println("加锁成功，执行业务..." + Thread.currentThread().getId());
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			// 3、解锁	，加锁解锁代码没有运行，redisson会不会出现死锁
			System.out.println("释放锁..." + Thread.currentThread().getId());
			lock.unlock();
		}
		return "hello";
	}
}
