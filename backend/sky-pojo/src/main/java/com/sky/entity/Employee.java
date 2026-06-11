package com.sky.entity;

import com.sky.validator.groups.Update;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "员工信息")
public class Employee implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(groups = Update.class, message = "员工ID不能为空")
    private Long id;

    @Schema(description = "用户名")
    @NotBlank(message = "用户名不能为空")
    private String username;

    @Schema(description = "姓名")
    @NotBlank(message = "姓名不能为空")
    private String name;

    @Schema(description = "密码")
    @JsonIgnore
    @NotBlank(message = "密码不能为空")
    private String password;

    @Schema(description = "手机号")
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Schema(description = "性别（0:女 1:男）", allowableValues = "0,1")
    @Pattern(regexp = "[0-1]", message = "性别不合法")
    private String sex;

    @Schema(description = "身份证号")
    @NotBlank(message = "身份证号不能为空")
    @Pattern(regexp = "^\\d{15}|\\d{18}$", message = "身份证号不合法")
    private String idNumber;

    @Schema(description = "职业")
    private String job;

    @Schema(description = "地址")
    private String address;

    @Schema(description = "状态（0:禁用 1:启用）", allowableValues = "0,1")
    @NotNull(message = "状态不能为空")
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

}
