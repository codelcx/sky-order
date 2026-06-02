package com.sky.enumeration;

import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.Getter;

@Getter
public enum PaymentStatus implements IEnum<Integer> {

    UN_PAID(0, "未支付"),
    PAID(1, "已支付"),
    REFUND(2, "退款");

    private final Integer code;
    private final String desc;

    PaymentStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public Integer getValue() {
        return this.code;
    }

    public static PaymentStatus fromCode(Integer code) {
        for (PaymentStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知支付状态: " + code);
    }

}
