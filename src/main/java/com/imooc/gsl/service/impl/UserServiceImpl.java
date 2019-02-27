package com.imooc.gsl.service.impl;

import com.imooc.gsl.dao.UserDAO;
import com.imooc.gsl.domain.User;
import com.imooc.gsl.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @auther guanyl on 2019-1-9.
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserDAO userDAO;

    @Override
    public User getUser(int i) {
        return userDAO.getUserById(i);
    }

    @Transactional
    @Override
    public boolean tx() {
        User user =new User();
        user.setId(2);
        user.setName("2222222");
        userDAO.insert(user);

        User user1 =new User();
        user1.setId(1);
        user1.setName("11111");
        userDAO.insert(user);
        return true;
    }
}
