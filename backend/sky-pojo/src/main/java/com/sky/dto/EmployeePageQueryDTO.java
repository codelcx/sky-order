package com.sky.dto;

import lombok.Data;

import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "员工列表查询对象")
public class EmployeePageQueryDTO implements Serializable {

    //员工姓名
    @Schema(description = "员工姓名")
    private String name;

    //页码
    @Schema(description = "页码")
    private int page = 1;

    //每页显示记录数
    @Schema(description = "每页数量")
    private int pageSize = 10;

}
