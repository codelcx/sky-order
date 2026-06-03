package com.sky.vo;

import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "订单详情返回数据")
public class OrderVO extends Orders implements Serializable {

    @Schema(description = "订单菜品信息")
    private String orderDishes;

    @Schema(description = "订单详情")
    private List<OrderDetail> orderDetailList;

}
