package com.highestpeak.springblog.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.highestpeak.springblog.constant.DefaultValueFactory;
import com.highestpeak.springblog.constant.SqlTableConstant;
import com.highestpeak.springblog.mapper.TagMapper;
import com.highestpeak.springblog.mapper.TagPathMapper;
import com.highestpeak.springblog.model.entity.Article;
import com.highestpeak.springblog.model.entity.ArticleTag;
import com.highestpeak.springblog.model.entity.Tag;
import com.highestpeak.springblog.model.entity.TagPath;
import com.highestpeak.springblog.model.vo.ArticleListBelongToTagVO;
import com.highestpeak.springblog.model.vo.TagPageFirstInVO;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author highestpeak
 */
@Service("tagService")
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements IService<Tag> {

    private final TagServiceImpl self;

    private final ArticleTagServiceImpl articleTagService;

    private final TagPathMapper tagPathMapper;

    public TagServiceImpl(@Lazy TagServiceImpl self, ArticleTagServiceImpl articleTagService,
                          TagPathMapper tagPathMapper) {
        this.self = self;
        this.articleTagService = articleTagService;
        this.tagPathMapper = tagPathMapper;
    }

    /* ----------------- simple tags search --------------- */

    /**
     * todo: 这会不会有线程安全问题
     */
    private QueryWrapper<Tag> tagNameQueryWrapper;

    {
        tagNameQueryWrapper = new QueryWrapper<>();
    }

    /**
     * 某个tag
     */
    @Cacheable(value = "simpleMap:cache:10min", key = "'selectTag_tagName_'+#tagName")
    public Tag selectTag(String tagName) {
        tagNameQueryWrapper.clear();
        tagNameQueryWrapper.eq(SqlTableConstant.TagColName.NAME, tagName);
        return self.getOne(tagNameQueryWrapper);
    }

    /**
     * 一次性查找一个 tag 列表
     *
     * @param tagIdList tag id list
     * @return tag entity list
     */
    public List<Tag> selectTagList(List<Integer> tagIdList) {
        return self.getBaseMapper().selectBatchIds(tagIdList);
    }

    /**
     * 找到 tagId 下的所有子 tag 的 id 的 list
     *
     * @param tagId tag id
     * @return 所有子 tag 的 id 的 list
     */
    @Cacheable(value = "simpleMap:cache:10min", key = "'childTagIdList_tagId_'+#tagId")
    public List<Integer> childTagIdList(int tagId) {
        return self.getBaseMapper().allSubTags(tagId);
    }

    /**
     * 找到 tagId 下的所有子 tag 的信息
     *
     * @param tagId tag id
     * @param start 分页起始 index
     * @param len   分页页大小
     * @return 所有子 tag 的信息（该页面的）
     */
    public List<Tag> childTagList(int tagId, int start, int len) {
        tagNameQueryWrapper.clear();
        tagNameQueryWrapper.in(SqlTableConstant.TagColName.ID, self.childTagIdList(tagId));

        List<Tag> tags = new ArrayList<>(len);
        do {
            tags.addAll(self.getBaseMapper().selectList(tagNameQueryWrapper));
            start += DefaultValueFactory.PAGE_UNIT_LENGTH;
        } while ((len -= DefaultValueFactory.PAGE_UNIT_LENGTH) >= 0);

        return tags;
    }

    private QueryWrapper<TagPath> tagPathQueryWrapper;

    {
        tagPathQueryWrapper = new QueryWrapper<>();
    }

    /**
     * 从每个根节点到 tag 的路径，以tag为root的标签树
     *
     * @param target 找到以 target 为核心的 树结构
     * @return 所有 path 、 每个涉及到的 tag 的信息
     */
    public Map<String,Object> tagTree(int target) {
        // 以 tag 为 root 的标签树
        tagPathQueryWrapper.clear();
        tagPathQueryWrapper.eq(SqlTableConstant.TagPathColName.ANCESTOR,target);
        List<TagPath> childrenOfTag = self.tagPathMapper.selectList(tagPathQueryWrapper);
        List<TagPath> tagPathList = new ArrayList<>(childrenOfTag);
        List<Integer> idList = childrenOfTag.stream().map(TagPath::getAncestor).collect(Collectors.toList());

        // 从每个根节点到 tag 的路径
        tagPathQueryWrapper.clear();
        tagPathQueryWrapper.eq(SqlTableConstant.TagPathColName.DESCENDANT,target);
        List<TagPath> parentOfTag = self.tagPathMapper.selectList(tagPathQueryWrapper);
        tagPathList.addAll(parentOfTag);
        idList.addAll(parentOfTag.stream().map(TagPath::getDescendant).collect(Collectors.toList()));

        // 找到对应的 Tag 的信息
        List<Tag> tagList = self.getBaseMapper().selectBatchIds(idList);

        return new HashMap<String,Object>(TagServiceImpl.DEFAULT_TAG_TREE_RESULT_MAP_SIZE){{
            put("paths",tagPathList);
            put("tagInfo",tagList);
        }};
    }

    /**
     * 默认 tagTree 结果的 result 的 map 的 size ， 即上面几行的那个 map
     */
    private static final int DEFAULT_TAG_TREE_RESULT_MAP_SIZE = 2;

    /* ------------- tag with its article group ----------- */

    /**
     * 检查 articlesBelongToTags controller 接收到的 start 是否越界
     * id 和 name 可能有一个为空
     *
     * @param id    tag id
     * @param name  tag name
     * @param start 分页 start index
     * @return 越界与否
     */
    public boolean checkArticlesBelongToTagsParamBound(int id, String name, int start) {
        if (id != -1 && name != null) {
            id = self.selectTag(name).getId();
        }
        return start >= articleTagService.countArticleBelongToTag(id);
    }

    /**
     * 根据 tag name 来查找它的文章列表
     *
     * @param name  tag 名称
     * @param start 分页起始
     * @param len   分页大小
     * @return 文章列表
     */
    public ArticleListBelongToTagVO articleBelongsToTag(String name, int start, int len) {
        Tag target = self.selectTag(name);
        return self.articleBelongsToTag(target.getId(), start, len);
    }

    public ArticleListBelongToTagVO articleBelongsToTag(int id, int start, int len) {
        ArticleListBelongToTagVO result = new ArticleListBelongToTagVO();

        // 额外结构信息: 初始页返回size
        Map<String, Object> extra = new HashMap<>(DefaultValueFactory.DEFAULT_ARTICLE_EXTRA_INFO_MAP_SIZE);
        if (start == 0) {
            extra.put("size", articleTagService.countArticleBelongToTag(id));
        }
        result.setExtra(extra);

        List<Article> articles = new ArrayList<>(len);
        do {
            articles.addAll(self.articleBelongsToTagUnitList(id, start));
            start += DefaultValueFactory.PAGE_UNIT_LENGTH;
        } while ((len -= DefaultValueFactory.PAGE_UNIT_LENGTH) >= 0);
        result.setArticleList(articles);

        return result;
    }

    /**
     * 分页缓存
     *
     * @param tagId tag id
     * @param start 分页起始 index
     * @return 该页面文章列表
     */
    @Cacheable(value = "articleBelongsToTag:cache:10min", key = "'articleBelongsToTag_'+#tagId+'_'+#start")
    public List<Article> articleBelongsToTagUnitList(int tagId, int start) {
        Page<ArticleTag> page = new Page<>(start, DefaultValueFactory.PAGE_UNIT_LENGTH, false);
        IPage<Article> articlePage = articleTagService.getBaseMapper()
                .articleBelongsToTag(page, self.childTagIdList(tagId));
        return articlePage.getRecords();
    }

    /**
     * 初次进入标签页
     *
     * @return topLevel 为 0 的所有tag，第一个 tag 的文章列表，以及第一个 tag 的子tag
     */
    public Object tagPageFirstIn() {
        TagPageFirstInVO tagPageFirstInVO = new TagPageFirstInVO();

        // topLevel 为 0 的所有tag，按照 name 排序
        tagNameQueryWrapper.clear();
        tagNameQueryWrapper.eq(SqlTableConstant.TagColName.TOP_LEVEL, 0);
        tagNameQueryWrapper.orderBy(true, true, SqlTableConstant.TagColName.NAME);
        List<Tag> topTagList = self.getBaseMapper().selectList(tagNameQueryWrapper);
        tagPageFirstInVO.setTopTagList(topTagList);

        // 找到第一个有文章的 tag
        Tag first = null;
        int index = 0;
        while (first == null && index < topTagList.size()) {
            first = topTagList.get(index);
            index++;
        }
        if (first != null) {
            // 找到第一个 tag 的文章列表
            ArticleListBelongToTagVO articleListBelongToTagVO =
                    self.articleBelongsToTag(first.getId(), 0, DefaultValueFactory.PAGE_UNIT_LENGTH);
            tagPageFirstInVO.setArticleListBelongToFirstTag(articleListBelongToTagVO);

            // 找到第一个 tag 的子tag
            tagPageFirstInVO.setFirstChildTagList(self.childTagList(first.getId(), 0,
                    DefaultValueFactory.PAGE_UNIT_LENGTH));
        }

        return tagPageFirstInVO;
    }

    public boolean checkChildTagOfParentTagParamBound(int parentTagId, int start) {
        return start > self.childTagIdList(parentTagId).size();
    }
}
