package com.sky.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
public class OrdersRejectionDTO implements Serializable {

    @Schema(description = "订单ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "订单ID不能为空")
    private Long id;

    //订单拒绝原因
    @Schema(description = "拒绝原因", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "拒绝原因不能为空")
    private String rejectionReason;

}
