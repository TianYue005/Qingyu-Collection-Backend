package com.wang.tradingplatform.services.impl;

import com.wang.tradingplatform.mapper.ItemsMapper;
import com.wang.tradingplatform.pojo.dto.UploadItemDTO;
import com.wang.tradingplatform.pojo.entity.Items;
import com.wang.tradingplatform.services.itemsServices;
import com.wang.tradingplatform.utils.SnowflakeIdUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class itemsServicesImpl implements itemsServices {

    private final ItemsMapper itemsMapper;
    private final SnowflakeIdUtil snowflakeIdUtil;

    @Override
    @Transactional
    public Boolean add(UploadItemDTO uploadItemDTO) {
        Items items = new Items();
        items.setId(snowflakeIdUtil.nextId());
        items.setImageStr(uploadItemDTO.getImage() != null ? String.join(",", uploadItemDTO.getImage()) : "");
        items.setDescription(uploadItemDTO.getDescription());
        items.setPrice(uploadItemDTO.getPrice());
        items.setOriginalPrice(uploadItemDTO.getOriginalPrice());
        items.setSpecsStr(uploadItemDTO.getSpecs() != null ? String.join(",", uploadItemDTO.getSpecs()) : "");

        int rows = itemsMapper.insert(items);
        return rows > 0;
    }

    @Override
    public Items findById(Long id) {
        return itemsMapper.selectById(id);
    }

    @Override
    public List<Items> findAll() {
        return itemsMapper.selectAll();
    }
}