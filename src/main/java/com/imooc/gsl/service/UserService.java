package com.imooc.gsl.service;

import com.imooc.gsl.domain.UserDTO;

/**
 * @auther guanyl on 2019-1-9.
 */
public interface UserService {
    UserDTO getUser(int i);

    boolean tx();
}
