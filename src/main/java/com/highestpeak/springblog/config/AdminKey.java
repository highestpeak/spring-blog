package com.highestpeak.springblog.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author highestpeak
 */
@Getter
@Setter
@Component
public class AdminKey {
    @Value("${admin.key}")
    private String key;
}
