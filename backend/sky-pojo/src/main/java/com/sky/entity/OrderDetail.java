package com.sky.entity;

import com.sky.validator.groups.Update;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单明细
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetail implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(groups = Update.class, message = "明细ID不能为空")
    private Long id;

    //名称
    private String name;

    //订单id
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    //菜品id
    private Long dishId;

    //套餐id
    private Long setmealId;

    //口味
    private String dishFlavor;

    //数量
    @NotNull(message = "数量不能为空")
    private Integer number;

    //金额
    @NotNull(message = "金额不能为空")
    private BigDecimal amount;

    //图片
    private String image;
}
