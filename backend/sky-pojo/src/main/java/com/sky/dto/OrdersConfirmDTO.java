package com.sky.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "订单接单")
public class OrdersConfirmDTO implements Serializable {

    @Schema(description = "订单ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "订单ID不能为空")
    private Long id;

    //订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消 7退款
    @Schema(description = "订单状态", allowableValues = "1,2,3,4,5,6,7", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "订单状态不能为空")
    private Integer status;

}
