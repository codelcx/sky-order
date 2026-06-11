package com.sky.controller.admin;

import com.sky.dto.DictTypeDTO;
import com.sky.dto.DictTypePageQueryDTO;
import com.sky.entity.DictType;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DictTypeService;
import com.sky.validator.groups.Add;
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
@RequestMapping("/admin/dict/type")
@Tag(name = "字典类型管理")
public class DictTypeController {

    @Autowired
    private DictTypeService dictTypeService;

    @GetMapping("/page")
    @Operation(summary = "字典类型分页查询")
    public Result<PageResult<DictType>> getDictTypeList(@Validated DictTypePageQueryDTO dictTypePageQueryDTO) {
        log.info("字典类型分页查询，参数为：{}", dictTypePageQueryDTO);
        PageResult<DictType> pageResult = dictTypeService.getDictTypeList(dictTypePageQueryDTO);
        return Result.success(pageResult);
    }

    @PostMapping
    @Operation(summary = "新增字典类型")
    public Result<?> addDictType(@RequestBody @Validated(Add.class) DictTypeDTO dictTypeDTO) {
        log.info("新增字典类型，参数为：{}", dictTypeDTO);
        boolean result = dictTypeService.addDictType(dictTypeDTO);
        return result ? Result.success() : Result.error("新增失败");
    }

    @PutMapping
    @Operation(summary = "修改字典类型")
    public Result<?> updateDictType(@RequestBody @Validated(Update.class) DictTypeDTO dictTypeDTO) {
        log.info("修改字典类型，参数为：{}", dictTypeDTO);
        boolean result = dictTypeService.updateDictType(dictTypeDTO);
        return result ? Result.success() : Result.error("更新失败");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "根据ID删除字典类型")
    public Result<?> delDictType(@PathVariable @NotNull(message = "字典类型ID不能为空") Long id) {
        log.info("删除字典类型，参数为：{}", id);
        boolean result = dictTypeService.delDictType(id);
        return result ? Result.success() : Result.error("删除失败");
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询字典类型")
    public Result<DictType> getDictTypeById(@PathVariable @NotNull(message = "字典类型ID不能为空") Long id) {
        log.info("查询字典类型，参数为：{}", id);
        DictType dictType = dictTypeService.getDictTypeById(id);
        return Result.success(dictType);
    }

    @GetMapping("/list")
    @Operation(summary = "查询所有启用的字典类型")
    public Result<List<DictType>> listAllEnabled() {
        log.info("查询所有启用的字典类型");
        List<DictType> list = dictTypeService.listAllEnabled();
        return Result.success(list);
    }

    @PutMapping("/status/{status}")
    @Operation(summary = "启用、禁用字典类型")
    public Result<?> changeStatus(
            @PathVariable @Parameter(description = "状态 0:禁用 1:启用") Integer status,
            @RequestParam @NotNull(message = "字典类型ID不能为空") Long id) {
        log.info("修改字典类型状态，状态为：{}，ID为：{}", status, id);
        boolean result = dictTypeService.changeStatus(id, status);
        return result ? Result.success() : Result.error("更新失败");
    }
}
