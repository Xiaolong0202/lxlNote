package com.itheima;

import com.itheima.beans.OtherBean2;
import com.itheima.config.SpringConfig;
import com.itheima.dao.UserDao;
import com.itheima.service.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ApplicationContextTest {

    public static void main(String[] args) {

        //System.setProperty("spring.profiles.active","test");

        //xml方式的Spring容器
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        //注解方式去加载Spring的核心配置类
        //ApplicationContext applicationContext = new AnnotationConfigApplicationContext(SpringConfig.class);
        /*UserService userService = applicationContext.getBean(UserService.class);
        userService.show();*/
        /*Object userDao = applicationContext.getBean("userDao2");
        System.out.println(userDao);*/
        /*UserService userService = applicationContext.getBean(UserService.class);
        userService.show();*/
        OtherBean2 bean = applicationContext.getBean(OtherBean2.class);
        System.out.println(bean);


    }

}
