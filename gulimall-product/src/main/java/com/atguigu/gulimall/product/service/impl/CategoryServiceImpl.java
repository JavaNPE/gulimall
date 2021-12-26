package com.atguigu.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.gulimall.product.service.CategoryBrandRelationService;
import com.atguigu.gulimall.product.vo.Catelog2Vo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

/*  //方式一：注入我们的  CategoryDao
    @Autowired
    CategoryDao categoryDao;
    //方式二：由于CategoryServiceImpl继承ServiceImpl 存在泛型 只需要使用 baseMapper即可
    */

	@Autowired
	CategoryService categoryService;

	@Autowired
	CategoryBrandRelationService categoryBrandRelationService;

	/**
	 * SpringBoot中使用redis做缓存
	 */
	@Autowired
	private StringRedisTemplate redisTemplate;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<CategoryEntity> page = this.page(
				new Query<CategoryEntity>().getPage(params),
				new QueryWrapper<CategoryEntity>()
		);

		return new PageUtils(page);
	}

	@Override
	public List<CategoryEntity> listWithTree() {
		//1、查出所有分类
		List<CategoryEntity> entities = baseMapper.selectList(null); //null表示没有查询条件，就表示查询所有

		//2、组装成父子的树形结构
		//方式二：(方式一：需要自动注入CategoryDao)由于CategoryServiceImpl继承ServiceImpl 存在泛型 只需要使用 baseMapper即可
		//2.1:找到所有的一级分类
		List<CategoryEntity> level1Menus = entities.stream().filter((categoryEntity) -> {  //建 Stream方式一：通过集合创建 (categoryEntity)的括号可以省略
			//Stream 的中间操作：filter(Predicate p) 接收 Lambda ， 从流中排除某些元素
			return categoryEntity.getParentCid() == 0;  //因为只有一条返回语句 return和{}也可以省略
			//Stream 的终止操作:收集:collect(Collector c)将流转换为其他形式。把流中元素收集到Lis，用于给Stream中元素做汇总的方法
		}).map((menu) -> {
			menu.setChildren(getChildrens(menu, entities));
			return menu;
		}).sorted((menu1, menu2) -> {
			//处理空指针异常
			return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
		}).collect(Collectors.toList());

		return level1Menus;
	}

	@Override
	public void removeMenuByIds(List<Long> asList) {
		//TODO 检查当前删除的菜单，是否被别的地方引用

		//逻辑删除
		baseMapper.deleteBatchIds(asList);
	}

	//[2,25,225]
	@Override
	public Long[] findCatelogPath(Long catelogId) {
		List<Long> paths = new ArrayList<>();
		List<Long> parentPath = findParentPath(catelogId, paths);//查出来的集合{225,34,2}是逆序的
		//使用Collections工具类将其逆序转换
		Collections.reverse(parentPath);    //完整路径：[2, 34, 225]

		return parentPath.toArray(new Long[parentPath.size()]); //将list集合转成long[]数组
	}

	/**
	 * 级联更新所有关联的数据
	 * 方式一：通过xml文件格式操作数据库写法：目前报错：{"msg":"参数格式校验失败","code":10001}
	 *
	 * @param category
	 */
	@Transactional  //这是一个事务= 更新自己还有更新级联的数据，所以加个事务
	@Override
	public void updateCascade(CategoryEntity category) {
		//首先更新自己:一般都是通过this的方式通过id更新
		this.updateById(category);    //视频中这么写的
//        categoryService.updateById(category);
		//更新关联表中的数据
		categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
	}

	/**
	 * 查询所有的一级分类：
	 * "pms_category"表中parent_cid=0（没有上一级分类）或者cat_level=1表示为1级分类
	 *
	 * @return
	 */
	@Override
	public List<CategoryEntity> getLevel1Categorys() {
		//selectList查询集合
		List<CategoryEntity> categoryEntities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));

		return categoryEntities;
	}


	/**
	 * P153、缓存-redis缓存使用-改造三级分类业务
	 *
	 * @return
	 */
	@Override
	public Map<String, List<Catelog2Vo>> getCatalogJson() {
		// 注意：给缓存中放json字符串，拿出的json字符串，还要逆转为能用的对象类型；【序列号与反序列化】

		// 1、加入缓存逻辑  get(key) | 注意：缓存中存的数据是json字符串
		// JSON的好处：其是跨语言，跨平台兼容
		String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
		if (StringUtils.isEmpty(catalogJSON)) {
			// 2、缓存中没有所需数据，那么就去查询数据库（从数据库中获取数据）
			Map<String, List<Catelog2Vo>> catalogJsonFromDb = getCatalogJsonFromDb();
			// 3、将从数据中查到的数据放入到缓存中去，注意：需要将查询出来的对象转成json然后在放在缓存中
			String s = JSON.toJSONString(catalogJsonFromDb);
			redisTemplate.opsForValue().set("catalogJSON", s);
			return catalogJsonFromDb;
		}

		// 逆转：转成我们指定的对象类型 Map<String, List<Catelog2Vo>>
		// JSON.parseObject引入这种类型： parse0bject(String text, TypeReference<T> type, Feature... features )T
		Map<String, List<Catelog2Vo>> result = JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<Catelog2Vo>>>() {
		});
		return result;
	}

	/**
	 * 查出所有的分类(仅业务逻辑优化)
	 * 从数据库中查询并封装整个分类数据（未使用redis做缓存）
	 *
	 * @return
	 */
	//@Override
	public Map<String, List<Catelog2Vo>> getCatalogJsonFromDb() {

		/**
		 * 1、将数据库的多次查询变成一次（150、性能压测优化优化三级分类数据获取）
		 */
		// 查询所有
		List<CategoryEntity> selectList = baseMapper.selectList(null);

		//1、查出所有1级分类
		List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);

		//2、封装数据【重要】
		Map<String, List<Catelog2Vo>> parent_cid = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
			//1、每一个的一级分类，查到这个一级分类的二级分类
			List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getCatId());

			//2、封装上面的结果
			List<Catelog2Vo> catelog2Vos = null;
			if (categoryEntities != null) {
				catelog2Vos = categoryEntities.stream().map(l2 -> {
					Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
					//1、找当前二级分类的三级分类，封装成vo
					List<CategoryEntity> level3Catelog = getParent_cid(selectList, l2.getCatId());
					if (level3Catelog != null) {
						List<Catelog2Vo.Catelog3Vo> collect = level3Catelog.stream().map(l3 -> {
							//2、封装成指定格式
							Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());

							return catelog3Vo;
						}).collect(Collectors.toList());

						catelog2Vo.setCatalog3List(collect);
					}

					return catelog2Vo;
				}).collect(Collectors.toList());
			}
			return catelog2Vos;
		}));
		return parent_cid;
	}

	private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList, Long parent_cid) {
		List<CategoryEntity> collect = selectList.stream()
				.filter(item -> item.getParentCid().equals(parent_cid))
				.collect(Collectors.toList());
		return collect;
		//return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));
	}

//    /**
//     * 级联更新所有关联的数据
//     * 方式二：通过mybatis-plus的方式直接修改数据库
//     * @param category
//     */
//    @Transactional  //这是一个事务= 更新自己还有更新级联的数据，所以加个事务
//    @Override
//    public void updateCascade(CategoryEntity category) {
//        //首先更新自己:一般都是通过this的方式通过id更新
//        this.updateById(category);    //视频中这么写的
////        categoryService.updateById(category);
//        //更新关联表中的数据
//        if (!StringUtils.isEmpty(category.getName())) {
//            categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
//        }
//    }

	//递归: 递归收集所有父分类， 比如我们现在是查手机{225,34,2}  手机/手机通讯/手机(225)
	private List<Long> findParentPath(Long catelogId, List<Long> paths) {
		//1、收集当前节点id
		paths.add(catelogId);
		CategoryEntity byId = this.getById(catelogId);
		if (byId.getParentCid() != 0) { //如果当前节点有父id
			findParentPath(byId.getParentCid(), paths);
		}
		return paths;
	}

	/**
	 * 递归查找获取某一个菜单的子菜单
	 * 结合：空指针异常的处理方式：使用三目运算符
	 *
	 * @return
	 */
	private List<CategoryEntity> getChildrens(CategoryEntity root, List<CategoryEntity> all) {//(当前菜单，所有菜单)
		List<CategoryEntity> children = all.stream().filter(categoryEntity -> {
			return categoryEntity.getParentCid().equals(root.getCatId());
		}).map(categoryEntity -> {
			//1、找到子菜单
			categoryEntity.setChildren(getChildrens(categoryEntity, all));
			return categoryEntity;
		}).sorted((menu1, menu2) -> {
			//2、菜单的排序：处理空指针异常
			return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
		}).collect(Collectors.toList());
		return children;

	}
}