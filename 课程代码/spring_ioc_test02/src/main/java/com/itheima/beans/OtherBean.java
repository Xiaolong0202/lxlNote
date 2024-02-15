package com.itheima.beans;

import com.alibaba.druid.pool.DruidDataSource;
import com.itheima.dao.UserDao;
import com.itheima.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

//@Component
public class OtherBean {

    @Bean("dataSource")
    public DataSource dataSource(
            @Value("${jdbc.driver}") String driverClassName,
            @Qualifier("userDao2") UserDao userDao,
            UserService userService
    ){

        /*System.out.println(driverClassName);
        System.out.println(userDao);
        System.out.println(userService);*/


        DruidDataSource dataSource = new DruidDataSource();
        //设置4个基本参数 ...

        return dataSource;
    }

}
