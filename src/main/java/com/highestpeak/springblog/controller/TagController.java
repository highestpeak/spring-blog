package com.highestpeak.springblog.controller;

import com.highestpeak.springblog.constant.enumerate.RoleEnum;
import com.highestpeak.springblog.model.dto.TagDTO;
import com.highestpeak.springblog.model.dto.TagModifyDTO;
import com.highestpeak.springblog.service.TagServiceImpl;
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

    /**
     * 一个tag的所有文章
     * 当一个tag的文章过多时就可以通过分页来显示
     * todo: start 校验
     *
     * @param name
     * @param start
     * @param len
     * @return
     */
    @GetMapping
    public Object tag(
            @RequestParam("name") String name,
            @RequestParam(value = "start", required = false) int start,
            @RequestParam(value = "len", required = false) int len
    ) {
        return tagService.tagWithItsArticles(name, start, len);
    }

    /**
     * 所有的tag
     * 必须输入level，或输入某个tag即采用该tag的level
     *
     * @param limit
     * @param weight
     * @return
     */
    @GetMapping("/list")
    public Object tagList(
            @RequestParam("limit") int limit,
            @RequestParam(value = "level", defaultValue = "-1") int weight,
            @RequestParam(value = "tag",required = false) TagDTO tag
    ) {
        return null;
    }

    /**
     * todo: 标签树
     *
     * @return
     */
    @GetMapping("/tagTree")
    public Object tagTree() {
        return null;
    }

    public Object updateTags(
            @RequestParam("role") RoleEnum role,
            @RequestParam("data") List<TagModifyDTO> modifyList
    ) {
        return null;
    }
}
