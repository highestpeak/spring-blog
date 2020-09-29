package com.highestpeak.springblog.model.dto;

import com.highestpeak.springblog.model.bo.ArticleBO;
import lombok.Getter;
import lombok.Setter;


/**
 * @author highestpeak
 * 为方便把增删改查放到一个请求里，所以增加了修改类型字段
 */
@Getter
@Setter
public class ArticleModifyDTO {

    /**
     * 对article的什么进行修改
     */
    public enum ArticleModifyEnum {
        /**
         * 对文章修改
         */
        ARTICLE,
        /**
         * 对标签修改
         */
        TAGS,
        /**
         * 对文章存储位置修改
         */
        LOCATION,
        /**
         * 对文章评价修改
         */
        STAR
    }

    private ArticleModifyEnum articleModifyCol;

    /**
     * 增加文章: article是新的article的信息
     * 删除文章: article的id是要删除的id
     * 更新文章时: article的id是更新的id，
     *  未更新字段为不设置即最终为null，
     *  而更新字段为新值
     */
    private ArticleBO article;
}
