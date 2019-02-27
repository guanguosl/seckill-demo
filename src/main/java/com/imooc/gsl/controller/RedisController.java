package com.imooc.gsl.controller;

import com.imooc.gsl.domain.User;
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
    public Result<User> getUser() {
        User user = redisService.get(UserKey.getById,"1", User.class);
        return Result.success(user);
    }

    @RequestMapping("/redis/set")
    @ResponseBody
    public Result<Boolean> setUser() {
        User user =new User();
        user.setId(1);
        user.setName("guanyl1");
        redisService.set(UserKey.getById,"1", user);
        return Result.success(true);
    }

}
