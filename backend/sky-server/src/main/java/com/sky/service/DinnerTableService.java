package com.sky.service;

import com.sky.dto.DinnerTableDTO;
import com.sky.dto.DinnerTablePageQueryDTO;
import com.sky.entity.DinnerTable;
import com.sky.result.PageResult;
import com.sky.vo.DinnerTableVO;

public interface DinnerTableService {

    PageResult<DinnerTableVO> getDinnerTableList(DinnerTablePageQueryDTO dto);

    boolean addDinnerTable(DinnerTableDTO dto);

    boolean updateDinnerTable(DinnerTableDTO dto);

    boolean delDinnerTable(Long id);

    DinnerTable getDinnerTableById(Long id);

    DinnerTable getDinnerTableByTableNumber(Integer tableNumber);

    boolean changeDinnerTableStatus(Long id, Integer status);
}
