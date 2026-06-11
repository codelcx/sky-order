package com.sky.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Schema(description = "字典类型分页查询模型")
public class DictTypePageQueryDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "页码", requiredMode = Schema.RequiredMode.REQUIRED)
    private int page = 1;

    @Schema(description = "每页记录数", requiredMode = Schema.RequiredMode.REQUIRED)
    private int pageSize = 10;

    @Schema(description = "字典名称")
    private String name;

    @Schema(description = "字典编码")
    private String code;

    @Schema(description = "状态")
    private Integer status;
}
