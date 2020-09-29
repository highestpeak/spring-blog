package com.highestpeak.springblog.controller;

import com.highestpeak.springblog.constant.enumerate.CommentPlaceEnum;
import com.highestpeak.springblog.constant.enumerate.CommentsSortEnum;
import org.springframework.web.bind.annotation.*;

/**
 * @author highestpeak
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/comment")
public class CommentController {

    @GetMapping("/list")
    public Object commentList(
            @RequestParam("place") CommentPlaceEnum place, @RequestParam("id") int id,
            @RequestParam("start") int start, @RequestParam("len") int len,
            @RequestParam("sort-by") CommentsSortEnum sortBy, @RequestParam("desc") boolean desc
    ) {
        // 所有包括其他站点的评论
        // 其他站点的评论动态更新
        return null;
    }

    public Object modifyComment() {
        return null;
    }
}
