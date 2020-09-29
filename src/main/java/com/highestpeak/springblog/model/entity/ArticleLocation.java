package com.highestpeak.springblog.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.highestpeak.springblog.constant.SqlTableConstant;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author highestpeak
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
public class ArticleLocation {
    @TableId(type= IdType.AUTO)
    private int id;
    private int article;
    private String location;

    @TableField(value = SqlTableConstant.ArticleLocationColName.LOCATION_VALUE)
    private String locationValue;

    @TableField(value = SqlTableConstant.ArticleLocationColName.CREATE_TIME)
    private LocalDateTime createTime;

    @TableField(value = SqlTableConstant.ArticleLocationColName.UPDATE_TIME)
    private LocalDateTime updateTime;
}
