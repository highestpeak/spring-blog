package com.highestpeak.springblog.model.vo;

import com.highestpeak.springblog.model.bo.ArticleBO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * @author highestpeak
 */
@Getter
@Setter
public class ArticleListVO {
    private List<ArticleBO> articleList;
    private Map<String,Object> extra;
}
