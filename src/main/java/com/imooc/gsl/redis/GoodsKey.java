package com.imooc.gsl.redis;

public class GoodsKey extends BasePrefix{

	public static final int TOKEN_EXPIRE = 60;
	private GoodsKey(int expireSeconds, String prefix) {
		super(expireSeconds, prefix);
	}
	public static GoodsKey GOODS_LIST = new GoodsKey(TOKEN_EXPIRE, "gl");
	public static String GOODS_LIST_KEY="goodsList";

	public static GoodsKey GOODS_DETAIL = new GoodsKey(TOKEN_EXPIRE, "gd");
	public static String GOODS_DETAIL_KEY="goodsDetail";

	public static GoodsKey getMiaoshaGoodsStock = new GoodsKey(0,"gs");
	public static String GOODS_STOCK_KEY="goodsStock";
}
