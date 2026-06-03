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

    @Schema(description = "订单号")
    private String number;

    @Schema(description = "订单状态", allowableValues = "1,2,3,4,5,6,7")
    private Integer status;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "地址ID")
    private Long addressBookId;

    @Schema(description = "下单时间")
    private LocalDateTime orderTime;

    @Schema(description = "结账时间")
    private LocalDateTime checkoutTime;

    @Schema(description = "支付方式（1:微信 2:支付宝）", allowableValues = "1,2")
    private Integer payMethod;

    @Schema(description = "实收金额")
    private BigDecimal amount;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "用户名称")
    private String userName;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "地址")
    private String address;

    @Schema(description = "收货人")
    private String consignee;

    @Schema(description = "订单明细")
    private List<OrderDetail> orderDetails;

}
