package com.listener.reggie.config;

import com.listener.reggie.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

@Slf4j
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {

    /**
     * 静态资源映射【放行静态资源】
     * @param registry 注册对象
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {

        log.info("release static resource=======================>");

        //  注册放行前台路径
        registry
                //  添加资源处理器放行所有backend路径的目录
                .addResourceHandler("/backend/**")
                //  路径位置
                .addResourceLocations("classpath:/backend/");

        //  注册放行后台路径
        registry
                //  添加资源处理器放行所有backend路径的目录
                .addResourceHandler("/front/**")
                //  路径位置
                .addResourceLocations("classpath:/front/");
    }


    /**
     * 扩展Mvc框架提供的转换器
     * @param converters 转换器集合
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {

        //  创建消息转化器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();

        //  设置转换器对象
        messageConverter.setObjectMapper(new JacksonObjectMapper());

        //  将消息转换器对象追加到Mvc框架的转换容器集合中
        converters.add(0, messageConverter);
    }
}
