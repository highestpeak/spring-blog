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
     * 文章列表分页的每一页单位长度
     */
    public static final int PAGE_UNIT_LENGTH = 10;

    /**
     * 每一个页面的最大文章数
     */
    public static final int PAGE_MAX_LENGTH = 100;
}
