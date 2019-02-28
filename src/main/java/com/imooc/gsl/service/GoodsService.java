package com.imooc.gsl.service;

import java.util.List;

import com.imooc.gsl.dao.GoodsDao;
import com.imooc.gsl.domain.MiaoshaGoods;
import com.imooc.gsl.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class GoodsService {

    @Autowired
    GoodsDao goodsDao;

    public List<GoodsVo> listGoodsVo() {
        return goodsDao.listGoodsVo();
    }

    public GoodsVo getGoodsVoByGoodsId(long goodsId) {
        return goodsDao.getGoodsVoByGoodsId(goodsId);
    }

    public boolean reduceStock(GoodsVo goods) {
        MiaoshaGoods g = new MiaoshaGoods();
        g.setGoodsId(goods.getId());
        int count = goodsDao.reduceStock(g);
        return count > 0;
    }


}
