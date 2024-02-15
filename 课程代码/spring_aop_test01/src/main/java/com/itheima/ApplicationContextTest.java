package com.itheima;

import com.itheima.config.SpringConfig;
import com.itheima.dao.UserDao;
import com.itheima.service.UserService;
import com.itheima.service.impl.UserServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ApplicationContextTest {

    public static void main(String[] args) {

        /*ApplicationContext app = new ClassPathXmlApplicationContext("applicationContext.xml");
        UserService bean = app.getBean(UserService.class);
        bean.show2();*/

        ApplicationContext app = new ClassPathXmlApplicationContext("applicationContext3.xml");
        UserService bean = app.getBean(UserService.class);
        bean.show2();

        /*ApplicationContext app = new AnnotationConfigApplicationContext(SpringConfig.class);
        UserService bean = app.getBean(UserService.class);
        bean.show2();*/

    }

}
