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
import java.time.LocalDateTime;

/**
 * 菜品及套餐分类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(groups = Update.class, message = "分类ID不能为空")
    private Long id;

    //类型: 1菜品分类 2套餐分类
    @NotNull(message = "类型不能为空")
    private Integer type;

    //分类名称
    @NotBlank(message = "分类名称不能为空")
    private String name;

    //顺序
    @NotNull(message = "排序号不能为空")
    private Integer sort;

    //分类状态 0标识禁用 1表示启用
    @NotNull(message = "分类状态不能为空")
    private Integer status;

    //创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    //更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    //创建人
    private Long createUser;

    //修改人
    private Long updateUser;
}
