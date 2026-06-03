package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.validator.groups.Add;
import com.sky.validator.groups.Update;
import com.sky.vo.DishVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/admin/dish")
@Validated
@Tag(name = "菜品相关接口")
public class DishController {

    @Autowired
    private DishService dishService;

    @Operation(summary = "新增菜品")
    @PostMapping
    public Result<?> saveDish(@RequestBody @Validated(Add.class) DishDTO dishDTO) {
        boolean result = dishService.saveDish(dishDTO);
        return result ? Result.success() : Result.error("添加菜品失败");
    }

    @Operation(summary = "分页查询菜品列表")
    @GetMapping("/page")
    public Result<PageResult<DishVO>> getDishList(@Valid DishPageQueryDTO dishPageQueryDTO) {
        PageResult<DishVO> dishVOPageResult = dishService.getDishList(dishPageQueryDTO);
        return Result.success(dishVOPageResult);
    }

    @Operation(summary = "删除菜品")
    @DeleteMapping
    public Result<?> deleteDish(
            @RequestParam
            @Parameter(description = "需要删除的菜品ID")
            @NotEmpty(message = "菜品ID不能为空")
            List<Long> ids
    ) {
        log.info("删除菜品：{}", ids);
        boolean result = dishService.deleteDishByIds(ids);
        return result ? Result.success() : Result.error("删除失败");
    }

    @Operation(summary = "根据ID查询菜品")
    @GetMapping("{id}")
    public Result<DishVO> getDishVOById(@PathVariable Long id) {
        log.info("查询菜品：{}", id);
        DishVO dishVO = dishService.getDishVOById(id);
        return Result.success(dishVO);
    }

    @Operation(summary = "修改菜品")
    @PutMapping
    public Result<?> updateDish(@RequestBody @Validated(Update.class) DishDTO dishDTO) {
        log.info("更新菜品：{}", dishDTO);
        boolean result = dishService.updateDish(dishDTO);
        return result ? Result.success() : Result.error("更新失败");
    }

    @Operation(summary = "菜品起售、停售")
    @PostMapping("/status/{status}")
    public Result<?> updateDishStatus(
            @PathVariable
            @Range(max = 1L, message = "菜品状态不合法")
            @Parameter(description = "菜品状态", required = true)
            Integer status,

            @Parameter(description = "菜品ID", required = true)
            @RequestParam
            Long id
    ) {
        log.info("修改菜品状态：id: {}, status: {}", id, status);
        boolean result = dishService.updateDishStatus(id, status);
        return result ? Result.success() : Result.error("更新失败");
    }

    @Operation(summary = "根据分类ID查询菜品")
    @GetMapping("/list")
    public Result<List<Dish>> listDishByCategoryId(@RequestParam Long categoryId) {
        log.info("根据分类ID查询菜品：{}", categoryId);
        List<Dish> dishes = dishService.getDishListByCategoryId(categoryId);
        return Result.success(dishes);
    }
}
