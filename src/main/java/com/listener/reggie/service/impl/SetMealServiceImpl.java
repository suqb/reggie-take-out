package com.listener.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.listener.reggie.common.CustomException;
import com.listener.reggie.common.R;
import com.listener.reggie.dto.SetMealDto;
import com.listener.reggie.entity.SetMeal;
import com.listener.reggie.entity.SetMealDish;
import com.listener.reggie.mapper.SetMealMapper;
import com.listener.reggie.service.SetMealDishService;
import com.listener.reggie.service.SetMealService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SetMealServiceImpl extends ServiceImpl<SetMealMapper, SetMeal> implements SetMealService {

    @Resource
    private SetMealDishService setMealDishService;

    @Resource SetMealService setMealService;

    /**
     * 新增套餐，同时保存套餐和菜品的关联关系
     * @param setMealDto 包含套餐和菜品的增强实体类
     */
    @Override
    public void saveWithDish(SetMealDto setMealDto) {

        //  保存套餐的基本信息
        save(setMealDto);

        List<SetMealDish> setMealDishes = setMealDto.getSetmealDishes();

        //  为菜品关系添加套餐id
        setMealDishes.stream()
                .peek(item -> item.setSetMealId(setMealDto.getId()))
                .collect(Collectors.toList());

        //  保存套餐和菜品的关联信息
        setMealDishService.saveBatch(setMealDishes);

    }

    /**
     * 根据id批量删除套餐和对应菜品关联表
     * @param ids long集合
     */
    @Override
    public void deleteWithDish(List<Long> ids) {

        //  查询套餐状态是否可删除
        LambdaQueryWrapper<SetMeal> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper
                .in(SetMeal::getId, ids)
                .eq(SetMeal::getStatus, 1);

        if (count(queryWrapper) > 0) {
            throw new CustomException("套餐正在售卖，不可删除！");
        }

        //  删除表中数据
        removeByIds(ids);

        //  删除关系表数据
        LambdaQueryWrapper<SetMealDish> lqw = new LambdaQueryWrapper<>();

        lqw.in(SetMealDish::getSetMealId, ids);

        setMealDishService.remove(lqw);

    }
}
