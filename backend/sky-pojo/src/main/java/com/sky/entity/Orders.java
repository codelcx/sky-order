package com.sky.entity;

import com.sky.enumeration.OrderStatus;
import com.sky.enumeration.PaymentStatus;
import com.sky.validator.groups.Update;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Orders implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(groups = Update.class, message = "订单ID不能为空")
    private Long id;

    //订单号
    @NotBlank(message = "订单号不能为空")
    private String number;

    //订单状态
    @NotNull(message = "订单状态不能为空")
    private OrderStatus status;

    //下单用户id
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    //地址id
    @NotNull(message = "地址ID不能为空")
    private Long addressBookId;

    //下单时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "下单时间不能为空")
    private LocalDateTime orderTime;

    //结账时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkoutTime;

    //支付方式 1微信，2支付宝
    @NotNull(message = "支付方式不能为空")
    private Integer payMethod;

    //支付状态
    @NotNull(message = "支付状态不能为空")
    private PaymentStatus payStatus;

    //实收金额
    @NotNull(message = "实收金额不能为空")
    private BigDecimal amount;

    //备注
    private String remark;

    //用户名
    private String userName;

    //手机号
    private String phone;

    //地址
    private String address;

    //收货人
    private String consignee;

    //订单取消原因
    private String cancelReason;

    //订单拒绝原因
    private String rejectionReason;

    //订单取消时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime cancelTime;

    //预计送达时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime estimatedDeliveryTime;

    //配送状态  1立即送出  0选择具体时间
    @NotNull(message = "配送状态不能为空")
    private Integer deliveryStatus;

    //送达时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deliveryTime;

    //打包费
    private int packAmount;

    //餐具数量
    private int tablewareNumber;

    //餐具数量状态  1按餐量提供  0选择具体数量
    @NotNull(message = "餐具数量状态不能为空")
    private Integer tablewareStatus;
}
