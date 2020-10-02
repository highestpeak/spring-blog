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
public class Tag {
    @TableId(type = IdType.AUTO)
    private int id;

    private String name;

    private String description;

    private int article;

    @TableField(value = SqlTableConstant.TagColName.TOP_LEVEL)
    private int topLevel;

    private LocalDateTime createTime;
}
