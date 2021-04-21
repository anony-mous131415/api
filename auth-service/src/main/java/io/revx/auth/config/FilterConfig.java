package io.revx.auth.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.revx.auth.demo.*;

@Configuration
public class FilterConfig {

	@Bean
	public FilterRegistrationBean<DemoUserFilterAuth> logFilter() {
	    FilterRegistrationBean<DemoUserFilterAuth> registrationBean = new FilterRegistrationBean<>();
	    registrationBean.setFilter(new DemoUserFilterAuth());
	    registrationBean.addUrlPatterns("/v2/auth/userinfo","/v2/auth/user-privileges");
	    return registrationBean;
	}
}
