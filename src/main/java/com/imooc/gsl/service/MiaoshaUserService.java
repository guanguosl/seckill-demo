package com.imooc.gsl.service;

import com.imooc.gsl.component.MD5Component;
import com.imooc.gsl.constants.MiaoshaUserConstants;
import com.imooc.gsl.dao.MiaoshaUserDao;
import com.imooc.gsl.domain.MiaoshaUser;

import com.imooc.gsl.exception.GlobalException;
import com.imooc.gsl.redis.MiaoshaUserKey;
import com.imooc.gsl.redis.RedisService;
import com.imooc.gsl.result.CodeMsg;
import com.imooc.gsl.util.MD5Util;
import com.imooc.gsl.util.RequestHolderUtil;
import com.imooc.gsl.util.UUIDUtil;
import com.imooc.gsl.vo.LoginVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class MiaoshaUserService {

    @Autowired
    MiaoshaUserDao miaoshaUserDao;

    @Autowired
    RedisService redisService;

    @Autowired
    MD5Component md5Component;

    public MiaoshaUser getById(long id) {
        MiaoshaUser miaoshaUser = redisService.get(MiaoshaUserKey.user, String.valueOf(id), MiaoshaUser.class);
        if (!ObjectUtils.isEmpty(miaoshaUser)) {
            return miaoshaUser;
        }
        miaoshaUser = miaoshaUserDao.getById(id);
        if (!ObjectUtils.isEmpty(miaoshaUser)) {
            redisService.set(MiaoshaUserKey.user, String.valueOf(id), miaoshaUser);
        }
        return miaoshaUserDao.getById(id);
    }

    /**
     * 更新密码
     * @param token
     *        token
     *@param id
     *        用户ID
     * @param newPassword
     *        新密码
     * @return bool
     */
    public boolean updatePassword(String token, long id, String newPassword) {
        MiaoshaUser miaoshaUser = this.getById(id);
        if(ObjectUtils.isEmpty(miaoshaUser)){
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        MiaoshaUser newMiaoshaUser=new MiaoshaUser();
        newMiaoshaUser.setId(id);
        newMiaoshaUser.setPassword(MD5Util.inputPassToDbPass(newPassword,miaoshaUser.getSalt()));
        miaoshaUserDao.update(miaoshaUser);
        redisService.delete(MiaoshaUserKey.user,String.valueOf(id));
        miaoshaUser.setPassword(newMiaoshaUser.getPassword());
        redisService.set(MiaoshaUserKey.token,String.valueOf(id),miaoshaUser);
        return true;
    }

    public MiaoshaUser getByToken(String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        MiaoshaUser user = redisService.get(MiaoshaUserKey.token, token, MiaoshaUser.class);
        //延长有效期
        if (user != null) {
            addCookie(token, user);
        }
        return user;
    }

    public String login(LoginVo loginVo) {

        if (loginVo == null) {
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }

        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();
        //判断手机号是否存在
        MiaoshaUser user = getById(Long.parseLong(mobile));
        if (user == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //验证密码
        String dbPass = user.getPassword();
        String saltDB = user.getSalt();
        String calcPass = MD5Util.formPassToDBPass(formPass, saltDB);
        if (!calcPass.equals(dbPass)) {
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
        //生成cookie
        String token = UUIDUtil.uuid();
        addCookie(token, user);
        return token;
    }

    private void addCookie(String token, MiaoshaUser user) {
        HttpServletResponse response = RequestHolderUtil.getResponse();
        redisService.set(MiaoshaUserKey.token, token, user);
        Cookie cookie = new Cookie(MiaoshaUserConstants.COOKIE_NAME_TOKEN, token);
        cookie.setMaxAge(MiaoshaUserKey.token.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }

}
