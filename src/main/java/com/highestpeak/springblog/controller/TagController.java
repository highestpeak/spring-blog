package com.highestpeak.springblog.controller;

import com.highestpeak.springblog.constant.DefaultValueFactory;
import com.highestpeak.springblog.constant.enumerate.RoleEnum;
import com.highestpeak.springblog.model.dto.TagModifyDTO;
import com.highestpeak.springblog.service.TagServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author highestpeak
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/tag")
public class TagController {

    private final TagServiceImpl tagService;

    public TagController(TagServiceImpl tagService) {
        this.tagService = tagService;
    }

    /*
    用例
    1. 初次进入标签页，查找 topLevel 为 0 的所有tag，
        并返回第一个 tag 的文章列表，以及第一个 tag 的子tag
    2. 找到某个 tag 的所有文章
        这些文章可能是该 tag 的子 tag 的文章
        2.1 根据子 tag 应该也能筛选文章列表的文章
            但这样每确定一个 tag 组合就要请求一次，所以根据子 tag 来缓存？
            todo：目前只根据每点击一个新 tag 就向后台请求一次
    3. 找到某个 tag 的所有子 tag
        包括下一级、下二级、下三级、...，按照 级别、热度等综合排序(todo: 热度涉及到 user 多站点)
     */

    /**
     * 一个 tag 的所有文章（包括子tag）
     * <p>
     * 当一个 tag 的文章过多时就可以通过分页来显示
     * 分页逻辑和文章列表的分页逻辑相同，可以查看相应的文章列表的 doc
     * <p>
     * 不返回文章的所有 tag 和 location 信息，是因为该tag下的文章并不会每个文章都被访问到
     * 如此可以减少数据库查询
     * <p>
     * tag tagId 和 tagName 必须发送一个
     * todo：新功能，根据tag的正则来选择文章列表？否则因为id前端已经知道，这样就没有意义了
     *
     * @param tagId   tag tagId
     * @param tagName tag tagName
     * @param start   分页起始 index
     * @param len     分页大小
     * @return 文章列表，每篇文章不包括它的 tag 信息
     */
    @GetMapping("/articles")
    public Object articlesBelongToTags(
            @RequestParam(value = "tagId", defaultValue = "-1") int tagId,
            @RequestParam("tagName") String tagName,
            @RequestParam(value = "start", required = false) int start,
            @RequestParam(value = "len", required = false) int len
    ) {
        boolean pageUnitLengthCheck = start % DefaultValueFactory.PAGE_UNIT_LENGTH != 0 ||
                len % DefaultValueFactory.PAGE_UNIT_LENGTH != 0;
        boolean idNameHaveNone = tagId == -1 && tagName == null;
        if (pageUnitLengthCheck || idNameHaveNone) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"code\": 400, \"msg\": \"参数不正确!\"}");
        }

        // 边界检查
        if (len > DefaultValueFactory.PAGE_MAX_LENGTH ||
                tagService.checkArticlesBelongToTagsParamBound(tagId, tagName, start)
        ) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"code\": 400, \"msg\": \"overflow!\"}");
        }

        if (tagId == -1) {
            return tagService.articleBelongsToTag(tagId, start, len);
        }
        return tagService.articleBelongsToTag(tagName, start, len);
    }

    /**
     * 初次进入标签页，查找 topLevel 为 0 的所有tag，
     * 并返回第一个 tag 的文章列表，以及第一个 tag 的子tag
     */
    @GetMapping("/list/startPage")
    public Object tagPageFirstIn() {
        return tagService.tagPageFirstIn();
    }

    /**
     * 找到某个 tag 的所有子 tag,
     * 包括下一级、下二级、下三级、...，按照 级别、热度等综合排序
     *
     * @param parentTagId 哪个 tag 的子 tag
     * @param start       分页起始 index
     * @param len         页面大小
     * @return tag 信息列表
     */
    @GetMapping("/list")
    public Object childTagOfParentTag(
            @RequestParam(value = "parentTagId", required = false) int parentTagId,
            @RequestParam(value = "start", required = false) int start,
            @RequestParam(value = "len", required = false) int len
    ) {
        boolean pageUnitLengthCheck = start % DefaultValueFactory.PAGE_UNIT_LENGTH != 0 ||
                len % DefaultValueFactory.PAGE_UNIT_LENGTH != 0;
        if (pageUnitLengthCheck) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"code\": 400, \"msg\": \"参数不正确!\"}");
        }

        // 边界检查
        if (len > DefaultValueFactory.PAGE_MAX_LENGTH ||
                tagService.checkChildTagOfParentTagParamBound(parentTagId, start)
        ) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"code\": 400, \"msg\": \"overflow!\"}");
        }

        return tagService.childTagList(parentTagId, start, len);
    }

    /**
     * 1. 查找以 tag 为 root 的标签树, 不需要限定 depth < maxDepth 这是由数据库设计决定的
     * 1.1 从每个根节点到 tag 的路径，以tag为root的标签树
     *
     * @param target 找到以 target 为核心的 树结构
     * @return 返回和数据库信息存储方式一样存储的 path 、 每个涉及到的 tag 的信息
     */
    @GetMapping("/tagTree")
    public Object tagTree(@RequestParam(value = "root") int target) {
        if (target <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"code\": 400, \"msg\": \"参数不正确!\"}");
        }
        return tagService.tagTree(target);
    }

    public Object updateTags(
            @RequestParam("role") RoleEnum role,
            @RequestParam("data") List<TagModifyDTO> modifyList
    ) {
        return null;
    }
}
