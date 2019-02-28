package com.imooc.gsl.service;

import com.imooc.gsl.domain.MiaoshaOrder;
import com.imooc.gsl.domain.MiaoshaUser;
import com.imooc.gsl.domain.OrderInfo;
import com.imooc.gsl.redis.MiaoshaKey;
import com.imooc.gsl.redis.RedisService;
import com.imooc.gsl.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class MiaoshaService {

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    RedisService redisService;

    /**
     * 秒杀入库
     *
     * @param user
     * @param goods
     * @return
     */
    @Transactional
    public OrderInfo miaosha(MiaoshaUser user, GoodsVo goods) {
        //减库存 下订单 写入秒杀订单
        boolean success = goodsService.reduceStock(goods);
        if (success) {
            //order_info maiosha_order
            return orderService.createOrder(user, goods);
        } else {
            setGoodsOver(goods.getId());
            redisService.set(MiaoshaKey.GOODS_OVER, "" + goods.getId(), true);
        }
        return null;

    }


    /**
     * 获取秒杀订单结果
     *
     * @param userId
     * @param goodsId
     * @return
     */
    public long getMiaoshaResult(Long userId, long goodsId) {
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(userId, goodsId);
        if (order != null) {
            return order.getOrderId();
        }
        boolean isGoodsOver = getGoodsOver(goodsId);
        if (isGoodsOver) {
            return -1;
        }
        return 0;
    }

    /**
     * redis添加无库存标记
     *
     * @param id
     */
    private void setGoodsOver(Long id) {
        redisService.set(MiaoshaKey.GOODS_OVER, id + "", true);
    }

    private boolean getGoodsOver(long goodsId) {
        return redisService.exists(MiaoshaKey.GOODS_OVER, goodsId + "");
    }
}
