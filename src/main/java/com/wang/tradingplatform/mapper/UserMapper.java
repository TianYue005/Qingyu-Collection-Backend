package com.wang.tradingplatform.mapper;

import com.wang.tradingplatform.pojo.entity.User;
import com.wang.tradingplatform.pojo.vo.ChatListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper {

    /**
     * 新增用户
     */
    int insert(User user);

    /**
     * 登录 传递密码和账号 能查到就可以登陆
     */
    Integer login(String account, String password);

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
     *
     * @param userId
     * @return
     */
    User selectAccountAndName(Long userId);

    /**
     * 查找用户的聊天列表  session的id列表
     *
     * @param currentUserId
     * @return
     */
    List<Long> selectUserSessionList(Long currentUserId);

    /**
     * 根据session的id列表查找对应的聊天信息 并返回列表数据
     *
     * @param sessionIdList
     * @return
     */
    List<ChatListVO> selectUserChatList(@Param("sessionId") List<Long> sessionIdList);

    /**
     * 添加收藏功能
     * @return
     */
    int addFavourite(@Param("userId") Long userId, @Param("itemId") Long itemId);

    /**
     * 查看收藏功能
     * @param currentUserId  用户的Id
     * @return 返回的是用户收藏的商品的 商品id
     */
    List<Long> selectFavourite(Long currentUserId);

    /**
     * 得到账号基本信息
     * @param currentUserId 用户的Id
     * @return User
     */
    User selectAccountInfo(Long currentUserId);

    /**
     * 修改密码
     * @param password
     * @param currentUserId
     */
    void updateUserPassword(String password, Long currentUserId);
}
