package com.itheima.service.impl;

import com.itheima.dao.UserDao;
import com.itheima.mapper.UserMapper;
import com.itheima.pojo.User;
import com.itheima.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

//@Component("userService")
@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public void show() {
        List<User> all = userMapper.findAll();
        for (User user : all) {
            System.out.println(user);
        }
    }


    //@Autowired //根据类型进行注入,如果同一类型的Bean有多个，尝试根据名字进行二次匹配，匹配不成功在报错
    //@Qualifier("userDao2") //在此，结合@Autowired一起使用，作用是根据名称注入相应的Bean
    //@Resource(name="userDao2")  //不指定名称参数时，根据类型注入，指定名称就根据名称注入
    //private UserDao userDao;

    /*@Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }*/



    /*@Autowired
    public void xxx(UserDao userDao){
        //System.out.println("xxx:"+userDao);
    }*/

    /*@Autowired
    public void yyy(List<UserDao> userDaoList){
        //System.out.println("yyy:"+userDaoList);
    }*/



}
