package com.imooc.gsl.redis;

public interface KeyPrefix {
		
	public int expireSeconds();
	
	public String getPrefix();
	
}
