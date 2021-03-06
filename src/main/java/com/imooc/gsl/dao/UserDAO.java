package com.imooc.gsl.dao;

import com.imooc.gsl.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @auther guanyl on 2019-1-9.
 */
@Mapper
public interface UserDAO {

    @Select("select * from user t where t.id=#{id}")
    User getUserById(@Param("id") int id);

    @Select("insert into user(id,name)values(#{id},#{name})")
    int insert(User user);
}
