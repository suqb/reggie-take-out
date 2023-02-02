package com.listener.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.listener.reggie.entity.User;
import com.listener.reggie.mapper.UserMapper;
import com.listener.reggie.service.UserService;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
