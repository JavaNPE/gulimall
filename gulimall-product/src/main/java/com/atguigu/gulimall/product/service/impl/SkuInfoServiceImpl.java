package com.atguigu.gulimall.product.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.SkuInfoDao;
import com.atguigu.gulimall.product.entity.SkuInfoEntity;
import com.atguigu.gulimall.product.service.SkuInfoService;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuInfo(SkuInfoEntity skuInfoEntity) {
        this.baseMapper.insert(skuInfoEntity);
    }

    /**
     * http://localhost:88/api/product/skuinfo/list?t=1636882465490&page=2&limit=10&key=&catelogId=0&brandId=0&min=0&max=0
     *
     * @param params
     * @return
     */
    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> queryWrapper = new QueryWrapper<>();
        /**
         * key:
         * catelogId: 0
         * brandId: 0
         * min: 0
         * max: 0
         */

        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            // sku_id=1 and (sku_id=1 or sku_name like xxx),因为上面用了and()的方式拼接，拼接的条件在and()括号里面。
            // 若是使用and()进行拼接的话catelog_id x=1 and sku_id=1 or sku_name like xxx 会有恒成立的问题
            //==>  Preparing: SELECT * FROM pms_sku_info WHERE (catalog_id = ? AND brand_id = ? AND price >= ? AND price <= ?) LIMIT ?,?
            //==>  Preparing: SELECT * FROM pms_sku_info WHERE (( (sku_id = ? OR sku_name LIKE ?) ) AND catalog_id = ? AND brand_id = ? AND price >= ? AND price <= ?) LIMIT ?,?
            queryWrapper.and((wapper) -> {
                wapper.eq("sku_id", key).or().like("sku_name", key);
            });
        }
        String catelogId = (String) params.get("catelogId");
        if (!StringUtils.isEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)) {
            queryWrapper.eq("catalog_id", catelogId);
        }
        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(catelogId)) {
            queryWrapper.eq("brand_id", brandId);
        }
        String min = (String) params.get("min");
        if (!StringUtils.isEmpty(min)) {
            //https://blog.csdn.net/qq1009798402/article/details/112232435
            //ge 就是 greater than or equal 大于等于
            queryWrapper.ge("price", min);
        }
        String max = (String) params.get("max");
        if (!StringUtils.isEmpty(max)) {
            try {
                BigDecimal bigDecimal = new BigDecimal(max);
                //max的值与0比较 如果==1则表示fullPrice的值比0大
                if (bigDecimal.compareTo(new BigDecimal("0")) == 1) {
                    //le 就是 less than or equal 小于等于
                    queryWrapper.le("price", max);
                }
            } catch (Exception e) {

            }
        }

        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                queryWrapper
        );
        return new PageUtils(page);
    }

    @Override
    public List<SkuInfoEntity> getSkusBySpuId(Long spuId) {
        /**
         * 查询集合信息
         */
        List<SkuInfoEntity> list = this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
        return list;
    }
}