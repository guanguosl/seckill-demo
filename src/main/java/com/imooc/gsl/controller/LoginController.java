package com.imooc.gsl.controller;

import com.imooc.gsl.result.Result;
import com.imooc.gsl.service.UserService;
import com.imooc.gsl.vo.LoginVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @auther guanyl on 2019-1-9.
 */

@Controller
@RequestMapping("/login")
public class LoginController {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    UserService userService;

    @RequestMapping("/to_login")
    public String toLogin() {
        return "login";
    }

    @RequestMapping("/do_login")
    @ResponseBody
    public Result<Boolean> doLogin(LoginVO loginVO) {
        LOGGER.info("mobile:{},password:{}", loginVO.getMobile(), loginVO.getPassword());
        return Result.success(true);
    }

}
