package com.highestpeak.springblog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.highestpeak.springblog.model.entity.Article;
import com.highestpeak.springblog.model.entity.ArticleTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * @author highestpeak
 */
@Mapper
@Repository
public interface ArticleTagMapper extends BaseMapper<ArticleTag> {


    /**
     * 找到 tagId 下所有的文章
     *
     * @param page   分页 page
     * @param tagIds tag ids
     * @return article belongs to tag
     */
    @Select("<script>" +
            "select id,title,summary,create_time,update_time from " +
            "(select article as id from article_tag where tag in " +
                "<foreach item='item' index='index' collection='tagIds' open='(' separator=',' close=')'>" +
                "#{item}" +
                "</foreach>" +
            "#{tagIds}) article_ " +
            "join article on article.id=article_.id " +
            "</script>")
    IPage<Article> articleBelongsToTag(Page<?> page, List<Integer> tagIds);
}
