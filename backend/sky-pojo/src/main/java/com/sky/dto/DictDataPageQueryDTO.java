package com.sky.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Schema(description = "字典数据分页查询模型")
public class DictDataPageQueryDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "页码", requiredMode = Schema.RequiredMode.REQUIRED)
    private int page = 1;

    @Schema(description = "每页记录数", requiredMode = Schema.RequiredMode.REQUIRED)
    private int pageSize = 10;

    @Schema(description = "字典类型编码")
    private String dictTypeCode;

    @Schema(description = "字典标签")
    private String label;

    @Schema(description = "状态")
    private Integer status;
}
