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
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "订单明细")
public class OrderDetail implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(groups = Update.class, message = "明细ID不能为空")
    private Long id;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "订单ID")
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    @Schema(description = "菜品ID")
    private Long dishId;

    @Schema(description = "套餐ID")
    private Long setmealId;

    @Schema(description = "口味")
    private String dishFlavor;

    @Schema(description = "数量")
    @NotNull(message = "数量不能为空")
    private Integer number;

    @Schema(description = "金额")
    @NotNull(message = "金额不能为空")
    private BigDecimal amount;

    @Schema(description = "图片")
    private String image;
}
