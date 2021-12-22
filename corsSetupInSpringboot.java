
----cors application //date 22-12-2021 before lunch

package com.example.springboottraining;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class CorsConfig implements WebMvcConfigurer {


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/users/**")
                .allowedOrigins("https://www.w3schools.com")
                ;
				
				
				//can add multive entry in registry
				registry.addMapping("/users/**")
                .allowedOrigins("https://www.w3schools.com")
                ;
    }
}
