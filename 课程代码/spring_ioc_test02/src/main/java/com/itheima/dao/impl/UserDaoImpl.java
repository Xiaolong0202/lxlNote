package com.itheima.dao.impl;

import com.itheima.dao.UserDao;
import com.itheima.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;

//<bean id="userDao" class="com.itheima.dao.impl.UserDaoImpl"/>
//@Component("userDao")
@Repository("userDao")
//@Scope("singleton")
//@Lazy(false)
@Profile("test")
public class UserDaoImpl implements UserDao {

    @Value("${jdbc.driver}")
    private String username;

    //@Value("lisi")
    public void setUsername(String username){
        this.username = username;
    }

    @Override
    public void show() {
        //System.out.println(username);
    }

    /*public void setUsername(String username) {
        this.username = username;
    }*/


    /*public UserDaoImpl() {
        System.out.println("userDao创建");
    }

    @PostConstruct
    public void init(){
        System.out.println("userDao的初始化方法");
    }

    @PreDestroy
    public void destroy(){
        System.out.println("userDao的销毁方法");
    }*/


}
