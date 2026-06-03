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
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "菜品口味")
public class DishFlavor implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(groups = Update.class, message = "口味ID不能为空")
    private Long id;

    @Schema(description = "菜品ID")
    @NotNull(message = "菜品ID不能为空")
    private Long dishId;

    @Schema(description = "口味名称")
    @NotBlank(message = "口味名称不能为空")
    private String name;

    @Schema(description = "口味数据")
    @NotBlank(message = "口味数据不能为空")
    private String value;

}
