package com.imooc.gsl.controller;

import com.imooc.gsl.domain.UserDTO;
import com.imooc.gsl.result.Result;
import com.imooc.gsl.service.UserService;
import com.imooc.gsl.util.UUIDUtil;
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
//@RestControlle
@Controller
@RequestMapping("/demo")
public class SampleController {
    private static  final Logger LOGGER= LoggerFactory.getLogger(SampleController.class);
    @Autowired
    UserService userService;

    @RequestMapping("/thymeleaf")
    public String thymeleaf(Model model) {
        model.addAttribute("name", "guanguosl");
        return "hello";
    }

    @RequestMapping("/db/get")
    @ResponseBody
    public Result<UserDTO> getUser() {
        UserDTO userDTO = userService.getUser(1);
        return Result.success(userDTO);
    }

    @RequestMapping("/db/tx")
    @ResponseBody
    public Result<Boolean> tx() {
//        userService.tx();
        String uuid= UUIDUtil.uuid();
        LOGGER.info("uuid:{}",uuid);
        return Result.success(true);
    }
}
