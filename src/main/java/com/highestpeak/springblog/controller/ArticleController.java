package com.highestpeak.springblog.controller;

import com.highestpeak.springblog.constant.DefaultValueFactory;
import com.highestpeak.springblog.constant.enumerate.ArticlesSortEnum;
import com.highestpeak.springblog.constant.enumerate.ModifyTypeEnum;
import com.highestpeak.springblog.constant.enumerate.RoleEnum;
import com.highestpeak.springblog.model.dto.ArticleModifyDTO;
import com.highestpeak.springblog.service.ArticleServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author highestpeak
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/article")
public class ArticleController {

    /*
    todo: 如果校验可以放在controller就不要放在service，因为这样多了一层函数调用开销？
     */

    private final ArticleServiceImpl articleService;

    @Autowired
    public ArticleController(ArticleServiceImpl articleService) {
        this.articleService = articleService;
    }

    /**
     * 首页返回文章列表，每一项包括了文章的简要信息，文章内容地址等
     * 只根据一列排序
     * start 和 len 是 PAGE_UNIT_LENGTH 的倍数
     * len 应该是某个倍数，然后service的查找只会循环最小粒度的page
     *
     * @param start  页面起始文章的index，需要为 PAGE_UNIT_LENGTH 的倍数
     * @param len    页面大小，需要为 PAGE_UNIT_LENGTH 的倍数
     * @param sortBy 排序依据
     * @param asc    是否正序
     * @return 页面的文章数据，包括文章列表、Tag信息、额外控制信息extra(第一页时返回分页大小)
     */
    @GetMapping("/list")
    public Object articleList(
            @RequestParam("start") int start, @RequestParam("len") int len,
            @RequestParam("sort-by") ArticlesSortEnum sortBy, @RequestParam("asc") boolean asc
    ) {
        if (start % DefaultValueFactory.PAGE_UNIT_LENGTH != 0 ||
                len % DefaultValueFactory.PAGE_UNIT_LENGTH != 0
        ) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"code\": 400, \"msg\": \"参数不正确!\"}");
        }
        if (len > DefaultValueFactory.PAGE_MAX_LENGTH || start >= articleService.articleListSize()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"code\": 400, \"msg\": \"overflow!\"}");
        }
        return articleService.articleList(start, len, sortBy, asc);
    }

    /**
     * todo: spring security
     *
     * @param role      修改人角色
     * @param modifyMap 每一项为修改后文章内容
     * @return 修改成功和失败的列表
     */
    @PostMapping("/list")
    public Object updateArticles(
            @RequestParam("role") RoleEnum role,
            @RequestParam("salt") String adminSalt,
            @RequestParam("data") Map<ModifyTypeEnum, List<ArticleModifyDTO>> modifyMap
    ) {
        if (role.equals(RoleEnum.AUTHOR) && DefaultValueFactory.ADMIN_SALT.equals(adminSalt)) {
            return articleService.updateArticle(modifyMap);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("{\"code\": 401, \"msg\": \"未授权!\"}");
    }

    @PostMapping("/star")
    public Object starArticle(
            @RequestParam("article") int article,
            @RequestParam("user") int user,
            @RequestParam("star") int star
    ) {
        return null;
    }
}
