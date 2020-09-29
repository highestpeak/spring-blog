package com.highestpeak.springblog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author highestpeak
 */
@Service
public class UtilService {
    /**
     * 对形如 ArticleTag 的表的更新操作：删除旧的插入新的
     */
    public <T> void delAndInsertRow(
            List<T> oldList, List<T> newList, IService<T> tableService, Function<T, Integer> oldToDelIdFunction,
            int tId, StringBuilder delFailMsgBuilder, StringBuilder insertFailMsgBuilder
    ) {
        // to insert
        List<T> newToInsertList =
                newList.stream().filter(oldList::contains).collect(Collectors.toList());
        // to del
        List<Integer> oldToDelList =
                oldList.stream().filter(newList::contains).map(oldToDelIdFunction).collect(Collectors.toList());
        // 删除旧的,插入新的
        boolean isDelSuccess = tableService.removeByIds(oldToDelList);
        boolean isInsertSuccess = tableService.saveBatch(newToInsertList);
        if (!isDelSuccess) {
            delFailMsgBuilder.append(tId).append(",");
        }
        if (!isInsertSuccess) {
            insertFailMsgBuilder.append(tId).append(",");
        }
    }
}
