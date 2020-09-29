package com.highestpeak.springblog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.highestpeak.springblog.model.entity.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author highestpeak
 */
@Mapper
@Repository
public interface TagMapper extends BaseMapper<Tag> {

}
