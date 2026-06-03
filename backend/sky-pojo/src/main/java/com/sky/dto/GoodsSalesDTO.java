package com.sky.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "商品销量")
public class GoodsSalesDTO implements Serializable {
    //商品名称
    @Schema(description = "商品名称")
    private String name;

    //销量
    @Schema(description = "销量")
    private Integer number;
}
