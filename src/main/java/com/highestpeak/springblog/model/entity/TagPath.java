package com.highestpeak.springblog.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.highestpeak.springblog.constant.SqlTableConstant;
import lombok.Getter;
import lombok.Setter;

/**
 * @author highestpeak
 */
@Getter
@Setter
@TableName(value = SqlTableConstant.TableName.TAG_PATH)
public class TagPath {
    @TableId(type= IdType.AUTO)
    private int id;

    private int ancestor;
    private int descendant;
    private int depth;
}
