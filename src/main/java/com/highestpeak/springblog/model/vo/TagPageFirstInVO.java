package com.highestpeak.springblog.model.vo;

import com.highestpeak.springblog.model.entity.Tag;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author highestpeak
 */
@Setter
@Getter
public class TagPageFirstInVO {
    private List<Tag> topTagList;
    private ArticleListBelongToTagVO articleListBelongToFirstTag;
    private List<Tag> firstChildTagList;
}
