package com.imooc.gsl.controller;

import com.imooc.gsl.component.MobileValidatorComponent;
import com.imooc.gsl.result.Result;
import com.imooc.gsl.service.MiaoshaUserService;
import com.imooc.gsl.vo.LoginVVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

/**
 * @auther guanyl on 2019-1-9.
 */

@Controller
@RequestMapping("/login")
public class LoginController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    MobileValidatorComponent mobileValidatorComponent;

    @Autowired
    MiaoshaUserService userService;

    @RequestMapping("/to_login")
    public String toLogin() {
        return "login";
    }

    @RequestMapping("/do_login")
    @ResponseBody
    public Result<String> doLogin(@Valid LoginVVo loginVVo) {
        log.info(loginVVo.toString());
        //登录
        String token = userService.login(loginVVo);
        return Result.success(token);
    }

}
