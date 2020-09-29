package com.highestpeak.springblog.constant.enumerate;

/**
 * @author highestpeak
 */
public enum CommentsSortEnum {
    /**
     * 发表时间排序
     */
    TIME,

    /**
     * 最新回复
     */
    REPLY_NEW,

    /**
     * 回复数量
     */
    REPLY_NUM,

    /**
     * 默认排序，自己根据一系列策略指定的排序
     */
    DEFAULT
}
