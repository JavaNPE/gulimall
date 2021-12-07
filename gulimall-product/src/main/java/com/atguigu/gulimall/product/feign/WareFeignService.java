package com.atguigu.gulimall.product.feign;

import com.atguigu.common.to.SkuHasStockVo;
import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @Author Dali
 * @Date 2021/12/5 11:40
 * @Version 1.0
 * @Description: 远程调用库存相关的接口服务
 */
@FeignClient("gulimall-ware")
public interface WareFeignService {

    /**
     * 1、R设计的时候可以加上泛型
     * 2、直接放回我们想要的结果
     * 3、自己封装返回结果
     *
     * @param skuIds
     * @return
     */
    @PostMapping("/ware/waresku/hasstock")
    R<List<SkuHasStockVo>> getSkusHashStock(@RequestBody List<Long> skuIds);
}
