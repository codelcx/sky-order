package com.sky.dto;

import com.sky.validator.groups.Add;
import com.sky.validator.groups.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Schema(description = "字典类型DTO")
public class DictTypeDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "字典类型ID")
    @NotNull(groups = Update.class, message = "字典类型ID不能为空")
    private Long id;

    @Schema(description = "字典名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "字典名称不能为空")
    private String name;

    @Schema(description = "字典编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "字典编码不能为空")
    private String code;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "排序")
    @NotNull(groups = Add.class, message = "排序不能为空")
    private Integer sort;

    @Schema(description = "状态 0:禁用 1:启用")
    private Integer status;
}
