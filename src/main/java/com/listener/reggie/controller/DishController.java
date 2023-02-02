package com.listener.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.listener.reggie.common.CustomException;
import com.listener.reggie.common.R;
import com.listener.reggie.dto.DishDto;
import com.listener.reggie.entity.Category;
import com.listener.reggie.entity.Dish;
import com.listener.reggie.entity.SetMealDish;
import com.listener.reggie.service.CategoryService;
import com.listener.reggie.service.DishFlavorService;
import com.listener.reggie.service.DishService;
import com.listener.reggie.service.SetMealDishService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品管理控制器
 */
@RestController
@RequestMapping("/dish")
public class DishController {

    @Resource
    private DishService dishService;
    @Resource
    private DishFlavorService dishFlavorService;
    @Resource
    private CategoryService categoryService;
    @Resource
    private SetMealDishService setMealDishService;

    /**
     * 新增菜品
     * @param dishDto 强化实体类
     * @return 返回插入成功
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {

        dishService.saveWithFlavor(dishDto);

        return R.success("新增菜品成功");
    }

    /**
     * 查询菜品分页数据
     * @param page 需要查询的页码
     * @param pageSize 每页查询的条数
     * @param name 菜品名称模糊查询
     * @return 返回菜品分页信息
     */
    @GetMapping("/page")
    public R<Page<DishDto>> page(Integer page, Integer pageSize, String name) {

        //  创建满足需求的dto对象
        Page<DishDto> dishDtoPage = new Page<>();

        //  创建条件查询对象
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();

        //  输入查询条件
        queryWrapper.like(name != null && name.trim().equals(""), Dish::getName, name)
                .orderByDesc(Dish::getUpdateTime);

        //  根据条件查询出所有菜品
        Page<Dish> dishPage = dishService.page(new Page<>(page, pageSize), queryWrapper);

        //  进行对象拷贝，忽略需要修改的数据
        BeanUtils.copyProperties(dishPage, dishDtoPage, "records");

        //  获取菜品数据集合
        List<Dish> records = dishPage.getRecords();

        List<DishDto> dtoList = records.stream()
                .map(item -> {

                    //  新建dto对象
                    DishDto dishDto = new DishDto();

                    //  将原来查询出来的数据拷贝进去
                    BeanUtils.copyProperties(item, dishDto);

                    //  获取查询出来的分类id
                    Long categoryId = item.getCategoryId();

                    //  根据id查询分类名称
                    Category category = categoryService.getById(categoryId);

                    if (category != null) {

                        //  将查询出来的分类名称赋值给新的dto
                        dishDto.setCategoryName(category.getName());
                    }

                    return dishDto;
                })
                .collect(Collectors.toList());

        dishDtoPage.setRecords(dtoList);

        return R.success(dishDtoPage);
    }

    /**
     * 根据id查询单个菜品
     * @param id 菜品id
     * @return 返回单个菜品
     */
    @GetMapping("/{id}")
    public R<DishDto> select(@PathVariable Long id) {

        return R.success(dishService.getByIdWithFlavor(id));
    }

    /**
     * 更新菜品
     * @param dishDto 强化实体类
     * @return 返回插入成功
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {

        dishService.updateWithFlavor(dishDto);

        return R.success("更新菜品成功");
    }

    /**
     * 根据条件查询菜品数据
     * @param dish 增强通用性，使用Dish实体类接受查询条件
     * @return 返回菜品list集合
     */
    @GetMapping("/list")
    public R<List<Dish>> list(Dish dish) {

        //  构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();

        //  添加条件
        queryWrapper
                //  分类id
                .eq(ObjectUtils.isNotEmpty(dish.getCategoryId()), Dish::getCategoryId, dish.getCategoryId())
                //  为启售状态的菜品
                .eq(Dish::getStatus, 1)
                //  根据菜品排序查询
                .orderByAsc(Dish::getSort)
                //  根据菜品更新时间查询
                .orderByDesc(Dish::getUpdateTime);

        //  查询数据
        List<Dish> list = dishService.list(queryWrapper);

        //  响应客户端
        return R.success(list);
    }

    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable Integer status, @RequestParam Long[] ids) {

        ArrayList<Dish> dishes = new ArrayList<>();

        for (Long id : ids) {

            Dish dish = new Dish();

            dish.setStatus(status);

            dish.setId(id);

            dishes.add(dish);
        }

        dishService.updateBatchById(dishes);

        return R.success("SUCCESS");
    }

    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<SetMealDish> wrapper = new LambdaQueryWrapper<>();

        queryWrapper
                .in(Dish::getId, ids)
                        .eq(Dish::getStatus, 1);
        wrapper
                .in(SetMealDish::getDishId,ids);

        if (dishService.count(queryWrapper) != 0) {

            throw new CustomException("当前菜品正在售卖，不可删除！");

        } else if (setMealDishService.count(wrapper) != 0) {

            throw new CustomException("当前菜品关联了套餐，不可删除！");

        }

        dishService.removeByIds(ids);



        return R.success("SUCCESS");
    }
}












