package com.sky.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "套餐分页查询模版")
public class SetmealPageQueryDTO implements Serializable {

    @Schema(description = "页码", requiredMode = Schema.RequiredMode.REQUIRED)
    private int page = 1;

    @Schema(description = "每页记录数", requiredMode = Schema.RequiredMode.REQUIRED)
    private int pageSize = 10;

    @Schema(description = "套餐名称")
    private String name;

    @Schema(description = "分类ID")
    private Integer categoryId;

    @Schema(description = "套餐起售状态（0:禁用 1:启用）", allowableValues = "0, 1")
    @Range(max = 1L, message = "套餐起售状态错误")
    private Integer status;

}
