package com.atguigu.gulimall.product.web;

import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.vo.Catelog2Vo;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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

	@Autowired
	StringRedisTemplate redisTemplate;

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
//		lock.lock();	// 阻塞式等待。 默认加的锁都是30s的时间
		//1)、锁的自动续期，如果业务超长，运行期间自动给锁续上新的30s。不用担心业务时间长，锁自动过期被删掉
		//2)、加锁的业务只要运行完成，就不会给当前锁续期，即使不手动解锁， 锁默认在30s以后自动删除。

		lock.lock(10, TimeUnit.SECONDS);    // 10秒过后自动解锁，自动解锁时间一定要大于业务的执行时间。
		// 问题：lock.lock(10, TimeUnit.SECONDS);在锁时间到了以后，不会自动续期。
		//1、如果我们传递了锁的超时时间，就发送给redis执行脚本，进行占锁，默认超时就是我们指定的时间
		//2、如果我们未指定锁的超时时间，就使用30 * 1000 【LockWatchdogTimeout看门狗的默认时间】;
		// 		只要占锁成功，就会启动一个定时任务[重新给锁设置过期时间，新的过期时间就是看门狗的默认时间],每隔10s都会自动再次续期，续成30秒
		// 		internalLockLeaseTime [看门狗时间] / 3, 10s

		//Redisson-lock最佳实战
		//1)、lock. lock(30, TimeUnit . SECONDS);省掉了整个续期操作。手动解锁
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

	/**
	 * 读写锁：保证一定能读到最新数据, 修改期间，写锁是一个排他锁(互斥锁，独享锁)。读锁是一个共享锁；
	 * 写锁没释放，读就必须等待。
	 *
	 * 如果是先写（锁）——>在读（锁）： 读的时候就必须等待写锁释放
	 * 读 ——> 读： 相当于无锁，并发读，只会在redis中记录好，所有当前的读锁。他们都会同时加锁成功
	 * 写 ——> 读： 等待写锁释放
	 * 写 ——> 写： 阻塞方式
	 * 读 ——> 写： 有读锁，写也需要等待。会不会写等待（会）？
	 * 总结：只要有写锁的存在，都必须等待。
	 * 测试读写锁：写锁
	 *
	 * @return
	 */
	@GetMapping("/write")
	@ResponseBody
	public String writeValue() {
		RReadWriteLock lock = redisson.getReadWriteLock("rw-lock");
		String s = "";
		// 获取写锁
		RLock rLock = lock.writeLock();
		try {
			// 读写锁的用法：1、改数据加写锁，读数据加读锁
			rLock.lock();
			System.out.println("写锁加锁成功..." + Thread.currentThread().getId());
			s = UUID.randomUUID().toString();
			Thread.sleep(30000);
			redisTemplate.opsForValue().set("writeValue", s);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			rLock.unlock();
			System.out.println("写锁释放" + Thread.currentThread().getId());
		}
		return s;
	}

	/**
	 * 测试读写锁：读锁
	 *
	 * @return
	 */
	@GetMapping("/read")
	@ResponseBody
	public String readValue() {
		RReadWriteLock lock = redisson.getReadWriteLock("rw-lock");
		String s = "";
		// 获取读锁
		RLock rLock = lock.readLock();
		rLock.lock();
		try {
			System.out.println("读锁加锁成功" + Thread.currentThread().getId());
			s = redisTemplate.opsForValue().get("writeValue");
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			rLock.unlock();
			System.out.println("读锁释放" + Thread.currentThread().getId());
		}
		return s;
	}
}
