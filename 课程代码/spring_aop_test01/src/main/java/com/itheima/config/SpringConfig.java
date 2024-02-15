package com.itheima.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@ComponentScan("com.itheima")   //<context:component-scan base-package="com.itheima"/>
@EnableAspectJAutoProxy //<aop:aspectj-autoproxy/>
public class SpringConfig {
}
