package com.sky.entity;

import com.sky.validator.groups.Update;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 套餐
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Setmeal implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(groups = Update.class, message = "套餐ID不能为空")
    private Long id;

    //分类id
    @NotNull(message = "分类不能为空")
    private Long categoryId;

    //套餐名称
    @NotBlank(message = "套餐名称不能为空")
    private String name;

    //套餐价格
    @NotNull(message = "套餐价格不能为空")
    private BigDecimal price;

    //状态 0:停用 1:启用
    @NotNull(message = "售卖状态不能为空")
    private Integer status;

    //描述信息
    private String description;

    //图片
    private String image;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    private Long createUser;

    private Long updateUser;
}
