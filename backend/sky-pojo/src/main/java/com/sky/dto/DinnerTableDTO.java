package com.sky.dto;

import com.sky.validator.groups.Update;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "餐桌DTO")
public class DinnerTableDTO implements Serializable {

    @Schema(description = "餐桌ID")
    @NotNull(groups = Update.class, message = "餐桌ID不能为空")
    private Long id;

    @Schema(description = "桌号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "桌号不能为空")
    private Integer tableNumber;

    @Schema(description = "座位数", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "座位数不能为空")
    private Integer capacity;

    @Schema(description = "状态 0:空闲 1:使用中 2:已预订")
    private Integer status;
}
