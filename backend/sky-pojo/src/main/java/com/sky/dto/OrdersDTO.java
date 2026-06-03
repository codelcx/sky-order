package com.sky.dto;

import com.sky.entity.OrderDetail;
import com.sky.validator.groups.Update;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "订单模型")
public class OrdersDTO implements Serializable {

    @Schema(description = "订单ID")
    @NotNull(groups = Update.class, message = "订单ID不能为空")
    private Long id;

    //订单号
    @Schema(description = "订单号")
    private String number;

    //订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消 7退款
    @Schema(description = "订单状态", allowableValues = "1,2,3,4,5,6,7")
    private Integer status;

    //下单用户id
    @Schema(description = "用户ID")
    private Long userId;

    //地址id
    @Schema(description = "地址ID")
    private Long addressBookId;

    //下单时间
    @Schema(description = "下单时间")
    private LocalDateTime orderTime;

    //结账时间
    @Schema(description = "结账时间")
    private LocalDateTime checkoutTime;

    //支付方式 1微信，2支付宝
    @Schema(description = "支付方式（1:微信 2:支付宝）", allowableValues = "1,2")
    private Integer payMethod;

    //实收金额
    @Schema(description = "实收金额")
    private BigDecimal amount;

    //备注
    @Schema(description = "备注")
    private String remark;

    //用户名
    @Schema(description = "用户名称")
    private String userName;

    //手机号
    @Schema(description = "手机号")
    private String phone;

    //地址
    @Schema(description = "地址")
    private String address;

    //收货人
    @Schema(description = "收货人")
    private String consignee;

    @Schema(description = "订单明细")
    private List<OrderDetail> orderDetails;

}
