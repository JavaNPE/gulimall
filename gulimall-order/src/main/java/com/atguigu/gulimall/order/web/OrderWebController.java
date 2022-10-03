package com.atguigu.gulimall.order.web;

import com.atguigu.gulimall.order.service.OrderService;
import com.atguigu.gulimall.order.vo.OrderConfirmVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author HedianTea
 * @email daki9981@qq.com
 * @Date 2022/9/15 16:37
 * @Description:
 */
@Controller
public class OrderWebController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/{page}/order.html")
    public String toPage(@PathVariable("page") String page) {
        return page;
    }

    @RequestMapping("/toTrade")
    public String toTrade(Model model) {
        OrderConfirmVo confirmVo = orderService.confirmOrder();
        model.addAttribute("confirmOrder", confirmVo);
        return "confirm";
    }

/*    @RequestMapping("/submitOrder")
    public String submitOrder(OrderSubmitVo submitVo, Model model, RedirectAttributes attributes) {
        try {
            SubmitOrderResponseVo responseVo = orderService.submitOrder(submitVo);
            Integer code = responseVo.getCode();
            if (code == 0) {
                model.addAttribute("order", responseVo.getOrder());
                return "pay";
            } else {
                String msg = "下单失败;";
                switch (code) {
                    case 1:
                        msg += "防重令牌校验失败";
                        break;
                    case 2:
                        msg += "商品价格发生变化";
                        break;
                }
                attributes.addFlashAttribute("msg", msg);
                return "redirect:http://order.gulimall.com/toTrade";
            }
        } catch (Exception e) {
            if (e instanceof NoStockException) {
                String msg = "下单失败，商品无库存";
                attributes.addFlashAttribute("msg", msg);
            }
            return "redirect:http://order.gulimall.com/toTrade";
        }
    }*/

    /**
     * 获取当前用户的所有订单
     *
     * @return
     */
/*
    @RequestMapping("/memberOrder.html")
    public String memberOrder(@RequestParam(value = "pageNum", required = false, defaultValue = "0") Integer pageNum,
                              Model model) {
        Map<String, Object> params = new HashMap<>();
        params.put("page", pageNum.toString());
        PageUtils page = orderService.getMemberOrderPage(params);
        model.addAttribute("pageUtil", page);
        return "list";
    }
*/

}

