package com.wang.tradingplatform.services;

import com.wang.tradingplatform.pojo.dto.LoginDTO;
import com.wang.tradingplatform.pojo.dto.RegisterDTO;
import com.wang.tradingplatform.pojo.entity.ItemQueryParam;
import com.wang.tradingplatform.pojo.entity.User;
import com.wang.tradingplatform.pojo.vo.ChatListVO;
import com.wang.tradingplatform.pojo.vo.GoodsVO;
import com.wang.tradingplatform.pojo.vo.PageResult;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface userService {
    /**
     * 用户注册
     *
     * @param registerDTO 注册请求参数
     * @return 注册结果消息
     */
    String register(RegisterDTO registerDTO);

    /**
     * 用户登录
     *
     * @param loginDTO
     * @return
     */
    String login(LoginDTO loginDTO);

    /**
     * 根据用户账号查询用户名
     */
    String selectName(String string);

    /**
     * 根据用户账户号查用户Id
     */
    Long selectId(String account);

    /**
     * 根据用户Id查询对应的用户名与账号
     */
    User selectAccountAndName(Long userId);

    /**
     * 返回用户聊天列表
     *
     * @return
     */
    List<ChatListVO> selectChatList();

    /**
     * 根据传递的sessionId获取历史消息
     *
     * @param id
     * @return
     */
    List<ChatListVO> selectHistory(@Param("id") Long id);

    /**
     * 添加收藏功能
     *
     * @param id
     * @return
     */
    int favourite(Long id);

    /**
     * 查看收藏功能
     *
     * @return
     */
    PageResult<GoodsVO> selectFavourite(ItemQueryParam itemQueryParam);

    /**
     * 得到账号基本信息
     *
     * @param account
     * @return
     */
    User selectAccountInfo(String account);

    /**
     * 修改密码
     *
     * @param password
     * @return
     */
    void updatePassword(@Param("password") String password);
}
