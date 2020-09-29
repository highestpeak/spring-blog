package com.highestpeak.springblog.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author highestpeak
 * todo: 这是连接表的类，类似这样的类怎么处理呢？
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
public class ArticleTag {
    @TableId(type = IdType.AUTO)
    private int id;

    private int article;
    private int tag;
}
