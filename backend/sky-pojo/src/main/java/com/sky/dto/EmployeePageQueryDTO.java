package com.sky.dto;

import lombok.Data;

import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "员工列表查询对象")
public class EmployeePageQueryDTO implements Serializable {

    @Schema(description = "员工姓名")
    private String name;

    @Schema(description = "页码")
    private int page = 1;

    @Schema(description = "每页数量")
    private int pageSize = 10;

}
