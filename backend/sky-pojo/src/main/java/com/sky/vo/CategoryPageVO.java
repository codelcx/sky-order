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
@Schema(description = "分类分页返回数据")
public class CategoryPageVO implements Serializable {

    @Schema(description = "分类ID")
    private Long id;

    @Schema(description = "类型（1:菜品分类 2:套餐分类）", allowableValues = "1,2")
    private Integer type;

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "排序号")
    private Integer sort;

    @Schema(description = "分类状态（0:禁用 1:启用）", allowableValues = "0,1")
    private Integer status;

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

    @Schema(description = "关联菜品数量")
    private Integer dishCount;

    @Schema(description = "关联套餐数量")
    private Integer setmealCount;
}
