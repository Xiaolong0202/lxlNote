package com.itheima.test;

import com.itheima.advice.MyAdvice;
import com.itheima.service.impl.UserServiceImpl;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class CGlibTest {

    public static void main(String[] args) {

        //CGlib基于父类（目标类）生成Proxy

        //目标对象
        Target target = new Target();
        //通知对象（增强对象）
        MyAdvice4 myAdvice4 = new MyAdvice4();

        //编写CGlib的代码
        Enhancer enhancer = new Enhancer();
        //设置父类
        enhancer.setSuperclass(Target.class);//生成的代理对象就是Target的子类
        //设置回调
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            //intercept方法相当于JDK的Proxy的invoke方法
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                myAdvice4.before();
                Object res = method.invoke(target, objects); //执行目标方法
                myAdvice4.after();
                return res;
            }
        });

        //生成代理对象
        Target proxy = (Target) enhancer.create();

        //测试
        proxy.show();

    }

}
