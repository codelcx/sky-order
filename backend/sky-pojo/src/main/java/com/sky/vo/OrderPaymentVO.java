package com.sky.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "订单支付返回数据")
public class OrderPaymentVO implements Serializable {

    @Schema(description = "随机字符串")
    private String nonceStr;
    @Schema(description = "签名")
    private String paySign;
    @Schema(description = "时间戳")
    private String timeStamp;
    @Schema(description = "签名算法")
    private String signType;
    @Schema(description = "统一下单接口返回的prepay_id参数值")
    private String packageStr;

}
