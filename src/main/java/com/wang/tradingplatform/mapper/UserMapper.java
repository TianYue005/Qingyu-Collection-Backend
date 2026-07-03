package com.wang.tradingplatform.mapper;

import com.wang.tradingplatform.pojo.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    /**
     * 根据用户名查询用户
     */
    @Select("SELECT * FROM user WHERE user_name = #{userName} AND deleted = 0")
    User findByUserName(String userName);

    /**
     * 根据手机号查询用户
     */
    @Select("SELECT * FROM user WHERE phone = #{phone} AND deleted = 0")
    User findByPhone(String phone);

    /**
     * 新增用户
     */
    @Insert("INSERT INTO user (user_name, password, phone, status, balance, integral, credit, level, avatar, create_time, update_time, deleted) " +
            "VALUES (#{userName}, #{password}, #{phone}, #{status}, #{balance}, #{integral}, #{credit}, #{level}, #{avatar}, #{createTime}, #{updateTime}, #{deleted})")
    @Options(useGeneratedKeys = true, keyProperty = "userId")
    int insert(User user);
}
