package com.itheima.service.impl;

import com.itheima.service.UserService;
import org.springframework.stereotype.Service;

@Service("userService")
public class UserServiceImpl implements UserService {
    @Override
    public void show1() {
        System.out.println("show1....");
    }

    public void show1(String aaa) {
        System.out.println("show1....");
    }

    @Override
    public void show2() {
        System.out.println("show2....");
        //int i = 1/0;
    }

    public void show3() {
        System.out.println("show3....");
    }
}
