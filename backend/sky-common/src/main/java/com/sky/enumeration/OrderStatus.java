package com.sky.enumeration;

import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.Getter;

@Getter
public enum OrderStatus implements IEnum<Integer> {

    PENDING_PAYMENT(1, "待付款"),
    PENDING_CONFIRM(2, "待接单"),
    CONFIRMED(3, "已接单"),
    DELIVERING(4, "派送中"),
    COMPLETED(5, "已完成"),
    CANCELLED(6, "已取消"),
    REFUND(7, "退款");

    private final Integer code;
    private final String desc;

    OrderStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public Integer getValue() {
        return this.code;
    }

    public static OrderStatus fromCode(Integer code) {
        for (OrderStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知订单状态: " + code);
    }

}
