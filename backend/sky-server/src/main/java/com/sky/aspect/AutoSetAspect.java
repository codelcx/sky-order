package com.sky.aspect;

import com.sky.annotation.AutoSet;
import com.sky.constant.AutoSetConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.SneakyThrows;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 切面类：自动填充公共字段（创建时间、更新时间、创建人、更新人）
 */
@Aspect
@Component
public class AutoSetAspect {

    /**
     * 切入点：匹配 com.sky.mapper 包下所有带有 @AutoSet 注解的方法
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoSet)")
    private void autoSetPointcut() {
    }

    /**
     * 前置通知：在目标方法执行前自动填充公共字段
     *
     * @param joinPoint 连接点，可获取目标方法的参数等信息
     */
    @SneakyThrows
    @Before("autoSetPointcut()")
    public void autoSet(JoinPoint joinPoint) {
        // 获取目标方法的 @AutoSet 注解，判断操作类型（INSERT / UPDATE）
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        AutoSet autoSet = signature.getMethod().getAnnotation(AutoSet.class);
        OperationType operationType = autoSet.value();

        // 获取方法第一个参数（实体对象）
        Object[] args = joinPoint.getArgs();
        if (args.length != 0) {
            Object o = args[0];

            // 当前时间 & 当前操作人 ID
            LocalDateTime now = LocalDateTime.now();
            Long empId = BaseContext.getCurrentId();

            // 反射获取实体中对应的 setter 方法
            Method setCreateTimeMethod = o.getClass().getDeclaredMethod(AutoSetConstant.SET_CREATE_TIME, LocalDateTime.class);
            Method setUpdateTimeMethod = o.getClass().getDeclaredMethod(AutoSetConstant.SET_UPDATE_TIME, LocalDateTime.class);
            Method setCreateUserMethod = o.getClass().getDeclaredMethod(AutoSetConstant.SET_CREATE_USER, Long.class);
            Method setUpdateUserMethod = o.getClass().getDeclaredMethod(AutoSetConstant.SET_UPDATE_USER, Long.class);

            // 插入操作：填充创建时间 & 创建人
            if (Objects.requireNonNull(operationType) == OperationType.INSERT) {
                setCreateTimeMethod.invoke(o, now);
                setCreateUserMethod.invoke(o, empId);
            }
            // 插入和更新都需填充：更新时间 & 更新人
            setUpdateTimeMethod.invoke(o, now);
            setUpdateUserMethod.invoke(o, empId);
        }

    }
}
