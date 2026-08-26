package com.wang.tradingplatform.controller;

import com.wang.tradingplatform.pojo.entity.*;
import com.wang.tradingplatform.pojo.vo.CommentCircleVO;
import com.wang.tradingplatform.pojo.vo.CommentVO;
import com.wang.tradingplatform.pojo.vo.PageResult;
import com.wang.tradingplatform.pojo.vo.Result;
import com.wang.tradingplatform.services.ForumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


@Tag(name = "论坛模块")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/forum")
public class ForumController {
    //论坛模块
    private final ForumService forumService;

    //--------------------------------------------------------------------------------
    //添加分组信息
    @Operation(summary = "添加组团信息")
    @PostMapping("/add")
    public Result<TeamUp> addTeamUp(@RequestBody TeamUp teamUp) {
        forumService.add(teamUp);
        return Result.success(teamUp);
    }

    //分组分页查询
    @Operation(summary = "分页查询")
    @GetMapping("/select")
    public Result<PageResult<TeamUp>> select(ItemQueryParam itemQueryParam) {
        PageResult<TeamUp> page = forumService.select(itemQueryParam);
        return Result.success(page);
    }

    //组团模糊搜索
    @Operation(summary = "组团模糊搜索")
    @GetMapping("/search")
    public Result<PageResult<TeamUp>> searchTeamUp(@RequestParam String keyword) {
        PageResult<TeamUp> page = forumService.searchTeamUp(keyword);
        return Result.success(page);
    }

    //根据组团id查询对应的详细信息
    @Operation(summary = "根据组团id查询对应的详细信息")
    @GetMapping("/detailed/{id}")
    public Result<TeamUp> detailedTeamUp(@PathVariable Long id) {
        TeamUp teamUp = forumService.detailedTeamUp(id);
        return Result.success(teamUp);
    }

    //我的参与
    @Operation(summary = "我的参与")
    @GetMapping("/myJoin")
    public Result<PageResult<TeamUp>> myJoin() {
        PageResult<TeamUp> page = forumService.myJoin();
        return Result.success(page);
    }

    //我的创建
    @Operation(summary = "我的创建")
    @GetMapping("/myCreate")
    public Result<PageResult<TeamUp>> myCreate() {
        PageResult<TeamUp> page = forumService.myCreate();
        return Result.success(page);
    }

    //组团评论
    @Operation(summary = "组团评论")
    @PostMapping("/add/comment")
    public Result<Comment> addComment(@RequestBody Comment comment) {
        forumService.addComment(comment);
        return Result.success();
    }

    //查看组团评论
    @Operation(summary = "查看组团评论")
    @GetMapping("/select/comment")
    public Result<PageResult<CommentVO>> selectComment(@RequestParam Long teamUpId) {
        PageResult<CommentVO> page = forumService.selectComment(teamUpId);
        return Result.success(page);
    }

    /*//查询该评论在回复的那个评论 (废弃)
    @Operation(summary = "查询该评论在回复的那个评论")
    @GetMapping("/select/comment/{id}")//id是指要查看的评论
    public Result<CommentVO> searchComment(@PathVariable Integer id) {
        CommentVO commentVO = forumService.searchComment(id);
        return Result.success(commentVO);
    }*/
    //查看该评论之前的所有互动
    @Operation(summary = "查看该评论之前的所有互动")
    @GetMapping("/select/comment/{id}")
    public Result<PageResult<CommentVO>> selectCommentInteraction(@PathVariable Integer id) {
        PageResult<CommentVO> page = forumService.selectCommentInteraction(id);
        return Result.success(page);
    }


    //--------------------------------------------------------------------------------
    //添加圈子动态信息
    @Operation(summary = "添加 圈子动态 信息")
    @PostMapping("/add/dynamic")
    public Result<Object> addDynamic(@RequestBody Circle circle) {
        if (!circle.getCategory().equals("Dynamics")) {
            return Result.error("传递类型错误");
        }
        forumService.addDynamic(circle);
        return Result.success();
    }

    //分页查询 圈子动态
    @Operation(summary = "分页查询 圈子动态")
    @GetMapping("/select/dynamic")
    public Result<PageResult<Circle>> selectDynamic(ItemQueryParam itemQueryParam) {
        PageResult<Circle> page = forumService.selectDynamic(itemQueryParam);
        return Result.success(page);
    }

    //圈子动态模糊搜索
    @Operation(summary = "圈子动态模糊搜索")
    @GetMapping("/search/dynamic")
    public Result<PageResult<Circle>> searchDynamic(@RequestParam String keyword) {
        PageResult<Circle> page = forumService.searchDynamic(keyword);
        return Result.success(page);
    }

    //圈子动态详细信息
    @Operation(summary = "圈子动态详细信息")
    @GetMapping("/detailed/{option}/{id}")
    public Result<Circle> detailedCircleUpdates(@PathVariable Integer id, @PathVariable String option){
        Circle Option=forumService.detailedCircleUpdates(id,option);
        return Result.success(Option);
    }

    //--------------------------------------------------------------------------------
    //添加发布任务信息
    @Operation(summary = "添加发布任务信息")
    @PostMapping("/add/task")
    public Result<Object> addTask(@RequestBody Circle circle) {
        if (!circle.getCategory().equals("Task")) {
            return Result.error("传递类型错误");
        }
        forumService.addTask(circle);
        return Result.success();
    }

    //分页查询 发布任务
    @Operation(summary = "分页查询 发布任务")
    @GetMapping("/select/task")
    public Result<PageResult<Circle>> selectTask(ItemQueryParam itemQueryParam) {
        PageResult<Circle> page = forumService.selectTask(itemQueryParam);
        return Result.success(page);
    }

    //发布任务模糊搜索
    @Operation(summary = "发布任务模糊搜索")
    @GetMapping("/search/task")
    public Result<PageResult<Circle>> searchTask(@RequestParam String keyword) {
        PageResult<Circle> page = forumService.searchTask(keyword);
        return Result.success(page);
    }


    //--------------------------------------------------------------------------------
    //添加热门活动信息
    @Operation(summary = "添加热门活动信息")
    @PostMapping("/add/activity")
    public Result<Object> addActivity(@RequestBody Circle circle) {
        if (!circle.getCategory().equals("Event")) {
            return Result.error("传递类型错误");
        }
        forumService.addActivity(circle);
        return Result.success();
    }

    //分页查询 热门活动
    @Operation(summary = "分页查询 热门活动")
    @GetMapping("/select/activity")
    public Result<PageResult<Circle>> selectActivity(ItemQueryParam itemQueryParam) {
        PageResult<Circle> page = forumService.selectActivity(itemQueryParam);
        return Result.success(page);
    }

    //热门活动模糊搜索
    @Operation(summary = "热门活动模糊搜索")
    @GetMapping("/search/activity")
    public Result<PageResult<Circle>> searchActivity(@RequestParam String keyword) {
        PageResult<Circle> page = forumService.searchActivity(keyword);
        return Result.success(page);
    }

    //--------------------------------------------------------------------------------
    //圈子评论
    @Operation(summary = "圈子评论")
    @PostMapping("/add/comment/circle")
    public Result<CommentCircle> addCircleComment(@RequestBody CommentCircle comment) {
        forumService.addCircleComment(comment);
        return Result.success();
    }

    //查看圈子评论
    @Operation(summary = "查看圈子评论")
    @GetMapping("/select/comment/circle")
    public Result<PageResult<CommentCircleVO>> selectCircleComment(@RequestParam Long circleId) {
        PageResult<CommentCircleVO> page = forumService.selectCircleComment(circleId);
        return Result.success(page);
    }

    //查看该评论之前的所有互动
    @Operation(summary = "查看该评论之前的所有互动")
    @GetMapping("/select/comment/circle/{id}")
    public Result<PageResult<CommentCircleVO>> selectCircleCommentInteraction(@PathVariable Integer id) {
        PageResult<CommentCircleVO> page = forumService.selectCircleCommentInteraction(id);
        return Result.success(page);
    }
}
