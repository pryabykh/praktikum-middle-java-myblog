package com.pryabykh.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@ComponentScan(basePackages = {"com.pryabykh"})
@PropertySource("classpath:application.properties")
public class WebConfiguration {}
