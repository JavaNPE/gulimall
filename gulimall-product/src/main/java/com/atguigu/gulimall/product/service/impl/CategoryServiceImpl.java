package com.atguigu.gulimall.product.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
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


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

/*  //方式一：注入我们的  CategoryDao
    @Autowired
    CategoryDao categoryDao;
    //方式二：由于CategoryServiceImpl继承ServiceImpl 存在泛型 只需要使用 baseMapper即可
    */

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