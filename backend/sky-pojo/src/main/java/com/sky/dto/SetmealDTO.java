package com.sky.dto;

import com.sky.entity.SetmealDish;
import com.sky.validator.groups.Update;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "套餐模版")
public class SetmealDTO implements Serializable {

    @Schema(description = "套餐ID")
    @NotNull(groups = Update.class, message = "套餐ID不能为空")
    private Long id;

    //分类id
    @Schema(description = "分类ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    //套餐名称
    @Schema(description = "套餐名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "套餐名称不能为空")
    private String name;

    //套餐价格
    @Schema(description = "套餐价格", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "套餐价格不能为空")
    @Range(message = "套餐价格错误")
    private BigDecimal price;

    //状态 0:停用 1:启用
    @Schema(description = "套餐状态（0:停用 1:启用）", allowableValues = "0, 1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "状态不能为空")
    @Range(max = 1L, message = "状态错误")
    private Integer status;

    //描述信息
    @Schema(description = "套餐描述")
    private String description;

    //图片
    @Schema(description = "套餐图片")
    @NotBlank(message = "套餐图片不能为空")
    private String image;

    //套餐菜品关系
    @Valid
    @NotEmpty(message = "套餐菜品不能为空")
    private List<SetmealDish> setmealDishes;

}
