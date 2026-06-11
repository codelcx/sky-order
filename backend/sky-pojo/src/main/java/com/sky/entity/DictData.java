package com.sky.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sky.validator.groups.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "字典数据")
public class DictData implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(groups = Update.class, message = "字典数据ID不能为空")
    private Long id;

    @Schema(description = "字典类型ID")
    private Long dictTypeId;

    @Schema(description = "字典标签")
    private String label;

    @Schema(description = "字典键值")
    private String value;

    @Schema(description = "是否默认 0:否 1:是")
    private Integer isDefault;

    @Schema(description = "状态 0:禁用 1:启用")
    private Integer status;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "备注")
    private String remark;

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
