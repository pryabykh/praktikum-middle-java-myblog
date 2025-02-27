package com.pryabykh.configuration;

import com.pryabykh.service.BlogServiceImplTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@ComponentScan(basePackages = {"com.pryabykh"},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = BlogServiceImplTest.DaoConfig.class))
@PropertySource("classpath:test-application.properties")
public class WebConfiguration {
}
