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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "套餐")
public class Setmeal implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(groups = Update.class, message = "套餐ID不能为空")
    private Long id;

    @Schema(description = "分类ID")
    @NotNull(message = "分类不能为空")
    private Long categoryId;

    @Schema(description = "套餐名称")
    @NotBlank(message = "套餐名称不能为空")
    private String name;

    @Schema(description = "套餐价格")
    @NotNull(message = "套餐价格不能为空")
    private BigDecimal price;

    @Schema(description = "售卖状态（0:停用 1:启用）", allowableValues = "0,1")
    @NotNull(message = "售卖状态不能为空")
    private Integer status;

    @Schema(description = "描述信息")
    private String description;

    @Schema(description = "图片")
    private String image;

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
