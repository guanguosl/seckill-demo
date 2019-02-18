package com.imooc.gsl.controller;

import com.imooc.gsl.domain.MiaoshaUser;
import com.imooc.gsl.redis.RedisService;
import com.imooc.gsl.service.GoodsService;
import com.imooc.gsl.service.MiaoshaUserService;
import com.imooc.gsl.util.RequestHolderUtil;
import com.imooc.gsl.vo.GoodsVo;
import org.apache.catalina.util.RequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.spring4.context.SpringWebContext;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.imooc.gsl.redis.GoodsKey.*;

/**
 * @auther guanyl on 2019-1-9.
 */

@Controller
@RequestMapping("/goods")
public class GoodsController {
    private static final Logger log = LoggerFactory.getLogger(GoodsController.class);

    @Autowired
    MiaoshaUserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;

    @Autowired
    ApplicationContext applicationContext;

    @RequestMapping(value = "/to_list", produces = "text/html")
    @ResponseBody
    public String list(Model model, MiaoshaUser user) {
        String html = redisService.get(GOODS_LIST, GOODS_LIST_KEY, String.class);
//        if (!StringUtils.isEmpty(html)) {
//            return html;
//        }
        model.addAttribute("user", user);
        //查询商品列表
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        model.addAttribute("goodsList", goodsList);
        //                return "goods_list";
        html = redisService.get(GOODS_LIST, GOODS_LIST_KEY, String.class);
        HttpServletRequest request = RequestHolderUtil.getRequest();
        SpringWebContext springWebContext = new SpringWebContext(request,
                RequestHolderUtil.getResponse(),
                request.getServletContext(),
                request.getLocale(),
                model.asMap(),
                applicationContext);
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list", springWebContext);
        redisService.set(GOODS_LIST, GOODS_LIST_KEY, html);
        return html;
    }

    /**
     * Pressure,有redis缓存 :thread=1000,loop=5,qps=850,avg=831
     * Pressure,有redis缓存 :thread=1000,loop=10,qps=860
     * Pressure,有redis缓存 :thread=1000,loop=20,qps=873
     *
     * Pressure,无redis缓存 :thread=1000,loop=5,qps=758,avg=1026
     * Pressure,无redis缓存 :thread=1000,loop=10,qps=814,avg=1008 --很快到达瓶颈
     * Pressure,无redis缓存 :thread=1000,loop=20,qps=736,avg=1162 --很快到达瓶颈
     */
    @RequestMapping(value = "/to_detail/{goodsId}", produces = "text/html")
    @ResponseBody
    public String detail(
            Model model,
            MiaoshaUser user,
            @PathVariable("goodsId") long goodsId) {
        String html = redisService.get(GOODS_DETAIL, GOODS_DETAIL_KEY + goodsId, String.class);
        if (!StringUtils.isEmpty(html)) {
            return html;
        }
        model.addAttribute("user", user);

        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods", goods);

        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();

        int miaoshaStatus = 0;
        int remainSeconds = 0;
        if (now < startAt) {//秒杀还没开始，倒计时
            miaoshaStatus = 0;
            remainSeconds = (int) ((startAt - now) / 1000);
        } else if (now > endAt) {//秒杀已经结束
            miaoshaStatus = 2;
            remainSeconds = -1;
        } else {//秒杀进行中
            miaoshaStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("miaoshaStatus", miaoshaStatus);
        model.addAttribute("remainSeconds", remainSeconds);
        //        return "goods_detail";
        HttpServletRequest request = RequestHolderUtil.getRequest();
        SpringWebContext springWebContext = new SpringWebContext(request,
                RequestHolderUtil.getResponse(),
                request.getServletContext(),
                request.getLocale(),
                model.asMap(),
                applicationContext);
        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", springWebContext);
        redisService.set(GOODS_LIST, GOODS_DETAIL_KEY + goodsId, html);
        return html;
    }

}
