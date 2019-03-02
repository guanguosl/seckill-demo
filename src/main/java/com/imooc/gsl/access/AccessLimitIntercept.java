package com.imooc.gsl.access;

import com.imooc.gsl.constants.MiaoshaUserConstants;
import com.imooc.gsl.domain.MiaoshaUser;
import com.imooc.gsl.redis.AccessKey;
import com.imooc.gsl.redis.RedisService;
import com.imooc.gsl.result.CodeMsg;
import com.imooc.gsl.service.MiaoshaUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class AccessLimitIntercept extends HandlerInterceptorAdapter {

    @Autowired
    MiaoshaUserService miaoshaUserService;

    @Autowired
    RedisService redisService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (handler instanceof HandlerMethod) {
            //获取用户
            MiaoshaUser miaoshaUser = this.getMiaoshaUser(request, response);
            UserContext.setUserHolder(miaoshaUser);
            HandlerMethod hm = (HandlerMethod) handler;
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            if (accessLimit == null) {
                return true;
            }
            //获取配置信息
            int seconds = accessLimit.seconds();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();

            String uri = request.getRequestURI();
            String key = uri;

            if (needLogin) {
                if (ObjectUtils.isEmpty(miaoshaUser)) {
                    WebUtils.render(response, CodeMsg.SESSION_ERROR);
                    return false;
                }
                key += "_" + miaoshaUser.getId();
            }
            //添加请求限制
            AccessKey ak = AccessKey.withExpire(seconds);
            Integer count = redisService.get(ak, key, Integer.class);
            if (count == null) {
                redisService.set(ak, key, 1);
            } else if (count < maxCount) {
                redisService.incr(ak, key);
            } else {
                WebUtils.render(response, CodeMsg.ACCESS_LIMIT_REACHED);
                return false;
            }
        }


        return true;
    }

    private MiaoshaUser getMiaoshaUser(HttpServletRequest request, HttpServletResponse response) {
        String cookieToken = this.getCookie(request);
        String paramToken = request.getParameter(MiaoshaUserConstants.COOKIE_NAME_TOKEN);
        if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
//            return "login";
            return null;
        }
        String token = StringUtils.isEmpty(paramToken) ? cookieToken : paramToken;
        MiaoshaUser miaoshaUser = miaoshaUserService.getByToken(token);
        if (ObjectUtils.isEmpty(miaoshaUser)) {
            return null;
        }
        return miaoshaUser;
    }

    private String getCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
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
