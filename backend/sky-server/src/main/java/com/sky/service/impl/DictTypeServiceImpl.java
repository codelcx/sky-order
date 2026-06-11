package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.StatusConstant;
import com.sky.dto.DictTypeDTO;
import com.sky.dto.DictTypePageQueryDTO;
import com.sky.entity.DictType;
import com.sky.exception.BusinessException;
import com.sky.mapper.DictTypeMapper;
import com.sky.result.PageResult;
import com.sky.service.DictTypeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DictTypeServiceImpl implements DictTypeService {

    @Autowired
    private DictTypeMapper dictTypeMapper;

    @Override
    public PageResult<DictType> getDictTypeList(DictTypePageQueryDTO dictTypePageQueryDTO) {
        try (Page<Object> page = PageHelper.startPage(
                dictTypePageQueryDTO.getPage(),
                dictTypePageQueryDTO.getPageSize())) {

            Page<DictType> dictTypePage = page.doSelectPage(() ->
                    dictTypeMapper.getDictTypeList(dictTypePageQueryDTO)
            );

            return new PageResult<>(
                    dictTypePage.getTotal(),
                    dictTypePage.getResult(),
                    dictTypePage.getPageSize(),
                    dictTypePage.getPageNum()
            );
        }
    }

    @Override
    public boolean addDictType(DictTypeDTO dictTypeDTO) {
        if (dictTypeMapper.getCountByName(dictTypeDTO.getName()) > 0) {
            throw new BusinessException("字典名称已存在");
        }
        if (dictTypeMapper.getCountByCode(dictTypeDTO.getCode()) > 0) {
            throw new BusinessException("字典编码已存在");
        }

        DictType dictType = new DictType();
        BeanUtils.copyProperties(dictTypeDTO, dictType);
        dictType.setStatus(StatusConstant.DISABLE);

        return dictTypeMapper.addDictType(dictType) > 0;
    }

    @Override
    public boolean updateDictType(DictTypeDTO dictTypeDTO) {
        DictType dictType = dictTypeMapper.getDictTypeById(dictTypeDTO.getId());
        if (dictType == null) {
            throw new BusinessException("字典类型不存在");
        }

        if (!dictTypeDTO.getName().equals(dictType.getName())
                && dictTypeMapper.getCountByName(dictTypeDTO.getName()) > 0) {
            throw new BusinessException("字典名称已存在");
        }
        if (!dictTypeDTO.getCode().equals(dictType.getCode())
                && dictTypeMapper.getCountByCode(dictTypeDTO.getCode()) > 0) {
            throw new BusinessException("字典编码已存在");
        }

        BeanUtils.copyProperties(dictTypeDTO, dictType);

        return dictTypeMapper.updateDictType(dictType) > 0;
    }

    @Override
    public boolean delDictType(Long id) {
        DictType dictType = dictTypeMapper.getDictTypeById(id);
        if (dictType == null) {
            throw new BusinessException("字典类型不存在");
        }

        return dictTypeMapper.delDictTypeById(id) > 0;
    }

    @Override
    public DictType getDictTypeById(Long id) {
        return dictTypeMapper.getDictTypeById(id);
    }

    @Override
    public List<DictType> listAllEnabled() {
        return dictTypeMapper.listAllEnabled();
    }

    @Override
    public boolean changeStatus(Long id, Integer status) {
        DictType dictType = dictTypeMapper.getDictTypeById(id);
        if (dictType == null) {
            throw new BusinessException("字典类型不存在");
        }
        dictType.setStatus(status);
        return dictTypeMapper.updateDictType(dictType) > 0;
    }
}
