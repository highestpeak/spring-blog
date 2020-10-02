package com.highestpeak.springblog.constant;

/**
 * @author highestpeak
 */
public class DefaultValueFactory {
    public static final String ADMIN_SALT = "fuckYouTest";

    /*
    todo: 动态配置这些值？
     */

    /**
     * 文章列表分页的每一页单位长度 (首页文章、标签所含文章)
     */
    public static final int PAGE_UNIT_LENGTH = 10;

    /**
     * 1. 首页/标签下的文章 每一个页面的最大文章数
     * 2. 每次请求标签的最大数目
     */
    public static final int PAGE_MAX_LENGTH = 100;

    /**
     * 默认请求分页返回的额外信息的Map大小
     */
    public static final int DEFAULT_ARTICLE_EXTRA_INFO_MAP_SIZE = 16;

}
