package com.imooc.gsl.controller;

import com.imooc.gsl.domain.MiaoshaOrder;
import com.imooc.gsl.domain.MiaoshaUser;
import com.imooc.gsl.domain.OrderInfo;
import com.imooc.gsl.redis.RedisService;
import com.imooc.gsl.result.CodeMsg;
import com.imooc.gsl.service.GoodsService;
import com.imooc.gsl.service.MiaoshaService;
import com.imooc.gsl.service.MiaoshaUserService;
import com.imooc.gsl.service.OrderService;
import com.imooc.gsl.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/miaosha")
public class MiaoshaController {

	@Autowired
	MiaoshaUserService userService;
	
	@Autowired
	RedisService redisService;
	
	@Autowired
	GoodsService goodsService;
	
	@Autowired
	OrderService orderService;
	
	@Autowired
	MiaoshaService miaoshaService;

	/**
	 *
	 * @param model
	 * @param user
	 * @param goodsId
	 * @return
	 */
	// FIXME: 2019/1/27 高并发情况下出现秒杀库存小于0
    @RequestMapping(value = "/do_miaosha")
    public String list(Model model, MiaoshaUser user,
					   @RequestParam("goodsId")long goodsId) {
    	model.addAttribute("user", user);
    	if(user == null) {
    		return "login";
    	}
    	//判断库存
    	GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
    	int stock = goods.getStockCount();
    	if(stock <= 0) {
    		model.addAttribute("errmsg", CodeMsg.MIAO_SHA_OVER.getMsg());
    		return "miaosha_fail";
    	}
    	//判断是否已经秒杀到了
    	MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
    	if(order != null) {
    		model.addAttribute("errmsg", CodeMsg.REPEATE_MIAOSHA.getMsg());
    		return "miaosha_fail";
    	}
    	//减库存 下订单 写入秒杀订单
    	OrderInfo orderInfo = miaoshaService.miaosha(user, goods);
    	model.addAttribute("orderInfo", orderInfo);
    	model.addAttribute("goods", goods);
        return "order_detail";
    }
}
