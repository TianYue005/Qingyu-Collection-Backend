package com.wang.tradingplatform.services;

import com.wang.tradingplatform.pojo.dto.UploadItemDTO;
import com.wang.tradingplatform.pojo.entity.Goods;
import com.wang.tradingplatform.pojo.entity.ItemQueryParam;
import com.wang.tradingplatform.pojo.vo.PageResult;

import java.util.List;

public interface itemsServices {
    //商品上架接口
    Boolean add(UploadItemDTO uploadItemDTO);

    //根据ID查找商品
    Goods findById(Long id);

    //分页查找
    PageResult<Goods> toPage(ItemQueryParam itemQueryParam);


}
