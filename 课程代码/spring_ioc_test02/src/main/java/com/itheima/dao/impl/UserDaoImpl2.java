package com.itheima.dao.impl;

import com.itheima.dao.UserDao;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;


@Repository("userDao2")
//@Primary
public class UserDaoImpl2 implements UserDao {

    @Override
    public void show() {

    }

}
