package com.imooc.gsl.controller;

import com.imooc.gsl.access.AccessLimit;
import com.imooc.gsl.domain.MiaoshaOrder;
import com.imooc.gsl.domain.MiaoshaUser;
import com.imooc.gsl.rabbitmq.MQSender;
import com.imooc.gsl.rabbitmq.MiaoshaMessage;
import com.imooc.gsl.redis.*;
import com.imooc.gsl.result.CodeMsg;
import com.imooc.gsl.result.Result;
import com.imooc.gsl.service.GoodsService;
import com.imooc.gsl.service.MiaoshaService;
import com.imooc.gsl.service.MiaoshaUserService;
import com.imooc.gsl.service.OrderService;
import com.imooc.gsl.util.MD5Util;
import com.imooc.gsl.util.UUIDUtil;
import com.imooc.gsl.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    private Map<Long, Boolean> localOverMap = new HashMap<Long, Boolean>();

    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsVoList = goodsService.listGoodsVo();
        if (ObjectUtils.isEmpty(goodsVoList)) {
            return;
        }
        for (GoodsVo goodsVo : goodsVoList) {
            redisService.set(GoodsKey.getMiaoshaGoodsStock, GOODS_STOCK_KEY + goodsVo.getId(), goodsVo.getStockCount());
            localOverMap.put(goodsVo.getId(), false);
        }
    }

    /**
     * QPS:1306
     * 5000 * 10
     * */
    /**
     * QPS:2000
     * 600 * 50
     * */
    // FIXME: 2019/3/1 服务器本身存在瓶颈

    /**
     * GET POST有什么区别？
     */
    @RequestMapping(value = "/{path}/do_miaosha", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> miaosha(Model model, MiaoshaUser user,
                                   @RequestParam("goodsId") long goodsId,
                                   @PathVariable String path) {
        model.addAttribute("user", user);
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        //校验地址是否正确
        if (!miaoshaService.checkPath(user, goodsId, path)) {
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }
        //判断内存标记，减少redis数据库开销
        boolean over = localOverMap.get(goodsId);
        if (over) {
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //减库存
        Long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock, GOODS_STOCK_KEY + goodsId);
        if (stock <= 0) {
            localOverMap.put(goodsId, true);
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

    @RequestMapping(value = "/reset", method = RequestMethod.GET)
    @ResponseBody
    public Result<Boolean> reset(Model model) {
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        for (GoodsVo goods : goodsList) {
            goods.setStockCount(10);
            redisService.set(GoodsKey.getMiaoshaGoodsStock, GOODS_STOCK_KEY + goods.getId(), goods.getStockCount());
            localOverMap.put(goods.getId(), false);
        }
        redisService.delete(OrderKey.getMiaoshaOrderByUidGid);
        redisService.delete(MiaoshaKey.isGoodsOver);
        miaoshaService.reset(goodsList);
        return Result.success(true);
    }

    /**
     * 获取秒杀地址
     */
    @RequestMapping(value = "/path", method = RequestMethod.GET)
    @ResponseBody
    @AccessLimit(seconds = 5,maxCount = 5)
    public Result<String> getMiaoshaPath(HttpServletRequest request, MiaoshaUser user,
                                         @RequestParam("goodsId") long goodsId,
                                         @RequestParam(value = "verifyCode", defaultValue = "0") Integer verifyCode) {
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        boolean check = miaoshaService.checkVerifyCode(user, goodsId, verifyCode);
        if (!check) {
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }
        String path = miaoshaService.creatMiaoshaPath(user, goodsId);
        return Result.success(path);
    }

    @RequestMapping(value = "/verifyCode", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getMiaoshaVerifyCod(HttpServletResponse response, MiaoshaUser user,
                                              @RequestParam("goodsId") long goodsId) {
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        try {
            BufferedImage image = miaoshaService.createVerifyCode(user, goodsId);
            OutputStream out = response.getOutputStream();
            ImageIO.write(image, "JPEG", out);
            out.flush();
            out.close();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(CodeMsg.MIAOSHA_FAIL);
        }
    }

}
