package com.sky.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "餐桌返回数据")
public class DinnerTableVO implements Serializable {

    @Schema(description = "餐桌ID")
    private Long id;

    @Schema(description = "桌号")
    private Integer tableNumber;

    @Schema(description = "座位数")
    private Integer capacity;

    @Schema(description = "状态 0:空闲 1:使用中 2:已预订")
    private Integer status;

    @Schema(description = "二维码图片URL")
    private String qrCodeUrl;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @Schema(description = "创建人")
    private Long createUser;

    @Schema(description = "修改人")
    private Long updateUser;
}
