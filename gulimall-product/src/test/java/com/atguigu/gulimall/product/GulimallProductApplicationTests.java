package com.atguigu.gulimall.product;

import com.atguigu.gulimall.product.entity.BrandEntity;
import com.atguigu.gulimall.product.entity.SkuInfoEntity;
import com.atguigu.gulimall.product.service.BrandService;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.service.SkuInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    @Autowired
    SkuInfoService skuInfoService;

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
        ops.set("测试key", "World" + UUID.randomUUID().toString());

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
        //List<String> brandIds = Arrays.asList("1L", "2L", "3L", "4L");
        List<String> brandIds = Arrays.asList("0L");
        List<BrandEntity> brandEntityList = brandService.list(new QueryWrapper<BrandEntity>().in("brand_id", brandIds));
        BrandEntity brandEntity2 = brandEntityList.stream().filter(brandEntity1 -> brandEntity1.getBrandId() != 2).findFirst().orElse(null);
        // 如果没有查询出来数据的时候是否会报错
        List<Long> collect = brandEntityList.stream().map(BrandEntity::getBrandId).collect(Collectors.toList());
        Map<Long, BrandEntity> collect1 = brandEntityList.stream().collect(Collectors.toMap(BrandEntity::getBrandId, Function.identity(), (a, b) -> b));
        List<Long> longList = brandEntityList.stream().map(BrandEntity::getBrandId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(brandEntityList)) {
            BrandEntity brandEntity3 = brandEntityList.get(0);
            System.out.println(brandEntity3);
        }

        if (CollectionUtils.isEmpty(longList)) {
            System.out.println("________________null_____________");
        }
        System.out.println(brandEntity2);
        if (Objects.nonNull(brandEntity2)) {
            System.out.println("**************");
        } else {
            System.out.println("-------------------------");
        }
        System.out.println("****************************");
        Map<Long, BrandEntity> brandEntityMap = brandEntityList.stream().collect(Collectors.toMap(BrandEntity::getBrandId, Function.identity(), (o1, o2) -> o2));
        Set<Long> idLong = brandEntityMap.keySet();
        Iterator<Long> iterator = idLong.iterator();
        while (iterator.hasNext()) {
            Long key = iterator.next();
            BrandEntity brandEntity1 = brandEntityMap.get(key);
        }
    }

    @Test
    public void filterNullMapTest() {
        BrandEntity brandEntity = new BrandEntity();
        //List<String> brandIds = Arrays.asList("1L", "2L", "3L", "4L");
        List<String> brandIds = Arrays.asList("0L");
        List<BrandEntity> brandEntityList = brandService.list(new QueryWrapper<BrandEntity>().in("brand_id", brandIds));
        Map<Long, BrandEntity> entityMap = brandEntityList.stream().collect(Collectors.toMap(BrandEntity::getBrandId, map -> map));
        BrandEntity brandEntity2 = entityMap.get("");
        if (Objects.isNull(brandEntity2)) {
            System.out.println("--------------------------");
        }
        Long brandId = brandEntity2.getBrandId();
        if (brandId == null) {
            System.out.println("-----------------");
        }
        // 如果没有查询出来数据的时候是否会报错
        List<Long> collect = brandEntityList.stream().map(BrandEntity::getBrandId).collect(Collectors.toList());
        Map<Long, BrandEntity> collect1 = brandEntityList.stream().collect(Collectors.toMap(BrandEntity::getBrandId, Function.identity(), (a, b) -> b));
        List<Long> longList = brandEntityList.stream().map(BrandEntity::getBrandId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(brandEntityList)) {
            BrandEntity brandEntity3 = brandEntityList.get(0);
            System.out.println(brandEntity3);
        }

        if (CollectionUtils.isEmpty(longList)) {
            System.out.println("________________null_____________");
        }

        System.out.println("****************************");
        Map<Long, BrandEntity> brandEntityMap = brandEntityList.stream().collect(Collectors.toMap(BrandEntity::getBrandId, Function.identity(), (o1, o2) -> o2));
        Set<Long> idLong = brandEntityMap.keySet();
        Iterator<Long> iterator = idLong.iterator();
        while (iterator.hasNext()) {
            Long key = iterator.next();
            BrandEntity brandEntity1 = brandEntityMap.get(key);
        }
    }


    @Test
    public void filterNullMapFilterTest() {
        BrandEntity brandEntity = new BrandEntity();
        List<String> brandIds = Arrays.asList("1L", "2L", "3L", "4L");
        // List<String> brandIds = Arrays.asList("111L");
        List<BrandEntity> brandEntityList = brandService.list(new QueryWrapper<BrandEntity>().in("brand_id", brandIds));
        if (brandEntityList.size() == 1) {
            System.out.println("brandEntityList.size()");
        }
        if (CollectionUtils.isEmpty(brandEntityList)) {
            System.out.println("brandEntityList is empty!!!");
        }
        List<Long> longs = brandEntityList.stream()
                .filter(input -> input.getBrandId().equals(Long.valueOf("1")))
                .map(BrandEntity::getBrandId).collect(Collectors.toList());
        if (longs.contains(Long.valueOf("1"))) {
            System.out.println("JIJIJIJIJI");
        }
        if (CollectionUtils.isEmpty(longs)) {
            System.out.println("-----------");
        }


        List<Long> longList = brandEntityList.stream().filter(input -> input.getShowStatus().equals("0")).map(BrandEntity::getBrandId).collect(Collectors.toList());
        Map<Long, BrandEntity> entityMap = brandEntityList.stream().collect(Collectors.toMap(BrandEntity::getBrandId, map -> map));
        BrandEntity brandEntity2 = entityMap.get(null);
        Long brandId = brandEntity2.getBrandId();
        System.out.println(brandId);
    }

    @Test
    public void testNPE() {
        List<String> brandIds = Arrays.asList("607L");
        Long brandId = Long.valueOf("70");
        List<BrandEntity> brandEntityList = brandService.list(new QueryWrapper<BrandEntity>().in("brand_id", brandIds));
        BrandEntity brandEntity = brandService.getById(brandId);
        Long aLong = Optional.ofNullable(brandEntity.getBrandId()).orElse(null);
        System.out.println("aLong:" + aLong);


        Map<Long, BrandEntity> collect = brandEntityList.stream().filter(input -> input.getBrandId().equals("1")).collect(Collectors.toMap(BrandEntity::getBrandId, Function.identity(), (a, b) -> b));
        List<BrandEntity> list = brandEntityList.stream().filter(input -> "1".equals(input.getBrandId())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(list)) {
            System.out.println("-------------------------fefewfre");
        }
        if (brandEntityList.contains("1")) {
            System.out.println("_______________");
        }
    }

    @Test
    public void testNull() {
        SkuInfoEntity skuInfo = skuInfoService.getSkuBySpuId(Long.valueOf("1"));
        BigDecimal bigDecimal = Optional.ofNullable(skuInfo == null ? BigDecimal.ZERO : skuInfo.getPrice()).orElse(BigDecimal.ZERO);
        System.out.println("输出值：" + bigDecimal);
    }
}
