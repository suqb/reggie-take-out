package com.listener.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.listener.reggie.common.R;
import com.listener.reggie.entity.Employee;
import com.listener.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Resource
    private EmployeeService employeeService;

    /**
     * 员工登录
     * @param request http request对象
     * @param employee 前端传输过来封装好的实体
     * @return R<Employee>
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {

        //  明文密码转换为md5加密
        String password = DigestUtils.md5DigestAsHex(employee.getPassword().getBytes());

        //  根据用户名查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        //  查询用户名是否存在
        if (emp == null) {
            return R.error("用户名不存在");
        }

        //  比较密码是否正确
        if (!emp.getPassword().equals(password)) {
            return R.error("密码错误");
        }

        //  查询员工账号是否可用
        if (emp.getStatus() == 0) {
            return R.error("该账号已禁用");
        }

        //  登录成功 将员工id保存于session域
        request
                .getSession()
                .setAttribute("employee", emp.getId());

        return R.success(emp);
    }

    /**
     * 员工退出登录
     * @param request http request对象
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {

        request.getSession().removeAttribute("employee");

        return R.success("退出成功");
    }

    /**
     * 新增员工
     * @param employee 前端传输过来封装好的实体
     * @param request http request对象
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {

        //  初始化员工密码
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        System.out.println(employee);

        employeeService.save(employee);

        return R.success("添加成功！");
    }

    /**
     * 分页查询
     * @param page 查询的页码
     * @param pageSize 需要查询的条数
     * @param name 条件
     * @return 返回分页数据
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {

        //  构造分页对象
        Page pageInfo = new Page(page, pageSize);

        //  创建条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();

        //  添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);

        //  添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        //  执行查询
        employeeService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 员工信息更新
     * @param employee 前端传输过来封装好的实体
     * @return 返回修改成功信息
     */
    @PutMapping
    public R<String> update(@RequestBody Employee employee) {

        employeeService.updateById(employee);

        return R.success("员工信息修改成功");
    }

    /**
     * 根据id查询单个员工信息
     * @param id 员工id
     * @return 响应信息
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {

        Employee employee = employeeService.getById(id);

        return employee != null ? R.success(employee) : R.error("没有查询到对应的员工信息");

    }
}















