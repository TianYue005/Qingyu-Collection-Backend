package com.wang.tradingplatform.mapper;

import com.wang.tradingplatform.pojo.entity.ChatMessage;
import com.wang.tradingplatform.pojo.entity.SessionState;
import com.wang.tradingplatform.pojo.entity.User;
import com.wang.tradingplatform.pojo.vo.ChatMessageListVO;
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
     * 根据用户id查询用户名
     */
    String selectUserNameById(Long id);


    /**
     * 根据session的id列表查找对应的聊天信息 并返回列表数据
     *
     * @param sessionIdList
     * @return
     */
    List<ChatMessageListVO> selectUserChatList(@Param("sessionId") List<Long> sessionIdList);

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

    //取消收藏功能
    Integer favouriteRM(Long id,Long userId);

    //看现在双方是否有会话(有的话直接返回会话的sessionId)
    Long selectSessionHistory(@Param("toUid") Long toUserId,@Param("fromUid") Long currentUserId);

    //发起会话
    Long createChatSession(SessionState Session);

    //分别查询自己与别人对自己发起的聊天
    List<ChatMessageListVO> selectChatList(Long currentUserId);

    //查找该用户的所有有关联的sessionId
    List<Long> selectUserSessionList(Long currentUserId);

    //查找请求用户有关的所有点对点对话的另一方的id的列表。
    List<Long> selectToId(Long id);

    //根据求到的id列表查询最后一条消息
    List<ChatMessageListVO> selectEndMessage(List<Long> idList);
}
