package com.imooc.gsl.controller;


import com.imooc.gsl.domain.MiaoshaUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.imooc.gsl.result.Result;

/**
 * @auther guanyl on 2019-1-9.
 */

@Controller
@RequestMapping("/user")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @RequestMapping("/info")
    @ResponseBody
    public Result<MiaoshaUser> info(MiaoshaUser miaoshaUser) {
        return Result.success(miaoshaUser);
    }

}
