package com.highestpeak.springblog.model.dto;

import com.highestpeak.springblog.constant.enumerate.ModifyTypeEnum;

import java.util.List;

/**
 * @author highestpeak
 */
public class TagModifyDTO {
    private ModifyTypeEnum type;
    private List<TagDTO> tags;
}
