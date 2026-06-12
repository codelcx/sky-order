package com.sky.controller.user;

import com.sky.entity.DinnerTable;
import com.sky.result.Result;
import com.sky.service.DinnerTableService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("userDinnerTableController")
@Slf4j
@RequestMapping("/user/dinnerTable")
@Tag(name = "用户端餐桌接口")
public class DinnerTableController {

    @Autowired
    private DinnerTableService dinnerTableService;

    @GetMapping("/{tableNumber}")
    @Operation(summary = "根据桌号查询餐桌")
    public Result<DinnerTable> getDinnerTableByTableNumber(
            @PathVariable @Parameter(description = "桌号", required = true) Integer tableNumber
    ) {
        log.info("根据桌号查询餐桌，桌号：{}", tableNumber);
        DinnerTable dinnerTable = dinnerTableService.getDinnerTableByTableNumber(tableNumber);
        return Result.success(dinnerTable);
    }
}
