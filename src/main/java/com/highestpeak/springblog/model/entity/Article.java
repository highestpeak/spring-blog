package com.highestpeak.springblog.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.highestpeak.springblog.constant.enumerate.ArticlesSortEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author highestpeak
 */
@Getter
@Setter
@Builder
public class Article {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String title;

    @TableField(value = "summary")
    private String description;

    @TableField(value = "create_time")
    private LocalDateTime createTime;

    @TableField(value = "update_time")
    private LocalDateTime updateTime;

    public static Map<ArticlesSortEnum,String> sortByToColName = new HashMap<ArticlesSortEnum, String>(){{
        // todo
        put(ArticlesSortEnum.WRITE_TIME,"");
        put(ArticlesSortEnum.WRITE_TIME,"");
    }};
}