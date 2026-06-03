package com.sky.service.state;

import com.sky.entity.Orders;
import com.sky.enumeration.OrderEvent;
import com.sky.enumeration.OrderStatus;
import com.sky.exception.OrderBusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;

@Service
public class OrderStateContext {
    private Orders order;
    private IOrderState<OrderStatus, OrderEvent> iOrderState;
    private StateMachine<OrderStatus, OrderEvent> stateMachine;

    @Autowired
    private PendingPaymentState pendingPaymentState;

    @Autowired
    private ToBeConfirmedState toBeConfirmedState;

    @Autowired
    private ConfirmedState confirmedState;

    @Autowired
    private CanceledState canceledState;

    @Autowired
    private DeliveryState deliveryState;

    @Autowired
    private CompleteState completeState;

    public void init(Orders order, StateMachine<OrderStatus, OrderEvent> stateMachine) {
        this.order = order;
        this.stateMachine = stateMachine;
        OrderStatus orderStatus = OrderStatus.fromState(order.getStatus());
        switch (orderStatus) {
            case PENDING_PAYMENT: // 待付款状态
                this.iOrderState = pendingPaymentState;
                break;
            case TO_BE_CONFIRMED: // 待接单状态
                this.iOrderState = toBeConfirmedState;
                break;
            case CONFIRMED: // 已接单状态
                this.iOrderState = confirmedState;
                break;
            case DELIVERY_IN_PROGRESS:
                this.iOrderState = deliveryState; // 派送中状态
                break;
            case COMPLETED:
                this.iOrderState = completeState; // 已完成状态
                break;
            case CANCELLED:
                this.iOrderState = canceledState;  // 已取消状态
                break;
            default:
                throw new OrderBusinessException("不存在的订单状态");
        }
    }

    public void pay() { iOrderState.pay(order, stateMachine); }

    public void userCancel() {
        iOrderState.userCancel(order, stateMachine);
    }

    public void confirmOrder() {
        iOrderState.confirmOrder(order, stateMachine);
    }

    public void adminCancel(String cancelReason) {
        iOrderState.adminCancel(order, stateMachine, cancelReason);
    }

    public void delivery(){
        iOrderState.delivery(order, stateMachine);
    }

    public void complete() {
        iOrderState.complete(order, stateMachine);
    }
}
