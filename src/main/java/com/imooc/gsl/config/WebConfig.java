package com.imooc.gsl.config;

import com.imooc.gsl.access.AccessLimitIntercept;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

/**
 * @auther guanyl on 2019-1-23.
 * web配置
 */
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    @Autowired
    MiaoshaUserArgumentResolver miaoshaUserArgumentResolver;

    @Autowired
    AccessLimitIntercept accessLimitIntercept;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(miaoshaUserArgumentResolver);
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(accessLimitIntercept);
    }
}
