package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.BusinessException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import java.util.Objects;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = DigestUtils.md5DigestAsHex(employeeLoginDTO.getPassword().getBytes());

        Employee employee = employeeMapper.getByUsername(username);

        // 处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        // 密码比对
        if (!password.equals(employee.getPassword())) {
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        // 账号被锁定
        if (Objects.equals(employee.getStatus(), StatusConstant.DISABLE)) {
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        return employee;
    }

    @Override
    public boolean save(EmployeeDTO employeeDTO) {
        // 寻找当前的要添加的用户名是否已被占用
        Employee em = employeeMapper.getByUsername(employeeDTO.getUsername());
        if (em != null) {
            throw new BusinessException(employeeDTO.getUsername() + "用户名已被注册");
        }

        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);

        // 设置默认密码
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));

        return employeeMapper.insertEmployee(employee) > 0;
    }

    @Override
    public PageResult<Employee> listEmployee(EmployeePageQueryDTO employeePageQueryDTO) {
        try (Page<Object> page = PageHelper.startPage(
                employeePageQueryDTO.getPage(),
                employeePageQueryDTO.getPageSize())) {

            Page<Employee> employeePage = page.doSelectPage(() ->
                    employeeMapper.listEmployee(employeePageQueryDTO)
            );

            return new PageResult<>(
                    employeePage.getTotal(),
                    employeePage.getResult(),
                    employeePage.getPageSize(),
                    employeePage.getPageNum()
            );
        }
    }

    @Override
    public void changeEmployeeStatus(Integer status, Long id) {
        Employee employee = getEmployeeById(id);

        employee.setStatus(status);
        employeeMapper.update(employee);
    }

    @Override
    public Employee getEmployeeById(Long id) {
        Employee employee = employeeMapper.getById(id);
        if (employee == null){
            throw new BusinessException("员工id不存在");
        }
        return employee;
    }

    @Override
    public void updateEmployee(EmployeeDTO employeeDTO) {
        Employee employee = getEmployeeById(employeeDTO.getId());

        BeanUtils.copyProperties(employeeDTO, employee);
        employeeMapper.update(employee);
    }

}
