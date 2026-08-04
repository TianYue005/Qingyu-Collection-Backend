package com.wang.tradingplatform.controller;

import com.wang.tradingplatform.pojo.entity.Circle;
import com.wang.tradingplatform.pojo.entity.ItemQueryParam;
import com.wang.tradingplatform.pojo.entity.TeamUp;
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


    //添加分组信息
    @Operation(summary = "添加组团信息")
    @PostMapping("/add")
    public Result<Object> addTeamUp(@RequestParam TeamUp teamUp) {
        forumService.add(teamUp);
        return Result.success();
    }

    //分组分页查询
    @Operation(summary = "分页查询")
    @GetMapping("/select")
    public Result<PageResult<TeamUp>> select(@RequestParam ItemQueryParam itemQueryParam) {
        PageResult<TeamUp> page = forumService.select(itemQueryParam);
        return Result.success(page);
    }

    //添加圈子动态信息
    @Operation(summary = "添加圈子动态信息")
    @PostMapping("/add/dynamic")
    public Result<Object> addDynamic(@RequestParam Circle circle) {
        forumService.addDynamic(circle);
        return Result.success();
    }

    //添加发布任务信息
    @Operation(summary = "添加发布任务信息")
    @PostMapping("/add/task")
    public Result<Object> addTask(@RequestParam Circle circle) {
        forumService.addTask(circle);
        return Result.success();
    }

    //添加创建活动信息
    @Operation(summary = "添加创建活动信息")
    @PostMapping("/add/activity")
    public Result<Object> addActivity(@RequestParam Circle circle) {
        forumService.addActivity(circle);
        return Result.success();
    }
}
