package com.highestpeak.springblog.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.highestpeak.springblog.constant.SqlTableConstant;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author highestpeak
 */
@Getter
@Setter
@Builder
public class Repo {
    @TableId(type = IdType.AUTO)
    private int id;

    /**
     * todo： 添加一个构建规则，而不是保存url的全部
     */
    @TableField(value = SqlTableConstant.RepoColName.REPO_URL)
    private String repoUrl;

    @TableField(value = SqlTableConstant.RepoColName.CREATE_TIME)
    private LocalDateTime createTime;

    /**
     * 有一篇文章是描述这个repo的，该文章的外键id
     */
    @TableField(value = SqlTableConstant.RepoColName.DESC_ARTICLE_ID)
    private int descArticleId;
}
