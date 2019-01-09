package com.imooc.gsl.redis;

public class OrderKey extends BasePrefix {

	public OrderKey(int expireSeconds, String prefix) {
		super(expireSeconds, prefix);
	}

}
