package com.quanxiaoha.weblog.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class LocalUploadResourceConfig implements WebMvcConfigurer {

    private static final String LOCAL_UPLOAD_DIR = "uploads";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path currentDir = Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize();
        Path currentUploadDir = currentDir.resolve(LOCAL_UPLOAD_DIR).normalize();
        Path parentUploadDir = currentDir.getParent() == null
                ? currentUploadDir
                : currentDir.getParent().resolve(LOCAL_UPLOAD_DIR).normalize();
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(
                        currentUploadDir.toUri().toString(),
                        parentUploadDir.toUri().toString()
                );
    }
}
