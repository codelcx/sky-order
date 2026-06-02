package com.sky.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel("订单接单")
public class OrdersConfirmDTO implements Serializable {

    @ApiModelProperty(value = "订单ID", required = true)
    @NotNull(message = "订单ID不能为空")
    private Long id;

    //订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消 7退款
    @ApiModelProperty(value = "订单状态", allowableValues = "1,2,3,4,5,6,7", required = true)
    @NotNull(message = "订单状态不能为空")
    private Integer status;

}
