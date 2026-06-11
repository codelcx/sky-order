package com.sky.dto;

import com.sky.validator.groups.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Schema(description = "字典数据DTO")
public class DictDataDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "字典数据ID")
    @NotNull(groups = Update.class, message = "字典数据ID不能为空")
    private Long id;

    @Schema(description = "字典类型ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "字典类型ID不能为空")
    private Long dictTypeId;

    @Schema(description = "字典标签", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "字典标签不能为空")
    private String label;

    @Schema(description = "字典键值", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "字典键值不能为空")
    private String value;

    @Schema(description = "是否默认 0:否 1:是")
    private Integer isDefault;

    @Schema(description = "排序")
    @NotNull(message = "排序不能为空")
    private Integer sort;

    @Schema(description = "状态 0:禁用 1:启用")
    private Integer status;

    @Schema(description = "备注")
    private String remark;
}
