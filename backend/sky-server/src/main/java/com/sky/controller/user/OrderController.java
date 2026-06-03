package com.sky.controller.user;

import com.sky.dto.OrdersPageQueryDTO;
import org.springdoc.core.annotations.ParameterObject;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController("UserOrderController")
@Slf4j
@Tag(name = "订单相关接口")
@Validated
@RequestMapping("/user/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/submit")
    @Operation(summary = "提交订单接口")
    public Result<OrderSubmitVO> submit(@RequestBody @Validated OrdersSubmitDTO ordersSubmitDTO) {
        log.info("提交订单: {}", ordersSubmitDTO);
        OrderSubmitVO submitVO = orderService.submit(ordersSubmitDTO);
        return Result.success(submitVO);
    }

    @PutMapping("/payment")
    @Operation(summary = "订单支付")
    public Result<OrderPaymentVO> payment(@RequestBody @Validated OrdersPaymentDTO ordersPaymentDTO) {
        log.info("订单支付：{}", ordersPaymentDTO);
        orderService.payment(ordersPaymentDTO);
        return Result.success();
    }

    @GetMapping("/historyOrders")
    @Operation(summary = "查询历史订单")
    public Result<PageResult<OrderVO>> getHistoryOrders(@ParameterObject OrdersPageQueryDTO ordersPageQueryDTO) {
        log.info("历史订单查询: {}", ordersPageQueryDTO);
        PageResult<OrderVO> orderVOPageResult = orderService.getHistoryOrders(ordersPageQueryDTO);
        return Result.success(orderVOPageResult);
    }

    @GetMapping("/orderDetail/{id}")
    @Operation(summary = "查询订单详情")
    public Result<OrderVO> getOrderDetail(@PathVariable @Parameter(description = "订单id", required = true) Long id) {
        log.info("查询订单详情，ID：{}", id);
        OrderVO orderVO = orderService.getUserOrderDetail(id);
        return Result.success(orderVO);
    }

    @PostMapping("/repetition/{id}")
    @Operation(summary = "再次下单")
    public Result<?> repeatOrder(@PathVariable @Parameter(description = "订单ID", required = true) Long id) {
        log.info("再次下单，ID：{}", id);
        orderService.repeatOrder(id);
        return Result.success();
    }

    @PutMapping("/cancel/{id}")
    @Operation(summary = "取消订单")
    public Result<?> cancelOrder(@PathVariable Long id) {
        log.info("取消订单，ID: {}", id);
        orderService.userCancelOrder(id);
        return Result.success();
    }

    @GetMapping("/reminder/{id}")
    @Operation(summary = "催单")
    public Result<?> remindOrder(@PathVariable Long id) {
        log.info("催单，ID：{}", id);
        orderService.remindOrder(id);
        return Result.success();
    }
}
