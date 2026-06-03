package com.sky.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "订单查询模型")
public class OrdersPageQueryDTO implements Serializable {

    @Schema(description = "页码", requiredMode = Schema.RequiredMode.REQUIRED)
    @Range(min = 1, max = Integer.MAX_VALUE, message = "页码错误")
    private int page = 1;

    @Schema(description = "分页数量", requiredMode = Schema.RequiredMode.REQUIRED)
    @Range(min = 1, max = Integer.MAX_VALUE, message = "分页数量错误")
    private int pageSize = 10;

    @Schema(description = "订单号")
    private String number;

    @Schema(description = "手机号码")
    private String phone;

    @Schema(description = "订单状态", allowableValues = "1,2,3,4,5,6,7")
    @Range(min = 1, max = 7, message = "订单状态错误")
    private Integer status;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "下单时间")
    private LocalDateTime beginTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "下单时间")
    private LocalDateTime endTime;

    @Schema(description = "用户ID")
    private Long userId;

}
