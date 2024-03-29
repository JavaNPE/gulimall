package com.atguigu.gulimall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.constant.ProductConstant;
import com.atguigu.common.to.SkuHasStockVo;
import com.atguigu.common.to.SkuReductionTo;
import com.atguigu.common.to.SpuBoundTo;
import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.product.dao.SpuInfoDao;
import com.atguigu.gulimall.product.entity.AttrEntity;
import com.atguigu.gulimall.product.entity.BrandEntity;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.entity.ProductAttrValueEntity;
import com.atguigu.gulimall.product.entity.SkuImagesEntity;
import com.atguigu.gulimall.product.entity.SkuInfoEntity;
import com.atguigu.gulimall.product.entity.SkuSaleAttrValueEntity;
import com.atguigu.gulimall.product.entity.SpuInfoDescEntity;
import com.atguigu.gulimall.product.entity.SpuInfoEntity;
import com.atguigu.gulimall.product.feign.CouponFeignService;
import com.atguigu.gulimall.product.feign.SearchFeignService;
import com.atguigu.gulimall.product.feign.WareFeignService;
import com.atguigu.gulimall.product.service.AttrService;
import com.atguigu.gulimall.product.service.BrandService;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.service.ProductAttrValueService;
import com.atguigu.gulimall.product.service.SkuImagesService;
import com.atguigu.gulimall.product.service.SkuInfoService;
import com.atguigu.gulimall.product.service.SkuSaleAttrValueService;
import com.atguigu.gulimall.product.service.SpuImagesService;
import com.atguigu.gulimall.product.service.SpuInfoDescService;
import com.atguigu.gulimall.product.service.SpuInfoService;
import com.atguigu.gulimall.product.vo.Attr;
import com.atguigu.gulimall.product.vo.BaseAttrs;
import com.atguigu.gulimall.product.vo.Bounds;
import com.atguigu.gulimall.product.vo.Images;
import com.atguigu.gulimall.product.vo.Skus;
import com.atguigu.gulimall.product.vo.SpuSaveVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {
    @Autowired
    SpuInfoDescService spuInfoDescService;

    @Autowired
    SpuImagesService imagesService;

    @Autowired
    AttrService attrService;

    @Autowired
    ProductAttrValueService attrValueService;

    @Autowired
    SkuInfoService skuInfoService;

    @Autowired
    SkuImagesService skuImagesService;

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    /**
     * 为了能够使用远程服务，我们需要注入CouponFeignService接口
     * 帮我们调用远程的所用功能
     */
    @Autowired
    CouponFeignService couponFeignService;

    /**
     * 为了远程调用品牌服务，查询相应的品牌名称等信息
     */
    @Autowired
    BrandService brandService;

    /**
     * 为了远程调用商品分类服务，查询相应的分类名称等信息
     */
    @Autowired
    CategoryService categoryService;

    @Autowired
    WareFeignService wareFeignService;  //远程调用

    @Autowired
    SearchFeignService searchFeignService;  //远程调用


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 保存前端传送的spu信息，可能会保存很多，所以我们使用事务注解@Transactional
     * TODO 高级部分逐步完善
     *
     * @param vo
     */
    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVo vo) {
        //1、保存spu基本信息：pms_spu_info
        //操作pms_spu_info表需要提前创建一个与其对应的实体类
        SpuInfoEntity infoEntity = new SpuInfoEntity();
        //进行属性对拷
        BeanUtils.copyProperties(vo, infoEntity);
        infoEntity.setCreateTime(new Date());
        infoEntity.setUpdateTime(new Date());
        this.saveBaseSpuInfo(infoEntity);

        //2、保存spu的描述图片：spm_spu_info_desc
        List<String> decript = vo.getDecript();
        SpuInfoDescEntity descEntity = new SpuInfoDescEntity();
        descEntity.setSpuId(infoEntity.getId());
        //使用join对其进行分割操作
        descEntity.setDecript(String.join(",", decript));
        //保存spu的描述图片信息
        spuInfoDescService.saveSpuInfoDesc(descEntity);

        //3、保存spu的规格参数：pms_product_attr_value
        List<String> images = vo.getImages();
        imagesService.saveImages(infoEntity.getId(), images);

        //4、保存spu的规格参数：pms_product_attr_value
        List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
        List<ProductAttrValueEntity> collect = baseAttrs.stream().map(attr -> {
            ProductAttrValueEntity valueEntity = new ProductAttrValueEntity();
            valueEntity.setAttrId(attr.getAttrId());
            /**
             * attrName:如果用户不上传  我们需要去表中查询
             */
            AttrEntity id = attrService.getById(attr.getAttrId());
            valueEntity.setAttrName(id.getAttrName());
            valueEntity.setAttrValue(attr.getAttrValues());
            valueEntity.setQuickShow(attr.getShowDesc());
            valueEntity.setSpuId(infoEntity.getId());

            return valueEntity;
        }).collect(Collectors.toList());

        attrValueService.saveProductAttr(collect);

        //5、保存spu的积分信息：gulimall_sms ——》 sms_spu_bounds （跨库）
        Bounds bounds = vo.getBounds(); //页面提交的积分
        SpuBoundTo spuBoundTo = new SpuBoundTo();   //数据传输对象
        BeanUtils.copyProperties(bounds, spuBoundTo);
        spuBoundTo.setSpuId(infoEntity.getId());

        //调用远程服务，结束之后我们最好需判断一下调用结果，日志记录。
        R r = couponFeignService.saveSpuBounds(spuBoundTo);
        if (r.getCode() != 0) {
            log.error("远程保存spu积分信息失败");
        }

        //5、保存当前spu对应的所有sku信息(需要跨库操作)
        List<Skus> skus = vo.getSkus();
        if (skus != null && skus.size() > 0) {
            skus.forEach(item -> {
                /**
                 * 获取默认图片
                 */
                String defaultImg = ""; //默认图片
                for (Images image : item.getImages()) {
                    if (image.getDefaultImg() == 1) {     //1: 表示默认图片
                        defaultImg = image.getImgUrl();
                    }
                }
                //    private String skuName;
                //    private BigDecimal price;
                //    private String skuTitle;
                //    private String skuSubtitle;
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(item, skuInfoEntity);
                skuInfoEntity.setBrandId(infoEntity.getBrandId());  //品牌id
                skuInfoEntity.setCatalogId(infoEntity.getCatalogId());  //三级分类id
                skuInfoEntity.setSaleCount(0L); //销量默认值0
                skuInfoEntity.setSpuId(infoEntity.getId());
                skuInfoEntity.setSkuDefaultImg(defaultImg);   //默认图片
                /**
                 * 5.1 保存sku的基本信息：pms_sku_info
                 */
                skuInfoService.saveSkuInfo(skuInfoEntity);
                /**
                 * sku的自增主键
                 */
                Long skuId = skuInfoEntity.getSkuId();


                /**
                 * 获取所有图片:
                 * 图片的保存是先保存sku 才保存图片的，所以获取默认图片要在保存sku之前获取
                 */
                List<SkuImagesEntity> imagesEntities = item.getImages().stream().map(img -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgUrl(img.getImgUrl());
                    skuImagesEntity.setDefaultImg(img.getDefaultImg());
                    return skuImagesEntity;
                }).filter(entity -> {
                    //返回true就是需要，返回false就会过滤掉（剔除）
                    return !StringUtils.isEmpty(entity.getImgUrl());
                }).collect(Collectors.toList());
                /**
                 * 5.2 保存sku的图片信息：pms_sku_images
                 */
                skuImagesService.saveBatch(imagesEntities);
                //TODO 没有图片路径的，无需保存
                /**
                 * 5.3 保存sku的销售属性信息：pms_sku_sale_attr_value
                 */
                List<Attr> attr = item.getAttr();
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attr.stream().map(a -> {
                    SkuSaleAttrValueEntity attrValueEntity = new SkuSaleAttrValueEntity();
                    //如果属性值相同的话可以进行对拷
                    BeanUtils.copyProperties(a, attrValueEntity);
                    attrValueEntity.setSkuId(skuId);

                    return attrValueEntity;
                }).collect(Collectors.toList());
                //5.3 保存sku的销售属性信息：pms_sku_sale_attr_value
                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);

                //调用远程服务
                //5.4 保存sku的优惠，满减等信息：gulimall_sms数据库中的sms_sku_ladder\sms_sku_full_reduction\sms_member_price
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(item, skuReductionTo);
                skuReductionTo.setSkuId(skuId);

                //fullPrice与0比较 如果返回1则表示fullPrice的值比0大
                if (skuReductionTo.getFullCount() > 0 || skuReductionTo.getFullPrice().compareTo(new BigDecimal(0)) == 1) {
                    //调用远程服务，结束之后我们最好需判断一下调用结果，日志记录。
                    R r1 = couponFeignService.saveSkuReduction(skuReductionTo);
                    if (r1.getCode() != 0) {
                        log.error("远程保sku优惠信息失败");
                    }
                }
            });
        }
    }

    @Override
    public void saveBaseSpuInfo(SpuInfoEntity infoEntity) {
        this.baseMapper.insert(infoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.and((w) -> {
                w.eq("id", key).or().like("spu_name", key);
            });
        }
        // status=1 and (id=1 or spu_name like xxx),因为上面用了and()的方式拼接，拼接的条件在and()括号里面。
        // 若是使用and()进行拼接的话statuse x=1 and id=1 or spu_name like xxx 会有恒成立的问题
        String status = (String) params.get("status");
        if (!StringUtils.isEmpty(status)) {
            wrapper.eq("publish_status", status);
        }
        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)) {
            wrapper.eq("brand_id", brandId);
        }
        String catelogId = (String) params.get("catelogId");
        if (!StringUtils.isEmpty(key) && !"0".equalsIgnoreCase(catelogId)) {
            wrapper.eq("catalog_id", catelogId);
        }
        /**
         * status: 1
         * key:
         * brandId: 2
         * catelogId: 225
         */
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void up(Long spuId) {
        //1、查出当前spuId对应的所有sku信息，品牌的名字。
        List<SkuInfoEntity> skus = skuInfoService.getSkusBySpuId(spuId);
        List<Long> skuIdList = skus.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());

        //TODO 4、查询当前sku的所有可以被用来检索（search_type：0否，1是）的规格属性。
        List<ProductAttrValueEntity> baseAttrs = attrValueService.baseAttrlistforspu(spuId);
        List<Long> attrIds = baseAttrs.stream().map(attr -> {
            return attr.getAttrId();
        }).collect(Collectors.toList());

        List<Long> searchAttrIds = attrService.selectSearchAttrIds(attrIds);
        //将List转成Set:无序不可重复
        Set<Long> idSet = new HashSet<>(searchAttrIds);

//        List<SkuEsModel.Attrs> attrs = new ArrayList<>();
        List<SkuEsModel.Attrs> attrsList = baseAttrs.stream().filter(item -> {
            return idSet.contains(item.getAttrId());
        }).map(item -> {
            SkuEsModel.Attrs attrs1 = new SkuEsModel.Attrs();
            BeanUtils.copyProperties(item, attrs1);
            return attrs1;
        }).collect(Collectors.toList());

        //hasStock,hotScore
        //TODO 1、发送远程调用，库存系统查询是否含有库存（hasStock）如果有：true,否则：false
        Map<Long, Boolean> stockMap = null;
        try {
            //因为是远程调用，会存在网络等问题导致远程调用失败，我们需要使用try进行捕获

            R skusHashStock = wareFeignService.getSkusHashStock(skuIdList);
//        List<SkuHasStockVo> data = skusHashStock.getData();
            TypeReference<List<SkuHasStockVo>> typeReference = new TypeReference<List<SkuHasStockVo>>() {
            };
            //将List转成Map（重要）
            stockMap = skusHashStock.getData(typeReference).stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId, item -> item.getHasStock()));

        } catch (Exception e) {
            log.error("库存服务查询异常：原因{}", e);
        }


        //2、封装每个sku的信息
        Map<Long, Boolean> finalStockMap = stockMap;
        List<SkuEsModel> upProducts = skus.stream().map(sku -> {
            //组装需要的数据
            SkuEsModel esModel = new SkuEsModel();
            /**
             * 将当前正在遍历的sku里面的数据，拷贝到esModel中
             */
            BeanUtils.copyProperties(sku, esModel);
            //对比SkuInfoEntity和SkuEsModel中的字段发现：skuPrice,skuImg,hasStock,hotScore等有些字段名称不一样，或者压根就没有以下字段，需要我们单独处理（查询）
            esModel.setSkuPrice(sku.getPrice());
            esModel.setSkuImg(sku.getSkuDefaultImg());
            //设置库存信息：hasStock,hotScore
            if (finalStockMap == null) {     //此时说明远程服务有问题： 默认有库存hasStock = true
                esModel.setHasStock(true);
            } else {
                esModel.setHasStock(finalStockMap.get(sku.getSkuId()));
            }

            //TODO 2、热度评分(hotScore)。新产品默认置：0 【暂时默认】
            esModel.setHotScore(0L);
            //TODO 3、查询品牌（brandName）和分类的名字(catalogName)信息。
            BrandEntity brand = brandService.getById(esModel.getBrandId());
            esModel.setBrandName(brand.getName());
            esModel.setBrandImg(brand.getLogo());

            CategoryEntity category = categoryService.getById(esModel.getCatalogId());
            esModel.setCatalogName(category.getName());
            //设置检索属性
            esModel.setAttrs(attrsList);

            /**
             *  private String brandName;
             *  private String brandImg;
             *  private String catalogName;
             *  private List<Attr> attrs;
             *
             *@Data
             *public static class Attr {
             *      private Long attrId;
             *      private String attrName;
             *      private String attrValue;
             *}
             */
            return esModel;
        }).collect(Collectors.toList());

        //TODO 5、将数据发给es进行保存：gulimall-search 【远程调用gulimall-search服务】
        R r = searchFeignService.productStatusUp(upProducts);
        if (r.getCode() == 0) {
            //远程调用成功
            //TODO 6、修改当前spu状态
            baseMapper.updateSpuStatus(spuId, ProductConstant.StatusEnum.SPU_UP.getCode());
        } else {
            //远程调用失败
            //TODO 7、重复调用？？ 接口幂等性：重试机制？？？（重要！！！）
            //Feign流程调用（含与那么解析）：https://www.bilibili.com/video/BV1np4y1C7Yf?p=134&spm_id_from=pageDriver
            /**
             * 1、构造请求数据，将对象转成json：
             *          RequestTemplate template = bui LdTemplateFromArgs. create (argv);
             * 2、发送请求进行执行(执行成功会解码响应数据)：
             *          executeAndDecode ( template);
             * 3、执行请求会有重试机制
             *      while(true){
             *      	try{
             *      		executeAndDecode(template);
             *             }catch {
             *      		try{retryer.continueOrPropagate(e);}catch() {throw es;}
             *                  continue;
             *         }
             *      }
             */
        }
    }


}