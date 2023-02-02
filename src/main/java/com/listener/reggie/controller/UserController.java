package com.listener.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.listener.reggie.common.R;
import com.listener.reggie.entity.User;
import com.listener.reggie.service.UserService;
import com.listener.reggie.utils.SMSUtils;
import com.listener.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 前台用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 发送数据短信验证码
     * @param user 接收用户的手机号码
     * @return 发送成功
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(User user, HttpSession session) {

        String phone = user.getPhone();

        if (phone != null) {

            String code = ValidateCodeUtils.generateValidateCode(4).toString();

            log.info("发送的验证码为{}=============================================>",code);

//            SMSUtils.sendMessage("瑞吉买卖","", phone, code);

            session.setAttribute(phone, code);

            return R.success("发送成功！");
        }

        return R.error("发送失败！");
    }

    /**
     * 移动端用户登录
     * @param map 接收用户的手机号码和验证码
     * @return 发送成功
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map<String, Object> map, HttpSession session) {

        //  获取提交的手机号
        String phone = map.get("phone").toString();

        //  获取提交的验证码
        String code = map.get("code").toString();

        //  从Session中获取保存的验证码
        Object codeInSession = session.getAttribute(phone);

        //  进行验证码比对
        if (code != null && code.equals(codeInSession)) {

            //  进行用户验证，如果用户存在侧直接登录，否则进行注册
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

            wrapper.eq(User::getPhone, phone);

            User user = userService.getOne(wrapper);

            if (user == null) {

                user = new User();
                user.setPhone(phone);
                user.setStatus(1);

                userService.save(user);
            }

            session.setAttribute("user", user.getId());

            return R.success(user);

        }
        return R.error("登录失败！");
    }
}
