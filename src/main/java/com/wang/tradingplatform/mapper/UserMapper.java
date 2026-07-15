package com.wang.tradingplatform.mapper;

import com.wang.tradingplatform.pojo.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    /**
     * 新增用户
     */
    int insert(User user);

    /**
     * 登录 传递密码和账号 能查到就可以登陆
     */
    Integer login(String account,String password);

    /**
     * 根据账号查找用户名字
     */
    String selectName(String string);


    /**
     * 根据账号查询用户id
     */
    @Select("SELECT USER_ID FROM user WHERE account = #{account} AND deleted = 0")
    Long findIDByAccount(@Param("account") String account);

    /**
     * 根据用户id查询账号和名字
     * @param userId
     * @return
     */
    String selectAccountAndName(Long userId);
}
