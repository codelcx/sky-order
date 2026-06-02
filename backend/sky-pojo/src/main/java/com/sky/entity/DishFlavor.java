package com.sky.entity;

import com.sky.validator.groups.Update;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;

/**
 * 菜品口味
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DishFlavor implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(groups = Update.class, message = "口味ID不能为空")
    private Long id;

    //菜品id
    @NotNull(message = "菜品ID不能为空")
    private Long dishId;

    //口味名称
    @NotBlank(message = "口味名称不能为空")
    private String name;

    //口味数据list
    @NotBlank(message = "口味数据不能为空")
    private String value;

}
