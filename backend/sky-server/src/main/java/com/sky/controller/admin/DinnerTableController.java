package com.sky.controller.admin;

import com.sky.dto.DinnerTableDTO;
import org.springdoc.core.annotations.ParameterObject;
import com.sky.dto.DinnerTablePageQueryDTO;
import com.sky.entity.DinnerTable;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DinnerTableService;
import com.sky.validator.groups.Add;
import com.sky.validator.groups.Update;
import com.sky.vo.DinnerTableVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@Slf4j
@Validated
@RequestMapping("/admin/dinnerTable")
@Tag(name = "餐桌管理")
public class DinnerTableController {

    @Autowired
    private DinnerTableService dinnerTableService;

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询餐桌")
    public Result<DinnerTable> getDinnerTableById(@PathVariable @Parameter(description = "餐桌ID", required = true) Long id) {
        log.info("根据ID查询餐桌，参数：{}", id);
        DinnerTable dinnerTable = dinnerTableService.getDinnerTableById(id);
        return Result.success(dinnerTable);
    }

    @GetMapping("/page")
    @Operation(summary = "餐桌分页查询")
    public Result<PageResult<DinnerTableVO>> getDinnerTableList(@Valid @ParameterObject DinnerTablePageQueryDTO dto) {
        log.info("餐桌分页查询，参数为：{}", dto);
        PageResult<DinnerTableVO> pageResult = dinnerTableService.getDinnerTableList(dto);
        return Result.success(pageResult);
    }

    @PostMapping
    @Operation(summary = "新增餐桌")
    public Result<?> addDinnerTable(@RequestBody @Validated(Add.class) DinnerTableDTO dto) {
        log.info("新增餐桌，参数为：{}", dto);
        boolean result = dinnerTableService.addDinnerTable(dto);
        return result ? Result.success() : Result.error("新增失败");
    }

    @PutMapping
    @Operation(summary = "修改餐桌")
    public Result<?> updateDinnerTable(@RequestBody @Validated(Update.class) DinnerTableDTO dto) {
        log.info("修改餐桌，参数为：{}", dto);
        boolean result = dinnerTableService.updateDinnerTable(dto);
        return result ? Result.success() : Result.error("更新失败");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "根据ID删除餐桌")
    public Result<?> delDinnerTable(@PathVariable @Parameter(description = "餐桌ID", required = true) Long id) {
        log.info("删除餐桌，参数为: {}", id);
        boolean result = dinnerTableService.delDinnerTable(id);
        return result ? Result.success() : Result.error("删除失败");
    }

    @PostMapping("/status/{status}")
    @Operation(summary = "修改餐桌状态")
    public Result<?> changeDinnerTableStatus(
            @PathVariable
            @Parameter(description = "餐桌状态", required = true)
            @Range(max = 2L, message = "status错误")
            Integer status,

            @RequestParam
            @Parameter(description = "餐桌ID", required = true)
            Long id
    ) {
        log.info("修改餐桌状态，状态为：{}，ID为：{}", status, id);
        boolean result = dinnerTableService.changeDinnerTableStatus(id, status);
        return result ? Result.success() : Result.error("更新失败");
    }
}
