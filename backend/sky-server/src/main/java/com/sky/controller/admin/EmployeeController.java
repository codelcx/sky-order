package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import org.springdoc.core.annotations.ParameterObject;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.validator.groups.Update;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Tag(name = "员工相关接口")
@Validated
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;


    @PostMapping("/login")
    @Operation(summary = "员工登录")
    public Result<EmployeeLoginVO> login(@RequestBody @Valid EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    @PostMapping("/logout")
    @Operation(summary = "退出登录")
    public Result<String> logout() {
        return Result.success();
    }

    @PostMapping
    @Operation(summary = "新增员工")
    public Result<?> save(@RequestBody @Valid EmployeeDTO employeeDTO) {
        log.info("新增员工：{}", employeeDTO);
        return employeeService.save(employeeDTO) ? Result.success(null) : Result.error("添加失败");
    }

    @GetMapping("/page")
    @Operation(summary = "获取员工列表")
    public Result<PageResult<Employee>> listEmployee(@ParameterObject EmployeePageQueryDTO employeePageQueryDTO) {
        log.info("员工分页查询，参数为：{}", employeePageQueryDTO);
        PageResult<Employee> employeePageResult = employeeService.listEmployee(employeePageQueryDTO);
        return Result.success(employeePageResult);
    }

    @PostMapping("/status/{status}")
    @Operation(summary = "修改员工状态")
    public Result<?> changeEmployeeStatus(
            @Parameter(description = "员工状态", required = true)
            @PathVariable
            @Range(max = 1L, message = "status不合法")
            @NotNull(message = "status不能为空")
            Integer status,

            @RequestParam
            @NotNull(message = "员工ID不能为空")
            @Parameter(description = "员工ID", required = true)
            Long id
    ) {
        log.info("员工修改状态，id：{}，状态值：{}", id, status);
        employeeService.changeEmployeeStatus(status, id);
        return Result.success();
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询员工信息")
    public Result<Employee> getEmployeeById(
            @PathVariable
            @Parameter(description = "员工ID", required = true)
            @NotNull(message = "员工ID不能为空")
            Long id
    ) {
        log.info("获取员工信息，id: {}", id);
        Employee employee = employeeService.getEmployeeById(id);
        return Result.success(employee);
    }

    @PutMapping
    @Operation(summary = "更新员工信息")
    public Result<?> updateEmployee(@RequestBody @Validated(Update.class) EmployeeDTO employeeDTO) {
        log.info("编辑员工信息：{}", employeeDTO);
        employeeService.updateEmployee(employeeDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除员工")
    public Result<?> deleteEmployee(
            @PathVariable
            @Parameter(description = "员工ID", required = true)
            @NotNull(message = "员工ID不能为空")
            Long id
    ) {
        log.info("删除员工，id: {}", id);
        employeeService.deleteEmployee(id);
        return Result.success();
    }
}
