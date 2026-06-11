package com.sky.service;

import com.sky.dto.DictTypeDTO;
import com.sky.dto.DictTypePageQueryDTO;
import com.sky.entity.DictType;
import com.sky.result.PageResult;

import java.util.List;

public interface DictTypeService {

    PageResult<DictType> getDictTypeList(DictTypePageQueryDTO dictTypePageQueryDTO);

    boolean addDictType(DictTypeDTO dictTypeDTO);

    boolean updateDictType(DictTypeDTO dictTypeDTO);

    boolean delDictType(Long id);

    DictType getDictTypeById(Long id);

    List<DictType> listAllEnabled();

    boolean changeStatus(Long id, Integer status);
}
