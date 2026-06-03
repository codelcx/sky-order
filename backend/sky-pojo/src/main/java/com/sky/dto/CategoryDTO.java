package com.sky.dto;

import com.sky.validator.groups.Add;
import com.sky.validator.groups.Update;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "分类模版")
public class CategoryDTO implements Serializable {

    //主键
    @Schema(description = "分类ID")
    @NotNull(groups = Update.class, message = "分类ID不能为空")
    private Long id;

    //类型 1 菜品分类 2 套餐分类
    @Schema(description = "类型（1:菜品分类 2:套餐分类）", allowableValues = "1, 2", requiredMode = Schema.RequiredMode.REQUIRED)
    @Range(min = 1L, max = 2L, message = "分类类型有误，请输入1或2")
    @NotNull(groups = Add.class, message = "类型不能为空")
    private Integer type;

    //分类名称
    @Schema(description = "分类名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "分类名称不能为空")
    private String name;

    //排序
    @Schema(description = "排序值", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "排序值不能为空")
    private Integer sort;

}
