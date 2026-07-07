package com.wang.tradingplatform.controller;

import com.wang.tradingplatform.pojo.dto.UploadItemDTO;
import com.wang.tradingplatform.pojo.entity.Items;
import com.wang.tradingplatform.pojo.vo.Result;
import com.wang.tradingplatform.services.impl.itemsServicesImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "商品上架模块")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/items")
public class ItemsController {

    private final itemsServicesImpl itemServices;

    //商品上架
    @Operation(summary = "商品上架")
    @PostMapping("/add")
    public Result<String> add(@RequestBody UploadItemDTO uploadItemDTO) {
        log.info("========== 添加商品 ==========");
        Boolean b = itemServices.add(uploadItemDTO);
        if (b){
            return Result.success();
        }
        return Result.error("商品上架失败");
    }

    //根据ID查找商品
    @Operation(summary = "根据ID查找商品")
    @GetMapping("/{id}")
    public Result<Items> findById(@PathVariable Long id) {
        log.info("========== 查找商品, id: {} ==========", id);
        Items item = itemServices.findById(id);
        if (item == null) {
            return Result.error("商品不存在");
        }
        return Result.success(item);
    }

    //查找所有商品
    @Operation(summary = "查找所有商品")
    @GetMapping("/list")
    public Result<List<Items>> findAll() {
        log.info("========== 查找所有商品 ==========");
        List<Items> list = itemServices.findAll();
        return Result.success(list);
    }

}
