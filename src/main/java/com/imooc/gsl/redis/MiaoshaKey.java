package com.imooc.gsl.redis;

public class MiaoshaKey extends BasePrefix {

    private MiaoshaKey(String prefix) {
        super(prefix);
    }

    public static MiaoshaKey GOODS_OVER = new MiaoshaKey("go");
}
