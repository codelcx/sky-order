package com.sky.controller.admin;

import com.sky.dto.DictDataDTO;
import com.sky.dto.DictDataPageQueryDTO;
import com.sky.entity.DictData;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DictDataService;
import com.sky.validator.groups.Update;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@Validated
@RequestMapping("/admin/dict/data")
@Tag(name = "字典数据管理")
public class DictDataController {

    @Autowired
    private DictDataService dictDataService;

    @GetMapping("/page")
    @Operation(summary = "字典数据分页查询")
    public Result<PageResult<DictData>> getDictDataList(@Validated DictDataPageQueryDTO dictDataPageQueryDTO) {
        log.info("字典数据分页查询，参数为：{}", dictDataPageQueryDTO);
        PageResult<DictData> pageResult = dictDataService.getDictDataList(dictDataPageQueryDTO);
        return Result.success(pageResult);
    }

    @PostMapping
    @Operation(summary = "新增字典数据")
    public Result<?> addDictData(@RequestBody @Validated DictDataDTO dictDataDTO) {
        log.info("新增字典数据，参数为：{}", dictDataDTO);
        boolean result = dictDataService.addDictData(dictDataDTO);
        return result ? Result.success() : Result.error("新增失败");
    }

    @PutMapping
    @Operation(summary = "修改字典数据")
    public Result<?> updateDictData(@RequestBody @Validated(Update.class) DictDataDTO dictDataDTO) {
        log.info("修改字典数据，参数为：{}", dictDataDTO);
        boolean result = dictDataService.updateDictData(dictDataDTO);
        return result ? Result.success() : Result.error("更新失败");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "根据ID删除字典数据")
    public Result<?> delDictData(@PathVariable @NotNull(message = "字典数据ID不能为空") Long id) {
        log.info("删除字典数据，参数为：{}", id);
        boolean result = dictDataService.delDictData(id);
        return result ? Result.success() : Result.error("删除失败");
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询字典数据")
    public Result<DictData> getDictDataById(@PathVariable @NotNull(message = "字典数据ID不能为空") Long id) {
        log.info("查询字典数据，参数为：{}", id);
        DictData dictData = dictDataService.getDictDataById(id);
        return Result.success(dictData);
    }

    @GetMapping("/listByCode/{code}")
    @Operation(summary = "根据字典类型编码查询字典数据")
    public Result<List<DictData>> listByCode(@PathVariable @NotNull(message = "字典编码不能为空") String code) {
        log.info("根据字典类型编码查询字典数据，编码为：{}", code);
        List<DictData> list = dictDataService.listByCode(code);
        return Result.success(list);
    }

    @PutMapping("/status/{status}")
    @Operation(summary = "启用、禁用字典数据")
    public Result<?> changeStatus(
            @PathVariable @Parameter(description = "状态 0:禁用 1:启用") Integer status,
            @RequestParam @NotNull(message = "字典数据ID不能为空") Long id) {
        log.info("修改字典数据状态，状态为：{}，ID为：{}", status, id);
        boolean result = dictDataService.changeStatus(id, status);
        return result ? Result.success() : Result.error("更新失败");
    }
}
