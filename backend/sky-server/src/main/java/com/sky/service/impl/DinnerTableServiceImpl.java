package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.StatusConstant;
import com.sky.dto.DinnerTableDTO;
import com.sky.dto.DinnerTablePageQueryDTO;
import com.sky.entity.DinnerTable;
import com.sky.exception.BusinessException;
import com.sky.mapper.DinnerTableMapper;
import com.sky.result.PageResult;
import com.sky.service.DinnerTableService;
import com.sky.utils.QRCodeUtil;
import com.sky.vo.DinnerTableVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class DinnerTableServiceImpl implements DinnerTableService {

    @Autowired
    private DinnerTableMapper dinnerTableMapper;

    @Value("${sky.local-storage.upload-path}")
    private String uploadPath;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Override
    public DinnerTable getDinnerTableById(Long id) {
        return dinnerTableMapper.getDinnerTableById(id);
    }

    @Override
    public DinnerTable getDinnerTableByTableNumber(Integer tableNumber) {
        return dinnerTableMapper.getDinnerTableByTableNumber(tableNumber);
    }

    @Override
    public boolean changeDinnerTableStatus(Long id, Integer status) {
        DinnerTable table = getDinnerTableById(id);
        if (table == null) throw new BusinessException("餐桌ID不存在");

        table.setStatus(status);
        int affectRow = dinnerTableMapper.updateDinnerTable(table);
        return affectRow > 0;
    }

    @Override
    public PageResult<DinnerTableVO> getDinnerTableList(DinnerTablePageQueryDTO dto) {
        try (Page<Object> page = PageHelper.startPage(dto.getPage(), dto.getPageSize())) {

            Page<DinnerTableVO> tablePage = page.doSelectPage(() ->
                    dinnerTableMapper.getDinnerTableList(dto)
            );

            return new PageResult<>(
                    tablePage.getTotal(),
                    tablePage.getResult(),
                    tablePage.getPageSize(),
                    tablePage.getPageNum()
            );
        }
    }

    @Override
    public boolean addDinnerTable(DinnerTableDTO dto) {
        int count = dinnerTableMapper.countByTableNumber(dto.getTableNumber());
        if (count > 0) throw new BusinessException("桌号已存在，请重新输入");

        DinnerTable table = new DinnerTable();
        BeanUtils.copyProperties(dto, table);
        table.setStatus(StatusConstant.TABLE_FREE);

        int affectRow = dinnerTableMapper.addDinnerTable(table);

        if (affectRow > 0) {
            generateQRCode(table);
        }

        return affectRow > 0;
    }

    @Override
    public boolean updateDinnerTable(DinnerTableDTO dto) {
        DinnerTable table = getDinnerTableById(dto.getId());
        if (table == null) throw new BusinessException("餐桌ID不存在");

        if (!dto.getTableNumber().equals(table.getTableNumber())) {
            int count = dinnerTableMapper.countByTableNumber(dto.getTableNumber());
            if (count > 0) throw new BusinessException("桌号已存在，请重新输入");
        }

        BeanUtils.copyProperties(dto, table);
        int affectRow = dinnerTableMapper.updateDinnerTable(table);

        if (affectRow > 0) {
            generateQRCode(table);
        }

        return affectRow > 0;
    }

    @Override
    public boolean delDinnerTable(Long id) {
        DinnerTable table = getDinnerTableById(id);
        if (table == null) throw new BusinessException("餐桌ID不存在");

        int affectRow = dinnerTableMapper.delDinnerTableById(id);
        return affectRow > 0;
    }

    private void generateQRCode(DinnerTable table) {
        try {
            String qrContent = contextPath + "/user/dinnerTable/" + table.getTableNumber();
            String fileName = "table_" + table.getId() + ".png";
            Path qrDir = Paths.get(uploadPath, "qrcodes");
            Path qrPath = qrDir.resolve(fileName);

            QRCodeUtil.generateQRCode(qrContent, qrPath);

            String qrCodeUrl = "/api/upload/qrcodes/" + fileName;
            table.setQrCodeUrl(qrCodeUrl);
            dinnerTableMapper.updateDinnerTable(table);
        } catch (Exception e) {
            throw new BusinessException("二维码生成失败");
        }
    }
}
