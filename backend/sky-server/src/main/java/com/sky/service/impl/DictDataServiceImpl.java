package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.StatusConstant;
import com.sky.dto.DictDataDTO;
import com.sky.dto.DictDataPageQueryDTO;
import com.sky.entity.DictData;
import com.sky.entity.DictType;
import com.sky.exception.BusinessException;
import com.sky.mapper.DictDataMapper;
import com.sky.mapper.DictTypeMapper;
import com.sky.result.PageResult;
import com.sky.service.DictDataService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DictDataServiceImpl implements DictDataService {

    @Autowired
    private DictDataMapper dictDataMapper;

    @Autowired
    private DictTypeMapper dictTypeMapper;

    @Override
    public PageResult<DictData> getDictDataList(DictDataPageQueryDTO dictDataPageQueryDTO) {
        try (Page<Object> page = PageHelper.startPage(
                dictDataPageQueryDTO.getPage(),
                dictDataPageQueryDTO.getPageSize())) {

            Page<DictData> dictDataPage = page.doSelectPage(() ->
                    dictDataMapper.getDictDataList(dictDataPageQueryDTO)
            );

            return new PageResult<>(
                    dictDataPage.getTotal(),
                    dictDataPage.getResult(),
                    dictDataPage.getPageSize(),
                    dictDataPage.getPageNum()
            );
        }
    }

    @Override
    public boolean addDictData(DictDataDTO dictDataDTO) {
        DictType dictType = dictTypeMapper.getDictTypeById(dictDataDTO.getDictTypeId());
        if (dictType == null) {
            throw new BusinessException("字典类型不存在");
        }

        if (dictDataMapper.getCountByTypeIdAndValue(dictDataDTO.getDictTypeId(), dictDataDTO.getValue()) > 0) {
            throw new BusinessException("该字典类型下键值已存在");
        }

        DictData dictData = new DictData();
        BeanUtils.copyProperties(dictDataDTO, dictData);
        dictData.setStatus(StatusConstant.DISABLE);

        return dictDataMapper.addDictData(dictData) > 0;
    }

    @Override
    public boolean updateDictData(DictDataDTO dictDataDTO) {
        DictData dictData = dictDataMapper.getDictDataById(dictDataDTO.getId());
        if (dictData == null) {
            throw new BusinessException("字典数据不存在");
        }

        if (!dictDataDTO.getValue().equals(dictData.getValue())
                && dictDataMapper.getCountByTypeIdAndValue(dictData.getDictTypeId(), dictDataDTO.getValue()) > 0) {
            throw new BusinessException("该字典类型下键值已存在");
        }

        BeanUtils.copyProperties(dictDataDTO, dictData);

        return dictDataMapper.updateDictData(dictData) > 0;
    }

    @Override
    public boolean delDictData(Long id) {
        DictData dictData = dictDataMapper.getDictDataById(id);
        if (dictData == null) {
            throw new BusinessException("字典数据不存在");
        }

        return dictDataMapper.delDictDataById(id) > 0;
    }

    @Override
    public DictData getDictDataById(Long id) {
        return dictDataMapper.getDictDataById(id);
    }

    @Override
    public List<DictData> listByCode(String code) {
        DictType dictType = dictTypeMapper.getDictTypeByCode(code);
        if (dictType == null) {
            throw new BusinessException("字典编码不存在");
        }

        return dictDataMapper.listByTypeId(dictType.getId());
    }

    @Override
    public boolean changeStatus(Long id, Integer status) {
        DictData dictData = dictDataMapper.getDictDataById(id);
        if (dictData == null) {
            throw new BusinessException("字典数据不存在");
        }
        dictData.setStatus(status);
        return dictDataMapper.updateDictData(dictData) > 0;
    }
}
