package com.atguigu.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;

/**
 * 品牌
 *
 * @author ÍõÈ½ê¿
 * @email daki9981@qq.com
 * @date 2021-08-13 19:53:21
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 品牌id
     */
    @TableId
    private Long brandId;
    /**
     * 品牌名
     */
    @NotBlank(message = "品牌名必须提交")
    private String name;
    /**
     * 品牌logo地址
     */
    @NotEmpty
    @URL(message = "logo必须是一个合法的url地址")
    private String logo;
    /**
     * 介绍
     */
    private String descript;
    /**
     * 显示状态[0-不显示；1-显示]
     */
    private Integer showStatus;
    /**
     * 检索首字母[a-zA-Z]
     */
    @NotEmpty
    @Pattern(regexp = "^[a-zA-Z]$", message = "检索首字母必须是一个字母")
    private String firstLetter;
    /**
     * 排序
     */
    @NotNull
    @Min(value = 0, message = "排序必须大于等于0")
    private Integer sort;

}
