package com.sky.entity;

import com.sky.validator.groups.Update;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "餐桌")
public class DinnerTable implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(groups = Update.class, message = "餐桌ID不能为空")
    private Long id;

    @Schema(description = "桌号")
    @NotNull(message = "桌号不能为空")
    private Integer tableNumber;

    @Schema(description = "座位数")
    @NotNull(message = "座位数不能为空")
    private Integer capacity;

    @Schema(description = "状态 0:空闲 1:使用中 2:已预订")
    @NotNull(message = "状态不能为空")
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
