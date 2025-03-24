package com.ecommerce.inventoryservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Using dedicated CorsFilter instead of this configuration
@Configuration
public class WebConfig implements WebMvcConfigurer {
    // CORS configuration moved to CorsFilter
}
