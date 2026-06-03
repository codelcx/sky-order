package com.sky.dto;

import com.sky.entity.DishFlavor;
import com.sky.validator.groups.Update;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "菜品模版")
public class DishDTO implements Serializable {

    @Schema(description = "菜品id")
    @NotNull(groups = Update.class, message = "菜品ID不能为空")
    private Long id;

    @Schema(description = "菜品名称")
    @NotBlank(message = "菜品名称不能为空")
    private String name;

    @Schema(description = "菜品分类ID")
    @NotNull(message = "菜品分类ID不能为空")
    private Long categoryId;

    @Schema(description = "菜品价格")
    @NotNull(message = "菜品价格不能为空")
    @Range(message = "菜品价格不合法")
    private BigDecimal price;

    @Schema(description = "菜品图片")
    @NotBlank(message = "菜品图片不能为空")
    private String image;

    @Schema(description = "菜品描述")
    private String description;

    @Schema(description = "菜品状态（0:停售 1:起售）", allowableValues = "0,1")
    @Range(max = 1L, message = "菜品状态不合法")
    private Integer status;

    @Schema(description = "菜品口味")
    @Valid
    private List<DishFlavor> flavors = new ArrayList<>();

}
