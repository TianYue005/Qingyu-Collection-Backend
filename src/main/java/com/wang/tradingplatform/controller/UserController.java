package com.wang.tradingplatform.controller;


import com.wang.tradingplatform.pojo.dto.LoginDTO;
import com.wang.tradingplatform.pojo.dto.RegisterDTO;
import com.wang.tradingplatform.pojo.entity.ItemQueryParam;
import com.wang.tradingplatform.pojo.entity.Pending;
import com.wang.tradingplatform.pojo.entity.User;
import com.wang.tradingplatform.pojo.vo.EvaluateVO;
import com.wang.tradingplatform.pojo.vo.GoodsVO;
import com.wang.tradingplatform.pojo.vo.PageResult;
import com.wang.tradingplatform.pojo.vo.Result;
import com.wang.tradingplatform.services.userService;
import com.wang.tradingplatform.utils.ParamUtil;
import com.wang.tradingplatform.utils.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Tag(name = "用户模块")
@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private userService userService;

    /**
     * 用户注册
     *
     * @param registerDTO 注册请求参数
     * @return 注册结果
     */
    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<String> register(@RequestBody RegisterDTO registerDTO) {
        ParamUtil.notNull(registerDTO, "注册信息");
        String msg = userService.register(registerDTO);
        if ("注册成功".equals(msg)) {
            return Result.success();
        }
        return Result.error(msg);
    }

    /**
     * 用户登录
     *
     * @param loginDTO 登录请求参数
     * @return 登录结果（成功时返回 token）
     */
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginDTO loginDTO) {
        log.info("========== 收到登录请求 ==========");
        log.info("登录用户名: {}", loginDTO.getAccount());

        ParamUtil.notNull(loginDTO, "登录信息");
        //message存储的是 token
        String message = userService.login(loginDTO);
        if (message.equals("信息有误") || message.equals("登陆失败，请检查账号或者密码")) {
            return Result.error(message);
        }
        return Result.success(message);
    }

    @Operation(summary = "修改用户名")
    @PostMapping("/alterName/{name}")
    public Result<Object> alterName(@PathVariable String name) {
        ParamUtil.notBlank(name, "用户名");
        Boolean b = userService.alterName(name);
        if (b) {
            return Result.success();
        }
        return Result.error("用户名修改失败");
    }

    /**
     * 得到用户的用户名与账号
     *
     * @param id 用户id
     * @return Result<User>
     */
    @Operation(summary = "得到用户的用户名与账号")
    @PostMapping("/info/{id}")
    public Result<User> info(@PathVariable Long id) {
        ParamUtil.positive(id, "用户id");
        log.info("========== 获取用户信息 ==========");
        return Result.success(userService.selectAccountAndName(id));
    }

    //添加收藏功能
    @Operation(summary = "收藏功能")
    @PostMapping("/favourite/{id}")
    public Result<Integer> favourite(@PathVariable Long id) {
        ParamUtil.positive(id, "商品id");
        log.info("========== 收藏功能 ==========");
        return Result.success(userService.favourite(id));
    }

    //取消收藏功能
    @Operation(summary = "取消收藏功能")
    @DeleteMapping("/favouriteRM/{id}")
    public Result<Integer> favouriteRM(@PathVariable Long id) {
        ParamUtil.positive(id, "商品id");
        log.info("========== 取消收藏功能 ==========");
        return Result.success(userService.favouriteRM(id));
    }

    //查看收藏功能
    @Operation(summary = "查看收藏功能")
    @GetMapping("/favourite")
    public Result<PageResult<GoodsVO>> favourite(ItemQueryParam itemQueryParam) {
        log.info("========== 查看收藏功能 ==========");
        return Result.success(userService.selectFavourite(itemQueryParam));
    }

    //得到账号基本信息
    @Operation(summary = "得到账号基本信息")
    @GetMapping("/accountInfo")
    public Result<User> accountInfo(@RequestParam String account) {
        ParamUtil.notBlank(account, "账号");
        log.info("========== 获取账号基本信息 ==========");
        return Result.success(userService.selectAccountInfo(account));
    }

    //修改密码
    @Operation(summary = "修改密码")
    @PostMapping("/updatePassword")
    public Result<Object> updatePassword(@RequestParam String password) {
        ParamUtil.notBlank(password, "密码");
        log.info("========== 修改密码 ==========");
        userService.updatePassword(password);
        return Result.success();
    }

    //当前用户的待处理交易
    @Operation(summary = "当前用户的待处理交易")
    @GetMapping("/pending/wait")
    public List<Pending> userPending() {
        log.info("========== 当前用户的待处理交易 ==========");
        return userService.userPending(UserContext.getCurrentUserId());
    }

    //查看评论  自己对别人的/别人对自己的
    @Operation(summary = "查看评论  自己对别人的/别人对自己的 ")
    @GetMapping("/myReview")
    public Result<PageResult<EvaluateVO>> selectMyReview(ItemQueryParam itemQueryParam) {
        log.info("========== 查看评论 自己对别人的/别人对自己的 ==========");
        return Result.success(userService.selectEvaluate(itemQueryParam));
    }

    //别的用户的详情界面
    @Operation(summary = "别的用户的详情界面")
    @GetMapping("/thisUserDetail/{userId}")
    public Result<User> thisUserDetail(@PathVariable Long userId) {
        log.info("========== 查看别的用户的详情界面 ==========");
        return Result.success(userService.selectThisUserDetail(userId));
    }

}
