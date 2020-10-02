package com.highestpeak.springblog.model.bo;

import com.highestpeak.springblog.model.entity.Article;
import com.highestpeak.springblog.model.entity.ArticleLocation;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author highestpeak
 */
@Getter
@Setter
@Builder
public class ArticleBO {
    private Article article;

    /**
     * todo：查三次表，然后缓存结果，返回的时候这个list存储id，
     *  注意缓存的有效性，内存的大小限制
     *  分析一下使用mybatis缓存还是spring的cache缓存、还是redis
     */
    private List<Integer> tagIdList;

    /**
     * 文章的所有位置
     */
    private List<ArticleLocation> articleLocations;

    /**
     * 平均评分
     * todo: 由于oauth暂未实现，暂时没有设置该字段
     */
    private Integer stars;

    /**
     * 评论数
     * todo: 由于oauth暂未实现，暂时没有设置该字段
     */
    private Integer comments;

}
