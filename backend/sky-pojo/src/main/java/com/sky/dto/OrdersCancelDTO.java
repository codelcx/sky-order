package com.sky.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "订单取消")
public class OrdersCancelDTO implements Serializable {

    @Schema(description = "订单ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "订单ID不能为空")
    private Long id;

    //订单取消原因
    @Schema(description = "取消原因", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "取消原因不能为空")
    private String cancelReason;

}
