package com.highestpeak.springblog;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
class SpringBlogApplicationTests {

    @Test
    void contextLoads() {
        String test = "null";
        Optional.ofNullable(test).ifPresent(System.out::println);
    }

}
