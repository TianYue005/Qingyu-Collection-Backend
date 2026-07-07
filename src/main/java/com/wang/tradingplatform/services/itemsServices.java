package com.wang.tradingplatform.services;

import com.wang.tradingplatform.pojo.dto.UploadItemDTO;
import com.wang.tradingplatform.pojo.entity.Items;

import java.util.List;

public interface itemsServices {
    //商品上架接口
    Boolean add(UploadItemDTO uploadItemDTO);

    //根据ID查找商品
    Items findById(Long id);

    //查找所有商品
    List<Items> findAll();
}
