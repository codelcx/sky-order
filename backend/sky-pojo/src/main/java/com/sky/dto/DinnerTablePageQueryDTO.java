package com.sky.dto;

import lombok.Data;

import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "餐桌分页查询模型")
public class DinnerTablePageQueryDTO implements Serializable {

    @Schema(description = "页码", requiredMode = Schema.RequiredMode.REQUIRED)
    private int page = 1;

    @Schema(description = "每页记录数", requiredMode = Schema.RequiredMode.REQUIRED)
    private int pageSize = 10;

    @Schema(description = "桌号")
    private Integer tableNumber;

    @Schema(description = "状态 0:空闲 1:使用中 2:已预订")
    private Integer status;
}
