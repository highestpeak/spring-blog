package com.highestpeak.springblog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.highestpeak.springblog.mapper.RepoMapper;
import com.highestpeak.springblog.model.entity.Repo;
import org.springframework.stereotype.Service;

/**
 * @author highestpeak
 */
@Service("repoService")
public class RepoServiceImpl extends ServiceImpl<RepoMapper, Repo> implements IService<Repo> {

}
