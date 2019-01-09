package com.imooc.gsl.service.impl;

import com.imooc.gsl.dao.UserDAO;
import com.imooc.gsl.domain.UserDTO;
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
    public UserDTO getUser(int i) {
        return userDAO.getUserById(i);
    }

    @Transactional
    @Override
    public boolean tx() {
        UserDTO userDTO=new UserDTO();
        userDTO.setId(2);
        userDTO.setName("2222222");
        userDAO.insert(userDTO);

        UserDTO userDTO1=new UserDTO();
        userDTO1.setId(1);
        userDTO1.setName("11111");
        userDAO.insert(userDTO);
        return true;
    }
}
