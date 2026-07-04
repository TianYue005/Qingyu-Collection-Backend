package com.wang.tradingplatform.mapper;

import com.wang.tradingplatform.pojo.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
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
    @Insert("INSERT INTO user (user_name, password, phone, status, balance, integral, credit, level, avatar, create_time, update_time, deleted) " +
            "VALUES (#{userName}, #{password}, #{phone}, #{status}, #{balance}, #{integral}, #{credit}, #{level}, #{avatar}, #{createTime}, #{updateTime}, #{deleted})")
    @Options(useGeneratedKeys = true, keyProperty = "userId")
    int insert(User user);

    /**
     * 根据用户ID查询用户
     * @param userId
     * @return
     */
    @Select("SELECT * FROM user WHERE user_id = #{userId} AND deleted = 0")
    User findByUserId(@NonNull String userId);

    /**
     * 根据账号查询对应的角色（role）
     * @param username
     * @return
     */
    @Select("SELECT role FROM user WHERE user_name = #{username}")
    String findRoleByUserId(@NonNull String username);

    /**
     * 根据账号查找用户
     * @param account
     * @return
     */
    @Select("SELECT * FROM user WHERE user_name = #{account} OR phone = #{account}")
    User findByAccount(String account);
}
