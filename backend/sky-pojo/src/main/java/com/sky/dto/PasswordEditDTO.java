package com.sky.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel("密码修改")
public class PasswordEditDTO implements Serializable {

    //员工id
    @ApiModelProperty(value = "员工ID", required = true)
    @NotNull(message = "员工ID不能为空")
    private Long empId;

    //旧密码
    @ApiModelProperty(value = "旧密码", required = true)
    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;

    //新密码
    @ApiModelProperty(value = "新密码", required = true)
    @NotBlank(message = "新密码不能为空")
    private String newPassword;

}
