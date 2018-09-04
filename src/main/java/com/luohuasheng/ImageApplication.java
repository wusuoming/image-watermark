package com.luohuasheng;

import org.opencv.core.Core;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.annotation.MultipartConfig;

@SpringBootApplication
@EnableSwagger2
@MultipartConfig(maxFileSize = 1024 * 1024 * 1024, maxRequestSize = 1024 * 1024 * 1024)
public class ImageApplication extends WebMvcConfigurationSupport {

    public static void main(String[] args) {

        System.out.println("Welcome to OpenCV " + Core.VERSION + ", lib is: " + Core.NATIVE_LIBRARY_NAME);
        SpringApplication.run(ImageApplication.class, args);
    }


    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        //setUseSuffixPatternMatch 后缀模式匹配
        configurer.setUseSuffixPatternMatch(true);
        //setUseTrailingSlashMatch 自动后缀路径模式匹配
        configurer.setUseTrailingSlashMatch(true);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/swagger-ui.html")
                .addResourceLocations("classpath:META-INF/resources/swagger-ui.html");
        registry.addResourceHandler("/index.html")
                .addResourceLocations("classpath:templates/index.html");
        registry.addResourceHandler("/img/**")
                .addResourceLocations("classpath:img/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
