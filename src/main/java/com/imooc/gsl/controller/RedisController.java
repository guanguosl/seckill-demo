package com.imooc.gsl.controller;

import com.imooc.gsl.domain.UserDTO;
import com.imooc.gsl.redis.RedisService;
import com.imooc.gsl.redis.UserKey;
import com.imooc.gsl.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/demo")
public class RedisController {


    @Autowired
    RedisService redisService;

    @RequestMapping("/redis/get")
    @ResponseBody
    public Result<UserDTO> getUser() {
        UserDTO userDTO = redisService.get(UserKey.getById,"1",UserDTO.class);
        return Result.success(userDTO);
    }

    @RequestMapping("/redis/set")
    @ResponseBody
    public Result<Boolean> setUser() {
        UserDTO userDTO=new UserDTO();
        userDTO.setId(1);
        userDTO.setName("guanyl1");
        redisService.set(UserKey.getById,"1",userDTO);
        return Result.success(true);
    }

}
