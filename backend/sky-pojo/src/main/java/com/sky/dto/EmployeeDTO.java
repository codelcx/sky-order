package com.sky.dto;

import com.sky.validator.groups.Update;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "员工模型")
public class EmployeeDTO implements Serializable {

    @Schema(description = "ID")
    @NotNull(groups = Update.class, message = "员工ID不能为空")
    private Long id;

    @Schema(description = "登录账号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 8, message = "用户名长度应在 3 到 8 个字符之间")
    private String username;

    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "姓名不能为空")
    @Size(min = 2, max = 4, message = "姓名长度应在 2 到 4 个字符之间")
    private String name;

    @Schema(description = "手机号码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1(3[0-9]|5[0-3,5-9]|7[1-3,5-8]|8[0-9])\\d{8}$", message = "手机号码格式不正确")
    private String phone;

    @Schema(description = "性别（0:女 1:男）", allowableValues = "0, 1")
    @NotBlank(message = "性别不能为空")
    @Pattern(regexp = "[0-1]", message = "性别输入错误")
    private String sex;

    @Schema(description = "身份证号")
    @Pattern(regexp = "^[1-9][0-9]{16}[0-9xX]$", message = "身份证号格式有误")
    @NotBlank(message = "身份证号不能为空")
    private String idNumber;

    @Schema(description = "职业")
    private String job;

    @Schema(description = "地址")
    private String address;

}
