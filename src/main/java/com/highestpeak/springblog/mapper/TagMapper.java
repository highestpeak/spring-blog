package com.highestpeak.springblog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.highestpeak.springblog.model.entity.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author highestpeak
 */
@Mapper
@Repository
public interface TagMapper extends BaseMapper<Tag> {

    /**
     * 找到 tagId 下的所有子 tag 的 list
     *
     * @param tagId tag id
     * @return 所有子 tag 的 id 的 list
     */
    @Select("select ancestor from tag_path where ancestor = #{tagId}")
    List<Integer> allSubTags(int tagId);
}
