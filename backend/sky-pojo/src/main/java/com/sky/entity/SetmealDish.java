package com.sky.entity;

import com.sky.validator.groups.Update;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "套餐菜品关系")
public class SetmealDish implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(groups = Update.class, message = "关系ID不能为空")
    private Long id;

    @Schema(description = "套餐ID")
    @NotNull(message = "套餐ID不能为空")
    private Long setmealId;

    @Schema(description = "菜品ID")
    @NotNull(message = "菜品ID不能为空")
    private Long dishId;

    @Schema(description = "菜品名称")
    private String name;

    @Schema(description = "菜品原价")
    @NotNull(message = "菜品原价不能为空")
    @Range(message = "菜品原价不合法")
    private BigDecimal price;

    @Schema(description = "份数")
    @NotNull(message = "菜品份数不能为空")
    @Range(min = 1L, message = "菜品份数不合法")
    private Integer copies;
}
