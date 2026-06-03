package com.sky.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * C端用户登录
 */
@Data
@Schema(description = "用户登录")
public class UserLoginDTO implements Serializable {

    @Schema(description = "登录code", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "code不能为空")
    private String code;

}
