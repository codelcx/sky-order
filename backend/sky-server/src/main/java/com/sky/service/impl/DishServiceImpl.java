package com.sky.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.RedisConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.exception.BusinessException;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.utils.LocalStorageUtil;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private SetMealDishMapper setMealDishMapper;

    @Autowired
    private LocalStorageUtil localStorageUtil;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public PageResult<DishVO> getDishList(DishPageQueryDTO dishPageQueryDTO) {
        try (Page<Object> page = PageHelper.startPage(
                dishPageQueryDTO.getPage(),
                dishPageQueryDTO.getPageSize())) {

            Page<DishVO> dishVoPage = page.doSelectPage(() ->
                    dishMapper.getDishVoList(dishPageQueryDTO)
            );

            return new PageResult<>(
                    dishVoPage.getTotal(),
                    dishVoPage.getResult(),
                    dishVoPage.getPageSize(),
                    dishVoPage.getPageNum()
            );
        }
    }

    @Override
    @Transactional // 涉及多表操作，需要开启事务，需要注意避免出现长事务
    public boolean saveDish(DishDTO dishDTO) {
        // 查找菜品名称是否重复
        Dish dish = dishMapper.getDishByDishName(dishDTO.getName());
        if (dish != null) {
            throw new BusinessException("菜品名称重复");
        }

        // 查找分类ID是否存在
        Category category = categoryMapper.getCategoryById(dishDTO.getCategoryId());
        if (category == null) {
            throw new BusinessException("分类ID不存在");
        }

        // 插入新菜品，数据库ID会回填到当前对象
        dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish, "flavors");
        // 将口味列表序列化为 JSON 存入菜品字段
        if (!CollectionUtils.isEmpty(dishDTO.getFlavors())) {
            dish.setFlavors(JSONObject.toJSONString(dishDTO.getFlavors()));
        }
        int affectRow = dishMapper.saveDish(dish);

        deleteAllDishCache();

        return affectRow > 0;
    }

    @Override
    @Transactional
    public boolean deleteDishByIds(List<Long> ids) {
        // 查找需要被删除的菜品ID是否是起售中
        List<Long> sellingDishIds = dishMapper.getSellingDishListByIds(ids);
        if (!sellingDishIds.isEmpty()) {
            throw new DeletionNotAllowedException("删除失败，菜品ID为：" + sellingDishIds + " 状态为起售中");
        }

        // 查找菜品ID是否存在关联的套餐
        List<Long> setMealWithDish = setMealDishMapper.getCountByDishIds(ids);
        if (!setMealWithDish.isEmpty()) {
            throw new DeletionNotAllowedException("删除失败，菜品ID为：" + setMealWithDish + " 存在关联套餐");
        }

        // 删除菜品
        int affectRows = dishMapper.deleteByIds(ids);
        // 查找被删除菜品的图片地址
        List<String> images = dishMapper.getDishImagesByIds(ids);

        deleteAllDishCache();

        localStorageUtil.deleteFileBatch(images);

        return affectRows >= 0;
    }

    @Override
    public DishVO getDishVOById(Long id) {
        DishVO dishVO = dishMapper.getDishVOById(id);
        if (dishVO == null) {
            throw new BusinessException("菜品ID不存在");
        }
        return dishVO;
    }

    @Override
    @Transactional
    public boolean updateDish(DishDTO dishDTO) {
        Dish dish = dishMapper.getDishById(dishDTO.getId());
        if (dish == null) {
            throw new BusinessException("菜品ID不存在");
        }

        // 当数据库中得到的菜品名称和传入的菜品名称不同时，需要判断菜品名称是否重复
        if (!dishDTO.getName().equals(dish.getName())) {
            if (dishMapper.getDishByDishName(dishDTO.getName()) != null) {
                throw new BusinessException("菜品名称重复");
            }
        }

        if (dish.getImage() != null && !dish.getImage().equals(dishDTO.getImage())) {
            localStorageUtil.deleteFile(dish.getImage());
        }


        BeanUtils.copyProperties(dishDTO, dish, "flavors");
        // 将口味列表序列化为 JSON 存入菜品字段
        if (!CollectionUtils.isEmpty(dishDTO.getFlavors())) {
            dish.setFlavors(JSONObject.toJSONString(dishDTO.getFlavors()));
        }
        int affectRow = dishMapper.updateDish(dish);

        deleteAllDishCache();
        return affectRow > 0;
    }

    @Override
    public boolean updateDishStatus(Long id, Integer status) {
        Dish dish = dishMapper.getDishById(id);
        if (dish == null) {
            throw new BusinessException("菜品ID不存在");
        }
        // 如果菜品关联了套餐就不能停售
        List<Long> setMealWithDish = setMealDishMapper.getCountByDishIds(Collections.singletonList(id));
        if (!setMealWithDish.isEmpty() && status == 0) {
            throw new BusinessException("修改状态失败，菜品ID为：" + setMealWithDish + " 存在关联套餐");
        }
        dish.setStatus(status);
        int affectRow = dishMapper.updateDish(dish);
        // 直接清空菜品缓存
        deleteAllDishCache();

        return affectRow > 0;
    }

    @Override
    public List<Dish> getDishListByCategoryId(Long categoryId) {
        return dishMapper.getDishListByCategoryId(categoryId);
    }

    @Override
    public List<DishVO> getDishVoListByCategoryId(Long categoryId) {

        String jsonStr = (String) redisTemplate.opsForHash().get(
                RedisConstant.SHOP_CATEGORY_DISHES,
                categoryId.toString()
        );

        if (jsonStr == null) {
            List<DishVO> dishVOList = dishMapper.getDishVoListByCategoryId(categoryId);
            String json = JSONObject.toJSONString(dishVOList);
            redisTemplate.opsForHash().put(RedisConstant.SHOP_CATEGORY_DISHES, categoryId.toString(), json);
            return dishVOList;
        }

        return JSONArray.parseArray(jsonStr, DishVO.class);
    }

    /// 业务规模小，全量清理简单可控，避免复杂的分类ID追踪逻辑
    private void deleteAllDishCache() {
        redisTemplate.delete(RedisConstant.SHOP_CATEGORY_DISHES);
    }
}
