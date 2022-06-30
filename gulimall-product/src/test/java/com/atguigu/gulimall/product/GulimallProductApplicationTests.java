package com.atguigu.gulimall.product;

import com.atguigu.gulimall.product.entity.BrandEntity;
import com.atguigu.gulimall.product.service.BrandService;
import com.atguigu.gulimall.product.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimallProductApplicationTests {
	@Autowired
	BrandService brandService;

//    @Autowired
//    OSSClient ossClient;
//    OSS ossClient;

	@Autowired
	CategoryService categoryService;

	@Autowired
	StringRedisTemplate stringRedisTemplate;

	@Autowired
	RedissonClient redissonClient;

	/**
	 * 测试Redisson
	 */
	@Test
	public void redisson() {
		System.out.println(redissonClient);
	}

	/**
	 * 测试redis
	 */
	@Test
	public void stringRedisTemplate() {
		//key-> Hello  value-> World
		ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();

		// 保存数据操作
		ops.set("Hello", "World" + UUID.randomUUID().toString());

		// 查询操作
		String hello = ops.get("Hello");
		System.out.println("之前保存的数据是：" + hello);
	}

	@Test
	public void testFindPath() {
		Long[] catelogPath = categoryService.findCatelogPath(225L);//225是手机分类的
//        log.info("完整路径：{}", catelogPath);
		log.info("完整路径：{}", Arrays.asList(catelogPath));
	}

/*    @Test
    public void testUpload() throws FileNotFoundException {

*//*        // yourEndpoint填写Bucket所在地域对应的Endpoint。以华东1（杭州）为例，Endpoint填写为https://oss-cn-hangzhou.aliyuncs.com。
        String endpoint = "oss-cn-beijing.aliyuncs.com";

        // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
        String accessKeyId = "LTAI5tQJe43xkmKionyVWfkH";
        String accessKeySecret = "NYJesMLZJcVdFvhVx5bjEPw20vBe2z";

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);*//*

        // 填写本地文件的完整路径。如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件流。
        InputStream inputStream = new FileInputStream("D:\\FFOutput\\7.png");
        // 依次填写Bucket名称（例如examplebucket）和Object完整路径（例如exampledir/exampleobject.txt）。Object完整路径中不能包含Bucket名称。
        ossClient.putObject("gulimall-dali", "7.png", inputStream);

        // 关闭OSSClient。
        ossClient.shutdown();

        System.out.println("上传完成...");
    }*/

	@Test
	public void contextLoads() {
		BrandEntity brandEntity = new BrandEntity();
//        brandEntity.setBrandId(6L);
//        brandEntity.setDescript("华为天下第一");
//        brandService.updateById(brandEntity);


        /*
        brandEntity.setName("华为");
        brandService.save(brandEntity);
        System.out.println("保存成功.....");
        */


		List<BrandEntity> list = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id", 1L));
//        for (BrandEntity entity : list) {
//            System.out.println(entity);
//        }
		list.forEach((item) -> {
			System.out.println(item);
		});
	}

	@Test
	public void filterTest() {
		BrandEntity brandEntity = new BrandEntity();
		List<BrandEntity> brandEntityList = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id", 1L));
		BrandEntity brandEntity2 = brandEntityList.stream().filter(brandEntity1 -> StringUtils.isNotBlank(brandEntity1.getLogo())).findFirst().orElse(null);
		if (Objects.nonNull(brandEntity2)) {
			System.out.println("**************");
		} else {
			System.out.println("-------------------------");

		}
	}
}
