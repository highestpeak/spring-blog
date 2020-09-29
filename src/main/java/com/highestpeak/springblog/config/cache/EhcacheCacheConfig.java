package com.highestpeak.springblog.config.cache;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * @author highestpeak
 */
@Configuration
@EnableCaching
public class EhcacheCacheConfig{
    /*
     * todo: 对于文章列表的size注意缓存一致性
     */
}
