package com.wang.tradingplatform.mapper;

import com.wang.tradingplatform.pojo.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jspecify.annotations.NonNull;

@Mapper
public interface UserMapper {

    /**
     * 根据用户名查询用户
     */
    @Select("SELECT * FROM user WHERE user_name = #{userName} AND deleted = 0")
    User findByUserName(String userName);

    /**
     * 新增用户
     */
    int insert(User user);

    /**
     * 根据用户ID查询用户
     *
     * @param userId
     * @return
     */
    @Select("SELECT * FROM user WHERE user_id = #{userId} AND deleted = 0")
    User findByUserId(@NonNull String userId);

    /**
     * 根据账号查找用户
     */
    @Select("SELECT * FROM user WHERE account = #{account} AND deleted = 0")
    User findByAccount(@Param("account") String account);

    /**
     * 登录 传递密码和账号 能查到就可以登陆
     */
    Integer login(String account,String password);
}
