package com.imooc.gsl.service;

import com.imooc.gsl.domain.User;

/**
 * @auther guanyl on 2019-1-9.
 */
public interface UserService {
    User getUser(int i);

    boolean tx();
}
