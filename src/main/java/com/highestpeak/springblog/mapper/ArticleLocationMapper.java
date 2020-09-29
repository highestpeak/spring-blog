package com.highestpeak.springblog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.highestpeak.springblog.model.entity.ArticleLocation;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author highestpeak
 */
@Repository
@Mapper
public interface ArticleLocationMapper extends BaseMapper<ArticleLocation> {

}
