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

import static com.imooc.gsl.redis.GoodsKey.GOODS_LIST;

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
        model.addAttribute("user", user);
        //查询商品列表
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        model.addAttribute("goodsList", goodsList);
        String html = redisService.get(GOODS_LIST, "goodsList", String.class);
        if (StringUtils.isEmpty(html)) {
            HttpServletRequest request = RequestHolderUtil.getRequest();
            SpringWebContext springWebContext = new SpringWebContext(request,
                    RequestHolderUtil.getResponse(),
                    request.getServletContext(),
                    request.getLocale(),
                    model.asMap(),
                    applicationContext);
            html = thymeleafViewResolver.getTemplateEngine().process("goods_list", springWebContext);
            redisService.set(GOODS_LIST, "goodsList", html);
        }
        //        return "goods_list";
        return html;
    }

    @RequestMapping("/to_detail/{goodsId}")
    public String detail(
            Model model,
            MiaoshaUser user,
            @PathVariable("goodsId") long goodsId) {
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
        return "goods_detail";
    }

}
