package com.reviewia.reviewiabackend.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PropertyLoader {
    @Value("${reviewia.serverUrl}")
    public String serverUrl;
}
