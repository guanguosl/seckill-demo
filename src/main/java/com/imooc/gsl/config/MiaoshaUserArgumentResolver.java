package com.imooc.gsl.config;

import com.imooc.gsl.constants.MiaoshaUserConstants;
import com.imooc.gsl.domain.MiaoshaUser;
import com.imooc.gsl.service.MiaoshaUserService;
import com.imooc.gsl.util.RequestHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @auther guanyl on 2019-1-23.
 */
@Component
public class MiaoshaUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    MiaoshaUserService miaoshaUserService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> clazz = parameter.getParameterType();
        return clazz == MiaoshaUser.class;
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) throws Exception {
        //        HttpServletRequest httpServletRequest = (HttpServletRequest) webRequest.getNativeRequest();
        HttpServletRequest httpServletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        String cookieToken = this.getCookie();
        String paramToken = httpServletRequest.getParameter(MiaoshaUserConstants.COOKIE_NAME_TOKEN);
        if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
//            return "login";
            return null;
        }
        String token = StringUtils.isEmpty(paramToken) ? cookieToken : paramToken;
        MiaoshaUser miaoshaUser = miaoshaUserService.getByToken(token);
        if(ObjectUtils.isEmpty(miaoshaUser)){
            return null;
        }
        return miaoshaUser;

    }

    private String getCookie() {
        HttpServletRequest httpServletRequest = RequestHolder.getRequest();
        Cookie[] cookies = httpServletRequest.getCookies();
        if (ObjectUtils.isEmpty(cookies)) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (MiaoshaUserConstants.COOKIE_NAME_TOKEN.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
