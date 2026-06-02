package com.sky.dto;

import com.sky.entity.OrderDetail;
import com.sky.validator.groups.Update;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel("订单模型")
public class OrdersDTO implements Serializable {

    @ApiModelProperty("订单ID")
    @NotNull(groups = Update.class, message = "订单ID不能为空")
    private Long id;

    //订单号
    @ApiModelProperty("订单号")
    private String number;

    //订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消 7退款
    @ApiModelProperty(value = "订单状态", allowableValues = "1,2,3,4,5,6,7")
    private Integer status;

    //下单用户id
    @ApiModelProperty("用户ID")
    private Long userId;

    //地址id
    @ApiModelProperty("地址ID")
    private Long addressBookId;

    //下单时间
    @ApiModelProperty("下单时间")
    private LocalDateTime orderTime;

    //结账时间
    @ApiModelProperty("结账时间")
    private LocalDateTime checkoutTime;

    //支付方式 1微信，2支付宝
    @ApiModelProperty(value = "支付方式（1:微信 2:支付宝）", allowableValues = "1,2")
    private Integer payMethod;

    //实收金额
    @ApiModelProperty("实收金额")
    private BigDecimal amount;

    //备注
    @ApiModelProperty("备注")
    private String remark;

    //用户名
    @ApiModelProperty("用户名称")
    private String userName;

    //手机号
    @ApiModelProperty("手机号")
    private String phone;

    //地址
    @ApiModelProperty("地址")
    private String address;

    //收货人
    @ApiModelProperty("收货人")
    private String consignee;

    @ApiModelProperty("订单明细")
    private List<OrderDetail> orderDetails;

}
