package com.atguigu.common.constant;

/**
 * @Author Dali
 * @Date 2021/10/2 18:06
 * @Version 1.0
 * @Description: 数据库表：pms_attr 表中attr_type字段：属性类型[0-销售属性，1-基本属性]
 */
public class ProductConstant {
    public enum AttrEnum {
        ATTR_TYPE_BASE(1, "基本属性"), ATTR_TYPE_SALE(0, "销售属性");

        private int code;
        private String msg;

        AttrEnum(int code, String msg) {
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
}
