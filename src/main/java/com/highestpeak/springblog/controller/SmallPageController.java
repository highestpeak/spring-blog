package com.highestpeak.springblog.controller;

import com.highestpeak.springblog.constant.enumerate.RoleEnum;
import com.highestpeak.springblog.model.dto.RepoModifyDTO;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author highestpeak
 * 一些小功能的页面，不足以单独提出作为Conrtoller
 * todo: 新模块
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1")
public class SmallPageController {

    @GetMapping("/timeline")
    public Object timeLine(
            @RequestParam("start") LocalDateTime start,
            @RequestParam("end") LocalDateTime end
    ) {
        return null;
    }

    @GetMapping("/repo")
    public Object repo() {
        return null;
    }

    @PostMapping("/repo")
    public Object repo(
            @RequestParam("role") RoleEnum role,
            @RequestParam("data") List<RepoModifyDTO> modifyList
    ) {
        return null;
    }

    @GetMapping("/friendLink")
    public Object friendLink() {
        return null;
    }

    @PostMapping("/friendLink")
    public Object friendLink(
            @RequestParam("role") RoleEnum role,
            @RequestParam("data") List<RepoModifyDTO> modifyList
    ) {
        return null;
    }

    @GetMapping("/setting")
    public Object setting() {
        return null;
    }
}
