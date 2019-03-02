package com.imooc.gsl.access;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AccessLimit {
    int seconds();

    int maxCount();

    boolean needLogin() default true;

}
