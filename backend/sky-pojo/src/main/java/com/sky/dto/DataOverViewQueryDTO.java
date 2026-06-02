package com.sky.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel("数据概览查询")
public class DataOverViewQueryDTO implements Serializable {

    @ApiModelProperty("开始时间")
    private LocalDateTime begin;

    @ApiModelProperty("结束时间")
    private LocalDateTime end;

}
