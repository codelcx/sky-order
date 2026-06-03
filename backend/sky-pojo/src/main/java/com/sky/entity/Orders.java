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
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "订单")
public class Orders implements Serializable {

    /**
     * 订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消 7退款
     */
    public static final Integer PENDING_PAYMENT = 1;
    public static final Integer TO_BE_CONFIRMED = 2;
    public static final Integer CONFIRMED = 3;
    public static final Integer DELIVERY_IN_PROGRESS = 4;
    public static final Integer COMPLETED = 5;
    public static final Integer CANCELLED = 6;

    /**
     * 支付状态 0未支付 1已支付 2退款
     */
    public static final Integer UN_PAID = 0;
    public static final Integer PAID = 1;
    public static final Integer REFUND = 2;

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(groups = Update.class, message = "订单ID不能为空")
    private Long id;

    @Schema(description = "订单号")
    @NotBlank(message = "订单号不能为空")
    private String number;

    @Schema(description = "订单状态（1:待付款 2:待接单 3:已接单 4:派送中 5:已完成 6:已取消 7:退款）", allowableValues = "1,2,3,4,5,6,7")
    @NotNull(message = "订单状态不能为空")
    private Integer status;

    @Schema(description = "用户ID")
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @Schema(description = "地址ID")
    @NotNull(message = "地址ID不能为空")
    private Long addressBookId;

    @Schema(description = "下单时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "下单时间不能为空")
    private LocalDateTime orderTime;

    @Schema(description = "结账时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkoutTime;

    @Schema(description = "支付方式（1:微信 2:支付宝）", allowableValues = "1,2")
    @NotNull(message = "支付方式不能为空")
    private Integer payMethod;

    @Schema(description = "支付状态（0:未支付 1:已支付 2:退款）", allowableValues = "0,1,2")
    @NotNull(message = "支付状态不能为空")
    private Integer payStatus;

    @Schema(description = "实收金额")
    @NotNull(message = "实收金额不能为空")
    private BigDecimal amount;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "用户名")
    private String userName;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "地址")
    private String address;

    @Schema(description = "收货人")
    private String consignee;

    @Schema(description = "取消原因")
    private String cancelReason;

    @Schema(description = "拒绝原因")
    private String rejectionReason;

    @Schema(description = "取消时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime cancelTime;

    @Schema(description = "预计送达时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime estimatedDeliveryTime;

    @Schema(description = "配送状态（0:选择具体时间 1:立即送出）", allowableValues = "0,1")
    @NotNull(message = "配送状态不能为空")
    private Integer deliveryStatus;

    @Schema(description = "送达时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deliveryTime;

    @Schema(description = "打包费")
    private int packAmount;

    @Schema(description = "餐具数量")
    private int tablewareNumber;

    @Schema(description = "餐具数量状态（0:选择具体数量 1:按餐量提供）", allowableValues = "0,1")
    @NotNull(message = "餐具数量状态不能为空")
    private Integer tablewareStatus;
}
