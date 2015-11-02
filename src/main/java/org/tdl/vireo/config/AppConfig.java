package org.tdl.vireo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.tdl.vireo.controller.interceptor.AppRestInterceptor;

import edu.tamu.framework.config.CoreWebAppConfig;

@Configuration
@ComponentScan(basePackages = {"org.tdl.vireo.config", "org.tdl.vireo.controller"})
@ConfigurationProperties(prefix="vireo.controller")
public class AppConfig extends CoreWebAppConfig {

    /**
     * Rest interceptor bean.
     *
     * @return      RestInterceptor
     *
     */
    @Bean
    public AppRestInterceptor restInterceptor() {
        return new AppRestInterceptor();
    }
    
    /**
     * Add interceptor to interceptor registry.
     *
     * @param       registry       InterceptorRegistry
     *
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(restInterceptor()).addPathPatterns("/**");
    }
    
}
