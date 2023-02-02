package com.listener.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.listener.reggie.dto.DishDto;
import com.listener.reggie.entity.Dish;
import com.listener.reggie.entity.DishFlavor;
import com.listener.reggie.mapper.DishMapper;
import com.listener.reggie.service.DishFlavorService;
import com.listener.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Resource
    private DishFlavorService dishFlavorService;
    /**
     * 保存菜品和菜品对应的口味
     * @param dishDto 注入继承了菜品和增加了菜品口味属性的对象
     */
    @Override
    public void saveWithFlavor(DishDto dishDto) {

        //  保存菜品基本信息到菜品表
        save(dishDto);

        //  获取保存的菜品id
        Long id = dishDto.getId();

        //  获取菜品对应的口味
        List<DishFlavor> flavors = dishDto.getFlavors();

        //  对菜品口味外键赋值
        flavors = flavors.stream()
                .peek(item -> item.setDishId(id))
                .collect(Collectors.toList());

//        map(item -> {item.setDishId(id);return item;}) 据说不推荐使用peek，peek用于调试，推荐使用map

        //  保存菜品口味带菜品表
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id查询对应菜品信息和口味信息
     * @param id 需要查询的菜品id
     * @return 返回单个菜品
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {

        //  根据id查询菜品信息
        Dish dish = getById(id);

        //  构造增强菜品实体类
        DishDto dishDto = new DishDto();

        //  将查询出来的数据拷贝到增强菜品实体类
        BeanUtils.copyProperties(dish, dishDto);

        //  构造查询对象
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();

        //  输出查询条件
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());

        //  查询菜品口味集合
        List<DishFlavor> list = dishFlavorService.list(queryWrapper);

        //  设置菜品口味集合
        dishDto.setFlavors(list);

        return dishDto;
    }

    /**
     * 更新菜品信息
     * @param dishDto 菜品强化实体类
     */
    @Override
    public void updateWithFlavor(DishDto dishDto) {

        //  根据id更新菜品
        updateById(dishDto);

        //  创建删除口味条件对象
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();

        //  添加删除条件
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());

        //  删除原先口味
        dishFlavorService.remove(queryWrapper);

        //  获取前端修改后的口味
        List<DishFlavor> flavors = dishDto.getFlavors();

        //
        flavors = flavors.stream()
                .peek(item -> item.setDishId(dishDto.getId()))
                .collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }
}
