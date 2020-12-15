package com.hss.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class DefaultView extends WebMvcConfigurerAdapter {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {

        registry.addViewController("/").setViewName("index");
        registry.addViewController("/index.html").setViewName("index");
        registry.addViewController("/index1.html").setViewName("index1");
        registry.addViewController("/socket1.html").setViewName("socket1");
        registry.addViewController("/socket2.html").setViewName("socket2");
        registry.addViewController("/socket3.html").setViewName("socket3");
        registry.addViewController("/errorMsg.html").setViewName("errorMsg");
        registry.addViewController("/successMsg.html").setViewName("successMsg");
        //设置优先级
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
        super.addViewControllers(registry);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //将templates目录下的CSS、JS文件映射为静态资源，防止Spring把这些资源识别成thymeleaf模版
        registry.addResourceHandler("/templates/**.js").addResourceLocations("classpath:/templates/");
        registry.addResourceHandler("/templates/**.css").addResourceLocations("classpath:/templates/");
        //其他静态资源
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
    }
}

