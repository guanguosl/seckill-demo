package com.imooc.gsl.controller;

import com.imooc.gsl.domain.MiaoshaOrder;
import com.imooc.gsl.domain.MiaoshaUser;
import com.imooc.gsl.domain.OrderInfo;
import com.imooc.gsl.rabbitmq.MQSender;
import com.imooc.gsl.rabbitmq.MiaoshaMessage;
import com.imooc.gsl.redis.GoodsKey;
import com.imooc.gsl.redis.MiaoshaKey;
import com.imooc.gsl.redis.RedisService;
import com.imooc.gsl.result.CodeMsg;
import com.imooc.gsl.result.Result;
import com.imooc.gsl.service.GoodsService;
import com.imooc.gsl.service.MiaoshaService;
import com.imooc.gsl.service.MiaoshaUserService;
import com.imooc.gsl.service.OrderService;
import com.imooc.gsl.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.imooc.gsl.redis.GoodsKey.GOODS_STOCK_KEY;


@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean {

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

    @Autowired
    MQSender sender;

    private Map<Long, Boolean> overMap = new HashMap<Long, Boolean>();

    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsVoList = goodsService.listGoodsVo();
        if (ObjectUtils.isEmpty(goodsVoList)) {
            return;
        }
        for (GoodsVo goodsVo : goodsVoList) {
            redisService.set(GoodsKey.GOODS_STOCK, GOODS_STOCK_KEY + goodsVo.getId(), goodsVo.getStockCount());
            overMap.put(goodsVo.getId(), false);
        }
    }

    /**
     * QPS:1306
     * 5000 * 10
     * */
    /**
     * GET POST有什么区别？
     */
    @RequestMapping(value = "/do_miaosha", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> miaosha(Model model, MiaoshaUser user,
                                   @RequestParam("goodsId") long goodsId) {
        model.addAttribute("user", user);
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        //判断内存标记，减少redis数据库开销
        boolean over = overMap.get(goodsId);
        if (over) {
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //减库存
        Long stock = redisService.decr(GoodsKey.GOODS_STOCK, GOODS_STOCK_KEY + goodsId);
        if (stock <= 0) {
            overMap.put(goodsId, true);
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }

        //判断是否已经秒杀到了
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null) {
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }
        //入队列
        MiaoshaMessage miaoshaMessage = new MiaoshaMessage();
        miaoshaMessage.setUser(user);
        miaoshaMessage.setGoodsId(goodsId);
        sender.sendMiaoshaMessage(miaoshaMessage);
        return Result.success(0);
//		//判断库存
//		GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);//10个商品，req1 req2
//		int stock = goods.getStockCount();
//		if(stock <= 0) {
//			return Result.error(CodeMsg.MIAO_SHA_OVER);
//		}
//		//判断是否已经秒杀到了
//		MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
//		if(order != null) {
//			return Result.error(CodeMsg.REPEATE_MIAOSHA);
//		}
//		//减库存 下订单 写入秒杀订单
//		OrderInfo orderInfo = miaoshaService.miaosha(user, goods);
//		return Result.success(orderInfo);
    }

    /**
     * 如果秒杀成功返回goodsId
     * 秒杀失败返回-1
     * 进行中返回 0
     * */
    /**
     * GET POST有什么区别？
     */
    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> result(Model model, MiaoshaUser user,
                               @RequestParam("goodsId") long goodsId) {
        //判断是否已经秒杀到了
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null) {
            return Result.success(order.getOrderId());
        }
        long miaoshaResult = miaoshaService.getMiaoshaResult(user.getId(), goodsId);
        return Result.success(miaoshaResult);
    }


}
