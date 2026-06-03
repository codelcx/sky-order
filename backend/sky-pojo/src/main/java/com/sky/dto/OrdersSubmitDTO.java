package com.sky.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sky.annotation.validation.MultiFieldAssociationCheck;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "订单提交模型")
@MultiFieldAssociationCheck(when = "tablewareStatus == 0", must = "tablewareNumber != null", message = "餐具数量不能为空", errorField = "tablewareNumber")
public class OrdersSubmitDTO implements Serializable {

    //地址簿id
    @Schema(description = "地址ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "地址ID不能为空")
    private Long addressBookId;

    //付款方式, 默认为1：微信支付
    @Schema(description = "支付方式（1:微信 2:支付宝）", allowableValues = "1, 2", requiredMode = Schema.RequiredMode.REQUIRED)
    @Range(min = 1L, max = 2L, message = "支付方式错误")
    private int payMethod = 1;

    //备注
    @Schema(description = "订单备注")
    private String remark;

    //预计送达时间
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "预计送达时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "预计送达时间不能为空")
    private LocalDateTime estimatedDeliveryTime;

    //配送状态  1立即送出  0选择具体时间
    @Schema(description = "配送状态（0:选择具体时间 1:立即送出）", allowableValues = "0, 1", requiredMode = Schema.RequiredMode.REQUIRED)
    @Range(max = 1L, message = "配送状态不合法")
    private Integer deliveryStatus = 1;

    //餐具数量
    @Schema(description = "餐具数量", requiredMode = Schema.RequiredMode.REQUIRED)
    @Range(message = "餐具数量错误")
    private Integer tablewareNumber;

    //餐具数量状态  1按餐量提供  0选择具体数量
    @Schema(description = "餐具数量状态（0:选择具体数量 1:按餐量提供）", allowableValues = "0,1", requiredMode = Schema.RequiredMode.REQUIRED)
    @Range(max = 1L, message = "餐具数量状态不合法")
    private Integer tablewareStatus = 1;

    //打包费
    @Schema(description = "打包费", requiredMode = Schema.RequiredMode.REQUIRED)
    @Range(max = Integer.MAX_VALUE, message = "打包费错误")
    @NotNull(message = "打包费不能为空")
    private Integer packAmount;

    //总金额
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)  // 表示只进行序列化，不进行反序列化
    private BigDecimal amount;
}
