package com.highestpeak.springblog.model.dto;

import com.highestpeak.springblog.constant.enumerate.ModifyTypeEnum;

import java.util.List;

/**
 * @author highestpeak
 */
public class RepoModifyDTO {
    private ModifyTypeEnum type;
    private List<RepoDTO> repos;
}
