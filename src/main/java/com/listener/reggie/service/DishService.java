package com.listener.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.listener.reggie.dto.DishDto;
import com.listener.reggie.entity.Dish;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

public interface DishService extends IService<Dish> {

    void saveWithFlavor(DishDto dishDto);   //  保存菜品和对应口味

    DishDto getByIdWithFlavor(Long id);     //  根据id查询对应菜品和口味信息

    void updateWithFlavor(DishDto dishDto); //  更新菜品信息

}
