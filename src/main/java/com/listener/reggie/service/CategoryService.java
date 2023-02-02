package com.listener.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.listener.reggie.entity.Category;

public interface CategoryService extends IService<Category> {

    public void remove(Long id);
}
