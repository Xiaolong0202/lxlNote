package com.itheima.advice;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.aop.MethodBeforeAdvice;

import java.lang.reflect.Method;

public class MyAdvice3 implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        System.out.println("环绕前******");
        //执行目标方法
        Object res = methodInvocation.getMethod().invoke(methodInvocation.getThis(), methodInvocation.getArguments());
        System.out.println("环绕后******");
        return res;
    }
}
