package com.highestpeak.springblog.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.highestpeak.springblog.constant.DefaultValueFactory;
import com.highestpeak.springblog.constant.SqlTableConstant;
import com.highestpeak.springblog.constant.enumerate.ArticlesSortEnum;
import com.highestpeak.springblog.constant.enumerate.ModifyTypeEnum;
import com.highestpeak.springblog.mapper.ArticleMapper;
import com.highestpeak.springblog.model.bo.ArticleBO;
import com.highestpeak.springblog.model.dto.ArticleModifyDTO;
import com.highestpeak.springblog.model.entity.Article;
import com.highestpeak.springblog.model.entity.ArticleLocation;
import com.highestpeak.springblog.model.entity.ArticleTag;
import com.highestpeak.springblog.model.entity.Tag;
import com.highestpeak.springblog.model.vo.ArticleListVO;
import com.highestpeak.springblog.util.BinarySearches;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author highestpeak
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements IService<Article> {

    private final ArticleServiceImpl self;
    private final ArticleTagServiceImpl articleTagService;
    private final TagServiceImpl tagService;
    private final ArticleLocationServiceImpl articleLocationService;
    private final UserService userService;

    @Autowired
    public ArticleServiceImpl(ArticleTagServiceImpl articleTagService,
                              TagServiceImpl tagService, ArticleLocationServiceImpl articleLocationService,
                              @Lazy ArticleServiceImpl self, UserService userService) {
        this.articleTagService = articleTagService;
        this.tagService = tagService;
        this.articleLocationService = articleLocationService;

        // 加上lazy解决循环依赖
        this.self = self;
        this.userService = userService;
    }


    /* ---------------- article select group -------------- */


    /**
     * 文章总数，缓存1h
     *
     * @return 最新的size，文章总数
     */
    @Cacheable(value = "simpleValue:cache:1h", key = "'articleListSize'")
    public int articleListSize() {
        return self.count();
    }

    /**
     * 当start为0,即第一页时，传回size
     *
     * @param start 分页起始
     * @param extra 额外信息map
     * @return 传入或新建的extra
     */
    private Map<String, Object> listStartProcess(int start, Map<String, Object> extra) {
        if (extra == null) {
            extra = new HashMap<>(DefaultValueFactory.DEFAULT_ARTICLE_EXTRA_INFO_MAP_SIZE);
        }
        if (start == 0) {
            extra.put("size", self.articleListSize());
        }
        return extra;
    }

    /**
     * 请求分页的页面数据
     *
     * @param start  页面起始文章的index，需要为 PAGE_UNIT_LENGTH 的倍数，已经在Controller校验
     * @param len    页面大小，需要为 PAGE_UNIT_LENGTH 的倍数，已经在Controller校验
     * @param sortBy 排序依据
     * @param asc    是否正序
     * @return 页面的文章数据，包括文章列表、Tag信息、额外控制信息extra(第一页时返回分页大小)
     */
    public ArticleListVO articleList(int start, int len, ArticlesSortEnum sortBy, boolean asc) {
        ArticleListVO result = new ArticleListVO();

        // 额外结构信息: 初始页返回size
        Map<String, Object> extra = new HashMap<>(DefaultValueFactory.DEFAULT_ARTICLE_EXTRA_INFO_MAP_SIZE);
        extra = listStartProcess(start, extra);
        result.setExtra(extra);

        // 文章列表、Tag列表
        // 每篇文章记录所持Tag的id、Tag列表为出现的Tag的详细的信息
        List<ArticleBO> articleBOList = new ArrayList<>();
        List<Tag> tagList = new ArrayList<>();
        ArticlesSortEnum tmp = ArticlesSortEnum.WRITE_TIME;
        if (self.sortByTableColName.containsKey(sortBy)) {
            tmp = sortBy;
        }
        // 以最小页大小为单位，循环读取数据，最终拼接到一起
        do {
            articleBOList.addAll(articleUnitList(start, tmp, asc, tagList));
            start += DefaultValueFactory.PAGE_UNIT_LENGTH;
        } while ((len -= DefaultValueFactory.PAGE_UNIT_LENGTH) >= 0);
        if (self.sortByCalValue.containsKey(sortBy)) {
            sortByCalValue.get(sortBy).sort(articleBOList);
        }
        result.setArticleList(articleBOList);
        extra.put("tags", tagList);

        return result;
    }

    /**
     * 直接就能在数据库排序
     */
    public Map<ArticlesSortEnum, String> sortByTableColName = new HashMap<ArticlesSortEnum, String>() {{
        put(ArticlesSortEnum.WRITE_TIME, SqlTableConstant.ArticleColName.WRITE_TIME);
        put(ArticlesSortEnum.UPDATE_TIME, SqlTableConstant.ArticleColName.UPDATE_TIME);
    }};

    /**
     * 对文章排序
     */
    private interface CalculateAndSortArticle {
        /**
         * 对文章排序
         *
         * @param articleList 待排序列表
         */
        void sort(List<ArticleBO> articleList);
    }

    /**
     * 必须经过计算才能排序
     */
    private Map<ArticlesSortEnum, CalculateAndSortArticle> sortByCalValue =
            new HashMap<ArticlesSortEnum, CalculateAndSortArticle>() {{
                // todo: 通过 star、read count、comment count 对文章排序
                put(ArticlesSortEnum.STAR, (articleList) -> {
                });
                put(ArticlesSortEnum.READ_COUNT, (articleList) -> {
                });
                put(ArticlesSortEnum.COMMENT_COUNT, (articleList) -> {
                });
            }};

    /**
     * 请求按照 PAGE_UNIT_LENGTH 的单位长度的页面
     * 所以参数需要满足:
     * start 是 PAGE_UNIT_LENGTH 的倍数（内部+外部约束）
     * 缓存策略：
     * 1. 每次缓存 [ start , start+PAGE_UNIT_LENGTH ] 这个区间
     * 2. 针对index前 200 的文章进行 1h 缓存（因为访问到的可能性大），在此 index 之后的进行 5min 缓存
     * 3. 针对每个 排序依据 即 ArticlesSortEnum 都有一个2的排序
     * 4. 只针对正序排序
     *
     * @param start   单位页首个文章 index
     * @param sortBy  排序依据
     * @param asc     是否正序
     * @param tagList 已有标签列表（主要针对 articleList 方法）
     * @return 页面文章数据，标签数据在 tagList 中已经置入
     */
    @Caching(cacheable = {
            @Cacheable(value = "articleListIndexBefore200:cache:1h", key = "#start+'_'+#sortBy",
                    condition = "#start<=200 and #asc=true"),
            @Cacheable(value = "articleListIndexAfter200:cache:10min", key = "#start+'_'+#sortBy",
                    condition = "#start>200 and #asc=true")
    })
    public List<ArticleBO> articleUnitList(int start, ArticlesSortEnum sortBy, boolean asc, List<Tag> tagList) {
        // 找到分页文章列表 articles
        // 获得 idList 方便后续一次性查找 Tag 和 Location
        QueryWrapper<Article> articleQueryWrapper = new QueryWrapper<>();
        articleQueryWrapper.orderBy(true, asc, self.sortByTableColName.get(sortBy));
        Page<Article> page = new Page<>(start, DefaultValueFactory.PAGE_UNIT_LENGTH, false);
        List<Article> articles = self.baseMapper.selectPage(page, articleQueryWrapper).getRecords();
        List<Integer> idList = articles.stream().map(Article::getId).collect(Collectors.toList());

        // 找到所有文章的 Location
        List<ArticleLocation> articlesLocations = articleLocationService.articlesLocationList(idList);
        Comparator<ArticleLocation> locationSearchComparator = Comparator.comparingInt(ArticleLocation::getArticle);

        // 找到所有文章的 Tag
        List<ArticleTag> articlesTags = articleTagService.articlesTagList(idList);
        Comparator<ArticleTag> tagSearchComparator = Comparator.comparingInt(ArticleTag::getArticle);

        // 构造 ArticleBO 结果的临时控制结构
        // 在上面找到的两个列表进行有重复值的二分查找,找到所有重复值
        Set<Integer> tagIdSet = new HashSet<>(articles.size());
        ArticleBO.ArticleBOBuilder articleBuilder = ArticleBO.builder();
        ArticleLocation searchTempleLocation = new ArticleLocation();
        ArticleTag searchTempleTag = new ArticleTag();
        // 构造结果
        List<ArticleBO> articleList = articles.stream().map(article -> {
            searchTempleLocation.setArticle(article.getId());
            List<ArticleLocation> currLocations =
                    BinarySearches.binarySearchDuplicate(
                            articlesLocations, searchTempleLocation, locationSearchComparator
                    );

            // 查找tag信息，只保存id，减少数据量
            searchTempleTag.setArticle(article.getId());
            List<ArticleTag> currTags =
                    BinarySearches.binarySearchDuplicate(
                            articlesTags, searchTempleTag, tagSearchComparator
                    );
            List<Integer> tagIdList = currTags.stream().map(ArticleTag::getTag).collect(Collectors.toList());
            tagIdSet.addAll(tagIdList);

            return articleBuilder.article(article)
                    .articleLocations(currLocations)
                    .tagIdList(tagIdList)
                    // 计算 star
                    .stars(userService.countStars(article))
                    .build();
        }).collect(Collectors.toList());

        // 减少一次数据库查询
        if (!tagIdSet.isEmpty()) {
            // 记录下所有tag
            tagList.addAll(tagService.selectTagList(new ArrayList<>(tagIdSet)));
        }

        return articleList;
    }

    /* ---------------- article modify group -------------- */

    private void insertArticle(List<ArticleModifyDTO> waitToInsert) {
        List<ArticleBO> newArticle = waitToInsert.stream()
                .map(ArticleModifyDTO::getArticle).filter(Objects::nonNull)
                .collect(Collectors.toList());

        List<Article> articleToInsert = newArticle.stream().map(ArticleBO::getArticle).collect(Collectors.toList());
        boolean isArticleInsertSuccess = self.saveBatch(articleToInsert);
        // 只有插入后才有article 的 id,插入失败则终止过程
        if (!isArticleInsertSuccess) {
            errorMsgBuilder.get(ModifyTypeEnum.ADD).add("插入 entity Article 失败");
            return;
        }

        List<ArticleTag> articleTagToInsert = newArticle.stream()
                .flatMap(articleTagService::buildArticleTagList)
                .collect(Collectors.toList());
        boolean isArticleTagInsertSuccess = articleTagService.saveBatch(articleTagToInsert);

        List<ArticleLocation> articleLocationToInsert = newArticle.stream()
                .flatMap(articleLocationService::buildArticleLocationList)
                .collect(Collectors.toList());
        boolean isArticleLocationInsertSuccess = articleLocationService.saveBatch(articleLocationToInsert);

        StringBuilder builder = new StringBuilder();
        if (!isArticleTagInsertSuccess) {
            builder.append("插入 entity ArticleTag 失败");
        }
        if (!isArticleLocationInsertSuccess) {
            builder.append("插入 entity ArticleLocation 失败");
        }
        errorMsgBuilder.get(ModifyTypeEnum.ADD).add(builder.toString());
    }

    private void delArticle(List<ArticleModifyDTO> waitToDel) {
        // 只能按照id删除
        List<Integer> delArticle = waitToDel.stream()
                .map(ArticleModifyDTO::getArticle).filter(Objects::nonNull)
                .map(ArticleBO::getArticle)
                .map(Article::getId)
                .collect(Collectors.toList());
        boolean isRemoveSuccess = self.removeByIds(delArticle);
        if (!isRemoveSuccess) {
            errorMsgBuilder.get(ModifyTypeEnum.DEL).add("删除 entity Article 失败");
        }
    }

    private void modifyArticle(List<ArticleModifyDTO> articleModifyDTOList) {
        if (articleModifyDTOList == null) {
            return;
        }
        List<Article> articleToUpdate = articleModifyDTOList.stream()
                .map(ArticleModifyDTO::getArticle)
                .map(ArticleBO::getArticle)
                .collect(Collectors.toList());
        self.updateBatchById(articleToUpdate);
    }

    private Map<ModifyTypeEnum, List<String>> errorMsgBuilder;

    {
        errorMsgBuilder = new HashMap<>(ModifyTypeEnum.values().length);
        errorMsgBuilder.put(ModifyTypeEnum.ADD, new ArrayList<>());
        errorMsgBuilder.put(ModifyTypeEnum.DEL, new ArrayList<>());
        errorMsgBuilder.put(ModifyTypeEnum.MODIFY, new ArrayList<>());
    }

    private void clearErrorMsgBuilder() {
        errorMsgBuilder.values().forEach(List::clear);
    }

    /**
     * todo: 日志，记录这样的修改记录，以便于某一天查看之前写过什么文章
     * 可能在modify里有新的tag或者location
     * 批量修改
     * 这里只按照tag id插入: 如果有新的tag，则需要提前建立新的tag
     * <p>
     * 先modify后del是因为可以在一次提交中，解除article的其他外键关联，然后删除article
     * <p>
     * 删除article只能在与article相关联的信息都被删除后才能删除
     * 这样是因为article与多个站点的数据相关联着，所以不能随便删除
     *
     * @param modifyMap modifies
     * @return modify status
     */
    public Object updateArticle(Map<ModifyTypeEnum, List<ArticleModifyDTO>> modifyMap) {
        // add article && add tag-article && add tag
        // add article && add tag-article
        // ---------------------------------------------
        // del article && del tag-article
        // del article && del tag-article && del tag
        // ---------------------------------------------
        // modify article && add tag-article
        // modify article && del tag-article
        // modify article && add tag-article
        // modify article && add tag-article && add tag
        // modify article && del tag-article
        // modify article && del tag-article && del tag
        // =============================================
        // 把article和location对应又出来一堆
        // 处理顺序
        // add article and add tag/location -> add tag-article/article-location
        // del tag-article/article-location -> del article and del tag/location
        // modify article/tag
        // modify tag-article/article-location

        self.clearErrorMsgBuilder();
        // todo: ofNullable 这样用到底比用if null判断哪个快呢
        // add
        Optional.ofNullable(modifyMap.get(ModifyTypeEnum.ADD)).ifPresent(self::insertArticle);

        // modify
        Map<ArticleModifyDTO.ArticleModifyEnum, List<ArticleModifyDTO>> modifyGroup =
                modifyMap.get(ModifyTypeEnum.MODIFY).stream()
                        .collect(Collectors.groupingBy(ArticleModifyDTO::getArticleModifyCol));
        self.modifyArticle(modifyGroup.get(ArticleModifyDTO.ArticleModifyEnum.ARTICLE));
        // 更新的话传过来的是tag的id，注意！
        articleTagService.modifyArticleTag(modifyGroup.get(ArticleModifyDTO.ArticleModifyEnum.TAGS), errorMsgBuilder);
        articleLocationService.modifyArticleLocation(
                modifyGroup.get(ArticleModifyDTO.ArticleModifyEnum.LOCATION),
                errorMsgBuilder
        );
        userService.modifyArticleStar(modifyGroup.get(ArticleModifyDTO.ArticleModifyEnum.STAR));
        // delete
        Optional.ofNullable(modifyMap.get(ModifyTypeEnum.DEL)).ifPresent(self::delArticle);

        return errorMsgBuilder;
    }

}
