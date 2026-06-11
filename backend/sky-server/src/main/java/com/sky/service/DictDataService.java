package com.sky.service;

import com.sky.dto.DictDataDTO;
import com.sky.dto.DictDataPageQueryDTO;
import com.sky.entity.DictData;
import com.sky.result.PageResult;

import java.util.List;

public interface DictDataService {

    PageResult<DictData> getDictDataList(DictDataPageQueryDTO dictDataPageQueryDTO);

    boolean addDictData(DictDataDTO dictDataDTO);

    boolean updateDictData(DictDataDTO dictDataDTO);

    boolean delDictData(Long id);

    DictData getDictDataById(Long id);

    List<DictData> listByCode(String code);

    boolean changeStatus(Long id, Integer status);
}
