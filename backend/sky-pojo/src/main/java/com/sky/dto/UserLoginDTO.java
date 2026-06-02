package com.sky.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * C端用户登录
 */
@Data
@ApiModel("用户登录")
public class UserLoginDTO implements Serializable {

    @ApiModelProperty(value = "登录code", required = true)
    @NotBlank(message = "code不能为空")
    private String code;

}
