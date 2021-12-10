package com.atguigu.gulimall.product.feign;

import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @Author Dali
 * @Date 2021/12/7 20:37
 * @Version 1.0
 * @Description
 */
@FeignClient("gulimall-search")
public interface SearchFeignService {
    /**
     * 远程调用gulimall-search服务，商品上架：远程接口（供商品服务：gulimall-product调用）
     *
     * @param skuEsModels
     * @return
     */
    @PostMapping("/search/save/product")
    R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels);
}
