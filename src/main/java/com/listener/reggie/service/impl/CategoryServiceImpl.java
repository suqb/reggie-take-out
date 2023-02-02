package com.listener.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.listener.reggie.common.CustomException;
import com.listener.reggie.entity.Category;
import com.listener.reggie.entity.Dish;
import com.listener.reggie.entity.SetMeal;
import com.listener.reggie.mapper.CategoryMapper;
import com.listener.reggie.service.CategoryService;
import com.listener.reggie.service.DishService;
import com.listener.reggie.service.SetMealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Resource
    private DishService dishService;

    @Resource
    private SetMealService setMealService;

    /**
     * 根据id删除菜品
     * @param id 菜品id
     */
    @Override
    public void remove(Long id) {

        //  根据分类id进行查询是否有关联菜品
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();

        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);

        int countDish = dishService.count(dishLambdaQueryWrapper);

        if (countDish > 0) {

            throw new CustomException("当前分类下关联了菜品，不能删除");

        }

        //  根据分类id进行查询是否有关联套餐
        LambdaQueryWrapper<SetMeal> setMealLambdaQueryWrapper = new LambdaQueryWrapper<>();

        setMealLambdaQueryWrapper.eq(SetMeal::getCategoryId, id);

        int countSetMeal = setMealService.count(setMealLambdaQueryWrapper);

        if (countSetMeal > 0) {

            throw new CustomException("当前分类下关联了套餐，不能删除");

        }

        super.removeById(id);
    }
}








