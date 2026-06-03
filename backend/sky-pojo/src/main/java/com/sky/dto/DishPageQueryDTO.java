package com.sky.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "菜品分页查询模版")
public class DishPageQueryDTO implements Serializable {

    @Schema(description = "页码")
    private int page = 1;

    @Schema(description = "每页数量")
    private int pageSize = 10;

    @Schema(description = "查询菜品名称")
    private String name;

    @Schema(description = "分类ID")
    private Integer categoryId;

    @Schema(description = "菜品状态（0:禁用 1:启用）", allowableValues = "0,1")
    @Range(max = 1L, message = "菜品状态不合法")
    private Integer status;

}
