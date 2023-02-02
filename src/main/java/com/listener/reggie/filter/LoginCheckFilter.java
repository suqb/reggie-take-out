package com.listener.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.listener.reggie.common.BaseContext;
import com.listener.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    //  路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        //  转换请求类型
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //  需要放行的uri
        String[] uris = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
        };

        //  检测和请求的uri是否需要放行
        if (check(uris, request.getRequestURI())) {

            filterChain.doFilter(request,response);

            return;
        }

        //  放行已登录用户
        if (request.getSession().getAttribute("employee") != null) {

            BaseContext.setCurrentId((Long) request.getSession().getAttribute("employee"));

            filterChain.doFilter(request,response);

            return;
        }

        //  放行移动端已登录用户
        if (request.getSession().getAttribute("user") != null) {

            BaseContext.setCurrentId((Long) request.getSession().getAttribute("user"));

            filterChain.doFilter(request,response);

            return;
        }

        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));


    }

    /**
     * 路径匹配，检查本次请求是否需要放行
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls, String requestURI) {

        for (String url : urls) {

            if (PATH_MATCHER.match(url,requestURI)) {
                return true;
            }

        }

        return false;
    }
}
