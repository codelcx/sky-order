package com.sky.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "分类查询模型")
public class CategoryPageQueryDTO implements Serializable {

    @Schema(description = "页码", requiredMode = Schema.RequiredMode.REQUIRED)
    private int page = 1;

    @Schema(description = "每页记录数", requiredMode = Schema.RequiredMode.REQUIRED)
    private int pageSize = 10;

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "分类类型（1:菜品分类 2:套餐分类）", allowableValues = "1, 2")
    @Range(min = 1L, max = 2L, message = "分类类型错误")
    private Integer type;
}
