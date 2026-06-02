package com.sky.entity;

import com.sky.validator.groups.Update;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.io.Serial;
import java.io.Serializable;

/**
 * 地址簿
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressBook implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(groups = Update.class, message = "地址ID不能为空")
    private Long id;

    //用户id
    private Long userId;

    //收货人
    @NotBlank(message = "收货人姓名不能为空")
    private String consignee;

    //手机号
    @NotBlank(message = "手机号不能为空")
    private String phone;

    //性别 0 女 1 男
    @Pattern(regexp = "[0-1]", message = "性别不合法")
    private String sex = "1";

    //省级区划编号
    @NotBlank(message = "provinceCode不能为空")
    private String provinceCode;

    //省级名称
    @NotBlank(message = "省级名称不能为空")
    private String provinceName;

    //市级区划编号
    @NotBlank(message = "cityCode不能为空")
    private String cityCode;

    //市级名称
    @NotBlank(message = "市级名称不能为空")
    private String cityName;

    //区级区划编号
    @NotBlank(message = "districtCode不能为空")
    private String districtCode;

    //区级名称
    @NotBlank(message = "区级名称不能为空")
    private String districtName;

    //详细地址
    @NotBlank(message = "详细地址不能为空")
    private String detail;

    //标签
    private String label;

    //是否默认 0否 1是
    @Range(max = 1L, message = "isDefault不合法")
    private Integer isDefault = 0;

    public String detailedAddress() {
        return provinceName + cityName + districtName + detail;
    }
}
