package com.highestpeak.springblog.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.highestpeak.springblog.constant.SqlTableConstant;
import com.highestpeak.springblog.mapper.TagMapper;
import com.highestpeak.springblog.model.entity.ArticleTag;
import com.highestpeak.springblog.model.entity.Tag;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author highestpeak
 */
@Service("tagService")
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements IService<Tag> {

    private final TagServiceImpl self;

    private final ArticleTagServiceImpl articleTagService;

    private final ArticleServiceImpl articleService;

    public TagServiceImpl(@Lazy TagServiceImpl self, ArticleTagServiceImpl articleTagService,
                          ArticleServiceImpl articleService) {
        this.self = self;
        this.articleTagService = articleTagService;
        this.articleService = articleService;
    }

    /**
     * todo: cache 缓存
     * 某个tag
     * @param tagId id
     * @return tag
     */
    @Cacheable("tag")
    public Tag selectTag(int tagId) {
        return self.getById(tagId);
    }

    @Cacheable("tags")
    public List<Tag> selectTagList(List<Integer> tagIdList){
        return self.getBaseMapper().selectBatchIds(tagIdList);
    }

    private QueryWrapper<Tag> tagNameQueryWrapper;

    {
        tagNameQueryWrapper = new QueryWrapper<>();
        tagNameQueryWrapper.select(SqlTableConstant.TagColName.NAME);
    }

    /**
     * 可能一个Tag的文章过多
     */
    private static final int MAX_ARTICLE_NUM_OF_TAG_SHOW = 50;

    /**
     * 选取某个tag的所有文章
     * @param name
     * @param start
     * @param len
     * @return
     */
    @Cacheable("tagname")
    public Object tagWithItsArticles(String name, int start, int len) {
        Tag target = self.getOne(tagNameQueryWrapper);
        List<ArticleTag> articleTags = articleTagService.tagArticleList(target.getId());
        len = Math.min(MAX_ARTICLE_NUM_OF_TAG_SHOW,len);
        List<Integer> articleIdList = articleTags.stream().map(ArticleTag::getId).collect(Collectors.toList());
        return articleService.articleUnitList(articleIdList, start, len);
    }
}
