package com.sky.dto;

import lombok.Data;

import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "购物车操作")
public class ShoppingCartDTO implements Serializable {

    @Schema(description = "菜品ID")
    private Long dishId;

    @Schema(description = "套餐ID")
    private Long setmealId;

    @Schema(description = "口味")
    private String dishFlavor;

}
