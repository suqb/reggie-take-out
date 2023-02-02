package com.listener.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.listener.reggie.common.R;
import com.listener.reggie.entity.Category;
import com.listener.reggie.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    /**
     * 新增菜品分类
     * @param category 菜品分类实体
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category) {

        categoryService.save(category);

        return R.success("新增分类成功");
    }

    /**
     * 菜品分页
     * @param page 需要查询的页码
     * @param pageSize 每页查询的条数
     * @return 返回封装了分页数据的R对象
     */
    @GetMapping("/page")
    public R<Page<Category>> page(int page, int pageSize) {

        Page<Category> pageInfo = new Page<>(page, pageSize);

        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.orderByAsc(Category::getSort);

        categoryService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 根据id删除菜品
     */
    @DeleteMapping
    public R<String> delete(Long ids) {

        categoryService.remove(ids);

        return R.success("分类信息删除成功");
    }

    /**
     * 根据id修改分类信息
     * @param category 菜品分类实体
     * @return 返回处理成功信息
     */
    @PutMapping
    public R<String> update(@RequestBody Category category) {

        categoryService.updateById(category);

        return R.success("分类信息修改成功");
    }

    /**
     * 根据条件查询查询分类数据
     * @param category 菜品分类实体
     * @return 返回封装菜品分类集合的R对象
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category) {

        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(category.getType() != null, Category::getType, category.getType());

        queryWrapper.orderByAsc(Category::getSort)
                .orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(queryWrapper);

        return R.success(list);
    }

}













