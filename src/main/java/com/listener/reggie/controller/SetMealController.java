package com.listener.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.listener.reggie.common.R;
import com.listener.reggie.dto.SetMealDto;
import com.listener.reggie.entity.Category;
import com.listener.reggie.entity.SetMeal;
import com.listener.reggie.service.CategoryService;
import com.listener.reggie.service.SetMealDishService;
import com.listener.reggie.service.SetMealService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐管理控制器
 */
@RestController
@RequestMapping("/setMeal")
public class SetMealController {

    @Resource
    private SetMealService setMealService;
    @Resource
    private CategoryService categoryService;
    @Resource
    private SetMealDishService setMealDishService;

    /**
     * 新增套餐
     * @param setMealDto 包含套餐和菜品的增强实体类
     * @return 返回响应新增成功结果
     */
    @PostMapping
    public R<String> save(@RequestBody SetMealDto setMealDto) {

        setMealService.saveWithDish(setMealDto);

        return R.success("SUCCESS");
    }

    /**
     * 套餐管理分页查询
     * @param page 需要查询的页码
     * @param pageSize 需要查询的条数
     * @param name 条件查询
     * @return 返回分页数据
     */
    @GetMapping("/page")
    public R<Page<SetMealDto>> page(Integer page, Integer pageSize, String name) {

        //  构造分页查询对象
        Page<SetMeal> pageInfo = new Page<>(page, pageSize);
        //  构造满足前端数据的增强对象
        Page<SetMealDto> dtoPage = new Page<>();
        //  构造条件查询对象
        LambdaQueryWrapper<SetMeal> queryWrapper = new LambdaQueryWrapper<>();
        //  条件查询条件
        queryWrapper
                .like(ObjectUtils.isNotEmpty(name), SetMeal::getName, name)
                .orderByDesc(SetMeal::getUpdateTime);
        //  查询数据库获取分页结果
        setMealService.page(pageInfo, queryWrapper);
        //  将查询出来的数据copy到满足需求的数据
        BeanUtils.copyProperties(pageInfo, dtoPage, "records");
        //  获取查询出来的菜品id
        List<SetMeal> records = pageInfo.getRecords();
        //  遍历将菜品id转换为菜品名称
        List<SetMealDto> list = records.stream()

                .map(item -> {

                    SetMealDto setMealDto = new SetMealDto();

                    BeanUtils.copyProperties(item, setMealDto);

                    Category category = categoryService.getById(item.getCategoryId());

                    if (category != null) {
                        setMealDto.setCategoryName(category.getName());
                    }

                    return setMealDto;
                })
                .collect(Collectors.toList());


        dtoPage.setRecords(list);

        return R.success(dtoPage);
    }

    /**
     * 根据id删除套餐
     * @param ids 可以传入多个菜品id
     * @return 只返回成功响应
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {

        setMealService.deleteWithDish(ids);

        return R.success("SUCCESS");
    }

    /**
     * 批量修改套餐售卖状态
     * @param status 需要修改的状态，1->售卖ing 0->停售ing
     * @param ids 需要修改的套餐id
     * @return 只响应成功结果
     */
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable Integer status, Long[] ids) {

        ArrayList<SetMeal> setMeals = new ArrayList<>();

        for (Long id : ids) {

            SetMeal setMeal = new SetMeal();

            setMeal.setStatus(status);

            setMeal.setId(id);

            setMeals.add(setMeal);
        }

        setMealService.updateBatchById(setMeals);

        return R.success("SUCCESS");

    }


}
