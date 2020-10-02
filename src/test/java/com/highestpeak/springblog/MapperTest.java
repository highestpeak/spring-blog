package com.highestpeak.springblog;

import com.highestpeak.springblog.model.bo.ArticleBO;
import com.highestpeak.springblog.model.entity.*;
import com.highestpeak.springblog.service.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootTest
class MapperTest {

    private LocalDateTime now = LocalDateTime.now();

    @Autowired
    private ArticleServiceImpl articleService;

    /*
    * todo 这几个简单的insert如何用设计模式抽象一个模板方法？
     */
    Object[][] buildArticleTestList(){
        return new Object[][]{
                {"Java高级-JVM-2.21", "update2",2},
                {"Java高级-JVM-3.31", "update2",3},
                {"Java高级-JVM-4.41", "update2",4},
                {"repoTestReadme-5.51", "update2",5},
        };
    }

    @Test
    void testArticleInsert() {
        System.out.println("------article mapper insert test------");
        Article.ArticleBuilder articleBuilder = Article.builder();
        String[][] articleTestList = (String[][]) buildArticleTestList();
        List<Article> articles = new ArrayList<>();
        Stream.of(articleTestList).forEach(article -> articles.add(
                articleBuilder.title(article[0]).description(article[1])
                        .createTime(now).updateTime(now)
                        .build()
        ));
        articleService.saveBatch(articles);
//        List<Article> articleList=articleService.getBaseMapper().selectList(null);
//        Assertions.assertEquals(articleTestList.length,articleList.size());

        System.out.println("------article mapper insert and get id test------");
        for (int i = 0; i < articles.size(); i++) {
            System.out.println(articleTestList[i][0] + ":" + articles.get(i).getId());
        }
    }

    /**
     * 说明可以如果插入前没有id，插入后是可以从原对象取到id的
     */
    @Test
    void testInsertAndIdChange(){
        System.out.println("------article mapper insert test------");
        Article.ArticleBuilder articleBuilder = Article.builder();
        String[][] articleTestList = (String[][]) buildArticleTestList();
        List<ArticleBO> newArticle = new ArrayList<>();
        ArticleBO.ArticleBOBuilder articleBOBuilder = ArticleBO.builder();
        Stream.of(articleTestList).forEach(article -> newArticle.add(
                articleBOBuilder.article(
                        articleBuilder.title(article[0]).description(article[1])
                        .createTime(now).updateTime(now)
                        .build()
                ).build()
        ));

        List<Article> articleToInsert = newArticle.stream().map(ArticleBO::getArticle).collect(Collectors.toList());
        articleService.saveBatch(articleToInsert);

        System.out.println("------article mapper insert and get id test------");
        for (int i = 0; i < newArticle.size(); i++) {
            System.out.println(articleTestList[i][0] + ":" + newArticle.get(i).getArticle().getId());
        }
    }

    /**
     * 说明如果字段为null则不会清空数据库字段
     */
    @Test
    void testUpdateNullField(){
        Object[][] articleTestList = buildArticleTestList();
        List<Article> articles = new ArrayList<>();
        Article.ArticleBuilder articleBuilder = Article.builder();
        Stream.of(articleTestList).forEach(article -> articles.add(
                articleBuilder.id((Integer) article[2])
                        //.title((String) article[0])
                        .description((String) article[1])
                        .build()
        ));
        articleService.updateBatchById(articles);
    }

    @Autowired
    private RepoServiceImpl repoService;

    @Test
    void testRepoInsert() {
        System.out.println("------repo mapper insert test------");
        Repo.RepoBuilder repoBuilder= Repo.builder();
        ArrayList<Repo> repos = new ArrayList<>();
        repos.add(repoBuilder.repoUrl("github/").descArticleId(4).createTime(now).build());
        repoService.saveBatch(repos);
        List<Repo> repoList = repoService.getBaseMapper().selectList(null);
        Assertions.assertEquals(repos.size(),repoList.size());
    }

    @Autowired
    private TagServiceImpl tagService;

    @Test
    void testTagInsert() {
        System.out.println("------tag mapper insert test------");
        Tag.TagBuilder tagBuilder = Tag.builder();
        Object[][] tagTestList = new Object[][]{
                {"JVM","",2},
                {"JAVA","",1},
                {"并发","",1},
                {"多线程","",1},
        };
        List<Tag> tags = new ArrayList<>();
        Stream.of(tagTestList).forEach(tag-> tags.add(
                tagBuilder.name((String) tag[0]).description((String) tag[1]).topLevel((Integer) tag[2]).createTime(now).build()
        ));
        tagService.saveBatch(tags);
        List<Tag> tagList = tagService.getBaseMapper().selectList(null);
        Assertions.assertEquals(tags.size(),tagList.size());
    }

    @Autowired
    private ArticleTagServiceImpl articleTagService;

    @Test
    void testArticleTagInsert(){
        System.out.println("------article_tag mapper insert test------");
        ArticleTag.ArticleTagBuilder articleTagBuilder = ArticleTag.builder();
        int[][] articleTagTestList=new int[][]{
                {1,1},{1,2},{1,3},
                {4,2}
        };
        List<ArticleTag> articleTags=new ArrayList<>();
        Stream.of(articleTagTestList).forEach(articleTag->articleTags.add(
                articleTagBuilder.article(articleTag[0]).tag(articleTag[1]).build()
        ));
        articleTagService.saveBatch(articleTags);
        List<ArticleTag> articleTagList = articleTagService.getBaseMapper().selectList(null);
        Assertions.assertEquals(articleTagTestList.length,articleTagList.size());
    }

    @Autowired
    private ArticleLocationServiceImpl articleLocationService;

    @Test
    void testArticleLocationInsert(){
        System.out.println("------article_location mapper insert test------");
        ArticleLocation.ArticleLocationBuilder articleLocationBuilder = ArticleLocation.builder();
        Object[][] articleLocationTestList=new Object[][]{
                {5,"GITHUB","BlogArticle"},
                {6,"GITLAB","BlogArticle"}
        };
        List<ArticleLocation> articleLocations=new ArrayList<>();
        Stream.of(articleLocationTestList).forEach(articleLocation->articleLocations.add(
                articleLocationBuilder.article((Integer) articleLocation[0])
                        .location((String) articleLocation[1]).locationValue((String) articleLocation[2])
                        .createTime(now).updateTime(now)
                        .build()
        ));
        try {
            articleLocationService.saveBatch(articleLocations);
            List<ArticleLocation> articleLocationList = articleLocationService.getBaseMapper().selectList(null);
            Assertions.assertEquals(articleLocationTestList.length,articleLocationList.size());
        }catch (DataIntegrityViolationException e){
            System.out.println("不满足完整性约束");
        }
    }
}
