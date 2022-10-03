package com.atguigu.gulimall.order.vo;

import com.atguigu.gulimall.order.entity.OrderEntity;
import lombok.Data;

/***
 * @author HedianTea
 * @email daki9981@qq.com
 * @date 2022/9/15 16:07
 */
@Data
public class SubmitOrderResponseVo {

    private OrderEntity order;

    /**
     * 错误状态码
     **/
    private Integer code;
}
