package com.jena.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConfigInfo {

    @Value("${project.filePath}")
    public String filePath;

}
