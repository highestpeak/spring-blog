package com.highestpeak.springblog.model.vo;

import com.highestpeak.springblog.model.entity.Article;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * @author highestpeak
 * 标签所含文章列表的 VO 类
 */
@Getter
@Setter
public class ArticleListBelongToTagVO {
    private List<Article> articleList;
    private Map<String,Object> extra;
}
