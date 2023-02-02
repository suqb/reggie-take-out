package com.listener.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.listener.reggie.dto.SetMealDto;
import com.listener.reggie.entity.SetMeal;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SetMealService extends IService<SetMeal> {

    /**
     * 新增套餐，同时保存套餐和菜品的关联关系
     * @param setMealDto 包含套餐和菜品的增强实体类
     */
    void saveWithDish(SetMealDto setMealDto);

    /**
     * 根据id批量删除套餐和对应菜品关联表
     * @param ids long集合
     */
    void deleteWithDish(List<Long> ids);
}
