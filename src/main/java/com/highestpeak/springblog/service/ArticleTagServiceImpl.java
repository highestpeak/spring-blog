package com.highestpeak.springblog.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.highestpeak.springblog.constant.SqlTableConstant;
import com.highestpeak.springblog.constant.enumerate.ModifyTypeEnum;
import com.highestpeak.springblog.mapper.ArticleTagMapper;
import com.highestpeak.springblog.model.bo.ArticleBO;
import com.highestpeak.springblog.model.dto.ArticleModifyDTO;
import com.highestpeak.springblog.model.entity.ArticleTag;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author highestpeak
 */
@Service("articleTag")
public class ArticleTagServiceImpl extends ServiceImpl<ArticleTagMapper, ArticleTag> implements IService<ArticleTag> {

    private final ArticleTagServiceImpl self;
    private final UtilService utilService;

    public ArticleTagServiceImpl(@Lazy ArticleTagServiceImpl self, UtilService utilService) {
        this.self = self;
        this.utilService = utilService;
    }

    /**
     * 查找一个文章所有 tag id 的wrapper
     * 省去每次select的时间和创建wrapper的时间
     */
    private QueryWrapper<ArticleTag> tagOfArticleQueryWrapper;

    {
        tagOfArticleQueryWrapper = new QueryWrapper<>();
    }

    public List<ArticleTag> articleTagList(int article) {
        tagOfArticleQueryWrapper.clear();
        tagOfArticleQueryWrapper.eq(SqlTableConstant.ArticleTagColName.ARTICLE, article);
        return self.getBaseMapper().selectList(tagOfArticleQueryWrapper);
    }

    public List<ArticleTag> articlesTagList(List<Integer> articleIdList) {
        tagOfArticleQueryWrapper.clear();
        tagOfArticleQueryWrapper.in(SqlTableConstant.ArticleTagColName.ARTICLE,articleIdList);
        tagOfArticleQueryWrapper.orderBy(true, true, SqlTableConstant.ArticleTagColName.ARTICLE);
        return self.getBaseMapper().selectList(tagOfArticleQueryWrapper);
    }

    private QueryWrapper<ArticleTag> articleOfTagQueryWrapper;

    {
        articleOfTagQueryWrapper = new QueryWrapper<>();
        articleOfTagQueryWrapper.select(SqlTableConstant.ArticleTagColName.ARTICLE);
    }

    /**
     * 计算某个 tag 所包含的文章数目
     * @param tagId tag id
     * @return 文章数目
     */
    public int countArticleBelongToTag(int tagId){
        articleOfTagQueryWrapper.eq(SqlTableConstant.ArticleTagColName.TAG,tagId);
        return self.count(articleOfTagQueryWrapper);
    }

    private ArticleTag.ArticleTagBuilder articleTagBuilder;

    {
        articleTagBuilder = ArticleTag.builder();
    }

    public Stream<ArticleTag> buildArticleTagList(ArticleBO article) {
        return article.getTagIdList().stream().map(tag -> articleTagBuilder
                .article(article.getArticle().getId())
                .tag(tag).build()
        );
    }

    public void modifyArticleTag(List<ArticleModifyDTO> articleModifyDTOList,
                                 Map<ModifyTypeEnum, List<String>> errorMsgBuilder) {
        if (articleModifyDTOList == null) {
            return;
        }
        Map<Integer, List<ArticleTag>> articleTagToUpdate = articleModifyDTOList.stream()
                .map(ArticleModifyDTO::getArticle)
                .flatMap(articleBO -> articleBO.getTagIdList().stream().map(tagId ->
                        articleTagBuilder.tag(tagId).article(articleBO.getArticle().getId()).build()
                ))
                .collect(Collectors.groupingBy(ArticleTag::getArticle));

        StringBuilder delFailMsgBuilder = new StringBuilder();
        StringBuilder insertFailMsgBuilder = new StringBuilder();
        articleTagToUpdate.forEach((article, newArticleTags) -> {
            List<ArticleTag> oldArticleTags = self.articleTagList(article);

            // 删除旧的,插入新的
            utilService.delAndInsertRow(
                    oldArticleTags, newArticleTags, self, ArticleTag::getId,
                    article, delFailMsgBuilder, insertFailMsgBuilder
            );
        });

        delFailMsgBuilder.deleteCharAt(delFailMsgBuilder.length() - 1);
        delFailMsgBuilder.insert(0, "删除旧ArticleTag失败列表:");
        insertFailMsgBuilder.deleteCharAt(insertFailMsgBuilder.length() - 1);
        insertFailMsgBuilder.insert(0, "插入新ArticleTag失败列表:");
        List<String> errorList = errorMsgBuilder.get(ModifyTypeEnum.MODIFY);
        errorList.add(delFailMsgBuilder.toString());
        errorList.add(insertFailMsgBuilder.toString());
    }
}
