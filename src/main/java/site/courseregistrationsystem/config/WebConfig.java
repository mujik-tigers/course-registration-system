package site.courseregistrationsystem.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;
import site.courseregistrationsystem.interceptor.LoginCheckInterceptor;
import site.courseregistrationsystem.util.resolver.LoginArgumentResolver;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

	private final LoginCheckInterceptor loginCheckInterceptor;
	private final LoginArgumentResolver loginArgumentResolver;

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
			.allowedOrigins("*")
			.allowedMethods(
				HttpMethod.HEAD.name(), HttpMethod.GET.name(), HttpMethod.POST.name(),
				HttpMethod.PUT.name(), HttpMethod.PATCH.name(), HttpMethod.DELETE.name(),
				HttpMethod.OPTIONS.name());
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(loginCheckInterceptor)
			.addPathPatterns("/**")
			.excludePathPatterns("/docs/**", "/login", "/registration-period/**");
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(loginArgumentResolver);
	}

}
