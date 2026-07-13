package com.pravin.demo.config;

import java.util.Arrays;

import com.pravin.demo.auth.TokenAuthorizationInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	private final TokenAuthorizationInterceptor tokenAuthorizationInterceptor;
	private final String allowedOrigins;

	public WebConfig(TokenAuthorizationInterceptor tokenAuthorizationInterceptor,
			@Value("${app.cors.allowed-origins}") String allowedOrigins) {
		this.tokenAuthorizationInterceptor = tokenAuthorizationInterceptor;
		this.allowedOrigins = allowedOrigins;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(tokenAuthorizationInterceptor)
				.addPathPatterns("/api/**");
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/api/**")
				.allowedOrigins(Arrays.stream(allowedOrigins.split(","))
						.map(String::trim)
						.filter(origin -> !origin.isEmpty())
						.toArray(String[]::new))
				.allowedMethods(
						HttpMethod.GET.name(),
						HttpMethod.POST.name(),
						HttpMethod.PUT.name(),
						HttpMethod.DELETE.name(),
						HttpMethod.OPTIONS.name())
				.allowedHeaders("*")
				.exposedHeaders("Content-Type")
				.maxAge(3600);
	}
}
