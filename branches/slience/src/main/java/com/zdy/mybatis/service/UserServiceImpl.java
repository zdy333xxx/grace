/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zdy.mybatis.service;

import com.zdy.mybatis.dao.UserDao;
import com.zdy.mybatis.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 *
 * @author breeze
 */
@Scope("prototype")
@Service("userService")
public class UserServiceImpl implements UserService {

    //@Value("HelloWorld")
    //@Resource(name ="userDao")
    @Autowired
    @Qualifier("userDao")
    private UserDao userDao;

    /*
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
    */

    @Override
    public User doSomething(User user) {
        return this.userDao.getUserById(user);
    }

}
