package com.mysite.irms.config;

import java.io.IOException;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.resource.PathResourceResolver;

@Configuration
public class SpaWebConfig implements WebMvcConfigurer {
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**").addResourceLocations("classpath:/static/").resourceChain(true)
				.addResolver(new PathResourceResolver() {
					@Override
					protected Resource getResource(String path, Resource location) throws IOException {
						Resource requested = location.createRelative(path);
						return requested.exists() && requested.isReadable() ? requested
								: new ClassPathResource("/index.html"); // fallback
					}
				});
	}

//	@Override
//	public void addViewControllers(ViewControllerRegistry registry) {
//		registry.addViewController("/{spring:[\\w\\-]+}").setViewName("forward:/index.html");
//		registry.addViewController("/**/{spring:[\\w\\-]+}").setViewName("forward:/index.html");
//		registry.addViewController("/{spring:[\\w\\-]+}/**{spring:?!(\\.js|\\.css)$}")
//				.setViewName("forward:/index.html");
//	}
}
