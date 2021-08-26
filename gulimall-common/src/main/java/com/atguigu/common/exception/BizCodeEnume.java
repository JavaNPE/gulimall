package com.atguigu.common.exception;

/**
 * @Author Dali
 * @Date 2021/8/26 12:19
 * @Version 1.0
 * @Description 错误码和错误信息定义类
 * 1.错误码定义规则为5为数字
 * 2.前两位表示业务场景，最后三位表示错误码。例如: 100001。 10:通 用001:系统未知
 * 异常
 * 3.维护错误码后需要维护错误描述，将他们定义为枚举形式
 * 错误码列表:
 * 10:通用
 * -----001:参数格式校验I
 * 11:商品
 * 12:订单
 * 13:购物车
 * 14:物流
 */
public enum BizCodeEnume {
    UNKNOW_EXCEPTION(10000, "系统未知异常"),
    VALID_EXCEPTION(10001, "参数格式校验失败");

    private int code;
    private String msg;

    BizCodeEnume(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
