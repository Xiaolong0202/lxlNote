package com.itheima.processor;

import com.itheima.advice.MyAdvice;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class MockAopBeanPostProcessor implements BeanPostProcessor, ApplicationContextAware {

    private ApplicationContext applicationContext;

    /*@Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        //目的：对UserServiceImpl中的show1和show2方法进行增强，增强方法存在与MyAdvice中
        //问题1：筛选service.impl包下的所有的类的所有方法都可以进行增强，解决方案if-else
        //问题2：MyAdvice怎么获取到？解决方案：从Spring容器中获得MyAdvice

        if(bean.getClass().getPackage().getName().equals("com.itheima.service.impl")){
            //生成当前Bean的Proxy对象
            Object beanProxy = Proxy.newProxyInstance(
                    bean.getClass().getClassLoader(),
                    bean.getClass().getInterfaces(),
                    (Object proxy, Method method, Object[] args) -> {
                        MyAdvice myAdvice = applicationContext.getBean(MyAdvice.class);
                        //执行增强对象的before方法
                        myAdvice.beforeAdvice();
                        //执行目标对象的目标方法
                        Object result = method.invoke(bean, args);
                        //执行增强对象的after方法
                        myAdvice.afterAdvice();
                        return result;
                    }
            );

            return beanProxy;
        }

        return bean;
    }*/

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
