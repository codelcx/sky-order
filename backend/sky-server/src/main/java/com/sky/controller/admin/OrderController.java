package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import org.springdoc.core.annotations.ParameterObject;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController("AdminOrderController")
@Slf4j
@Tag(name = "订单管理相关接口")
@Validated
@RequestMapping("/admin/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/conditionSearch")
    @Operation(summary = "订单搜素")
    public Result<PageResult<OrderVO>> getOrderListByCondition(@ParameterObject OrdersPageQueryDTO ordersPageQueryDTO) {
        log.info("后台订单管理分页查询，{}", ordersPageQueryDTO);
        PageResult<OrderVO> orderVOPageResult = orderService.getOrderListByCondition(ordersPageQueryDTO);
        return Result.success(orderVOPageResult);
    }

    @GetMapping("/details/{id}")
    @Operation(summary = "查询订单详情")
    public Result<OrderVO> getOrderDetail(@PathVariable @Parameter(description = "订单ID", required = true) Long id) {
        log.info("查询订单详情，ID：{}", id);
        OrderVO orderVO = orderService.getOrderDetail(id);
        return Result.success(orderVO);
    }

    @PutMapping("/confirm")
    @Operation(summary = "接单")
    public Result<?> confirmOrder(@RequestBody @Valid OrdersConfirmDTO confirmDTO) {
        log.info("商家接单：{}", confirmDTO);
        orderService.confirmOrder(confirmDTO.getId());
        return Result.success();
    }

    @GetMapping("/statistics")
    @Operation(summary = "订单状态数量统计")
    public Result<OrderStatisticsVO> statistics() {
        log.info("查询各个状态的订单数量");
        OrderStatisticsVO statisticsVO = orderService.statistics();
        return Result.success(statisticsVO);
    }

    @PutMapping("/rejection")
    @Operation(summary = "拒单")
    public Result<?> rejectOrder(@RequestBody @Valid OrdersRejectionDTO ordersRejectionDTO) {
        log.info("拒单，ID：{}，拒单原因：{}", ordersRejectionDTO.getId(), ordersRejectionDTO.getRejectionReason());
        orderService.rejectOrder(ordersRejectionDTO);
        return Result.success();
    }

    @PutMapping("/cancel")
    @Operation(summary = "取消订单")
    public Result<?> cancelOrder(@RequestBody @Valid OrdersCancelDTO ordersCancelDTO) {
        log.info("后台取消订单, ID: {}, 取消原因：{}", ordersCancelDTO.getId(), ordersCancelDTO.getCancelReason());
        orderService.adminCancelOrder(ordersCancelDTO);
        return Result.success();
    }

    @PutMapping("/delivery/{id}")
    @Operation(summary = "派送订单")
    public Result<?> deliveryOrder(@PathVariable @Parameter(description = "订单ID", required = true) Long id) {
        log.info("派送订单，ID：{}", id);
        orderService.deliveryOrder(id);
        return Result.success();
    }

    @PutMapping("/complete/{id}")
    @Operation(summary = "完成订单")
    public Result<?> completeOrder(@PathVariable @Parameter(description = "订单ID", required = true) Long id) {
        log.info("完成订单，ID：{}", id);
        orderService.completeOrder(id);
        return Result.success();
    }
}
