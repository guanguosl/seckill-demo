package com.imooc.gsl.access;

import com.imooc.gsl.domain.MiaoshaUser;

/**
 * 设置线程变量
 */
public class UserContext {
    private static ThreadLocal<MiaoshaUser> userHolder = new ThreadLocal<MiaoshaUser>();

    public static MiaoshaUser getUserHolder() {
        return userHolder.get();
    }

    public static void setUserHolder(MiaoshaUser miaoshaUser) {
        userHolder.set(miaoshaUser);
    }
}
