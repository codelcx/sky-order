package com.sky.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel("订单取消")
public class OrdersCancelDTO implements Serializable {

    @ApiModelProperty(value = "订单ID", required = true)
    @NotNull(message = "订单ID不能为空")
    private Long id;

    //订单取消原因
    @ApiModelProperty(value = "取消原因", required = true)
    @NotNull(message = "取消原因不能为空")
    private String cancelReason;

}
