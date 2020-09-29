package com.highestpeak.springblog.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.highestpeak.springblog.constant.SqlTableConstant;
import com.highestpeak.springblog.constant.enumerate.ModifyTypeEnum;
import com.highestpeak.springblog.mapper.ArticleLocationMapper;
import com.highestpeak.springblog.model.bo.ArticleBO;
import com.highestpeak.springblog.model.dto.ArticleModifyDTO;
import com.highestpeak.springblog.model.entity.ArticleLocation;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author highestpeak
 */
@Service("articleLocation")
public class ArticleLocationServiceImpl extends ServiceImpl<ArticleLocationMapper, ArticleLocation> implements IService<ArticleLocation> {

    private final ArticleLocationServiceImpl self;
    private final UtilService utilService;

    private QueryWrapper<ArticleLocation> locationOfArticleQueryWrapper;

    {
        locationOfArticleQueryWrapper = new QueryWrapper<>();
    }

    public ArticleLocationServiceImpl(
            @Lazy ArticleLocationServiceImpl self, UtilService utilService) {
        this.self = self;
        this.utilService = utilService;
    }

    public List<ArticleLocation> articlesLocationList(int article) {
        locationOfArticleQueryWrapper.clear();
        locationOfArticleQueryWrapper.eq(SqlTableConstant.ArticleLocationColName.ARTICLE, article);
        return self.getBaseMapper().selectList(locationOfArticleQueryWrapper);
    }

    public List<ArticleLocation> articlesLocationList(List<Integer> articleIdList) {
        locationOfArticleQueryWrapper.clear();
        locationOfArticleQueryWrapper.in(SqlTableConstant.ArticleLocationColName.ARTICLE, articleIdList);
        locationOfArticleQueryWrapper.orderBy(true, true, SqlTableConstant.ArticleLocationColName.ARTICLE);
        return self.getBaseMapper().selectList(locationOfArticleQueryWrapper);
    }

    /**
     * todo: 返回解析方法
     *
     * @return parse 方法
     */
    public Object locationParse() {
        return null;
    }

    public Stream<ArticleLocation> buildArticleLocationList(ArticleBO article) {
        return null;
    }

    private ArticleLocation.ArticleLocationBuilder articleLocationBuilder;

    {
        articleLocationBuilder = ArticleLocation.builder();
    }

    public void modifyArticleLocation(List<ArticleModifyDTO> articleModifyDTOList,
                                      Map<ModifyTypeEnum, List<String>> errorMsgBuilder) {
        if (articleModifyDTOList == null) {
            return;
        }
        Map<Integer, List<ArticleLocation>> articleLocationToUpdate = articleModifyDTOList.stream()
                .map(ArticleModifyDTO::getArticle)
                .flatMap(articleBO -> articleBO.getArticleLocations().stream().peek(articleLocation -> {
                    articleLocation.setArticle(articleBO.getArticle().getId());
                }))
                .collect(Collectors.groupingBy(ArticleLocation::getArticle));

        StringBuilder delFailMsgBuilder = new StringBuilder();
        StringBuilder insertFailMsgBuilder = new StringBuilder();
        articleLocationToUpdate.forEach((article, newArticleLocations) -> {
            List<ArticleLocation> oldArticleLocations = self.articlesLocationList(article);
            // 删除旧的,插入新的
            utilService.delAndInsertRow(
                    oldArticleLocations, newArticleLocations, self, ArticleLocation::getId,
                    article, delFailMsgBuilder, insertFailMsgBuilder
            );
        });

        delFailMsgBuilder.deleteCharAt(delFailMsgBuilder.length() - 1);
        delFailMsgBuilder.insert(0, "删除旧ArticleLocation失败列表:");
        insertFailMsgBuilder.deleteCharAt(insertFailMsgBuilder.length() - 1);
        insertFailMsgBuilder.insert(0, "插入新ArticleLocation失败列表:");
        List<String> errorList = errorMsgBuilder.get(ModifyTypeEnum.MODIFY);
        errorList.add(delFailMsgBuilder.toString());
        errorList.add(insertFailMsgBuilder.toString());
    }
}
