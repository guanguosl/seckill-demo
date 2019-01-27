package com.imooc.gsl.redis;

public class GoodsKey extends BasePrefix{

	public static final int TOKEN_EXPIRE = 60;
	private GoodsKey(int expireSeconds, String prefix) {
		super(expireSeconds, prefix);
	}
	public static GoodsKey GOODS_LIST = new GoodsKey(TOKEN_EXPIRE, "gl");
}
