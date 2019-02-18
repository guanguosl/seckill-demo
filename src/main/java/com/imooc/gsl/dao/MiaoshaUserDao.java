package com.imooc.gsl.dao;

import com.imooc.gsl.domain.MiaoshaUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface MiaoshaUserDao {
	
	@Select("select * from miaosha_user where id = #{id}")
	public MiaoshaUser getById(@Param("id") long id);

	@Update("update from miaosha_user set password=#{password}  where id = #{id}")
	void update(MiaoshaUser miaoshaUser);
}
