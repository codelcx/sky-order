package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import org.springdoc.core.annotations.ParameterObject;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.vo.CategoryPageVO;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import com.sky.validator.groups.Add;
import com.sky.validator.groups.Update;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@Validated
@RequestMapping("/admin/category")
@Tag(name = "分类管理")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询分类")
    public Result<Category> getCategoryById(@PathVariable @Parameter(description = "分类ID", required = true) Long id) {
        log.info("根据ID查询分类，参数：{}", id);
        Category category = categoryService.getCategoryById(id);
        return Result.success(category);
    }

    @GetMapping("/page")
    @Operation(summary = "分类分页查询")
    public Result<PageResult<CategoryPageVO>> getCategoryList(@Valid @ParameterObject CategoryPageQueryDTO categoryPageQueryDTO) {
        log.info("分类查询，参数为：{}", categoryPageQueryDTO);
        PageResult<CategoryPageVO> categoryPageResult = categoryService.getCategoryList(categoryPageQueryDTO);
        return Result.success(categoryPageResult);
    }

    @PostMapping
    @Operation(summary = "新增分类")
    public Result<?> addCategory(@RequestBody @Validated(Add.class) CategoryDTO categoryDTO) {
        log.info("新增分类，参数为：{}", categoryDTO);
        boolean result = categoryService.addCategory(categoryDTO);
        return result ? Result.success() : Result.error("新增失败");
    }

    @PutMapping
    @Operation(summary = "修改分类")
    public Result<?> updateCategory(@RequestBody @Validated(Update.class) CategoryDTO categoryDTO) {
        log.info("修改分类，参数为：{}", categoryDTO);
        boolean result = categoryService.updateCategory(categoryDTO);
        return result ? Result.success() : Result.error("更新失败");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "根据ID删除分类")
    public Result<?> delCategory(@PathVariable @Parameter(description = "分类ID", required = true) Long id) {
        log.info("删除分类，参数为: {}", id);
        boolean result = categoryService.delCategory(id);
        return result ? Result.success() : Result.error("删除失败");
    }

    @PostMapping("/status/{status}")
    @Operation(summary = "启用、禁用分类")
    public Result<?> changeCategoryStatus(
            @PathVariable
            @Parameter(description = "分类状态", required = true)
            @Range(max = 1L, message = "status错误")
            Integer status,

            @RequestParam
            @Parameter(description = "分类ID", required = true)
            Long id
    ) {
        log.info("修改分类状态，状态为：{}，ID为：{}", status, id);
        boolean result = categoryService.changeCategoryStatus(id, status);
        return result ? Result.success() : Result.error("更新失败");
    }

    @GetMapping("/list")
    @Operation(summary = "根据类型查询分类")
    public Result<List<Category>> listCategoryByType(
            @RequestParam
            @Parameter(description = "分类类型", required = true)
            @Range(min = 1L, max = 2L, message = "分类类型错误")
            Integer type
    ) {
        log.info("根据类型查询分类，参数：{}", type);
        List<Category> categories = categoryService.listCategoryByType(type);
        return Result.success(categories);
    }

}
