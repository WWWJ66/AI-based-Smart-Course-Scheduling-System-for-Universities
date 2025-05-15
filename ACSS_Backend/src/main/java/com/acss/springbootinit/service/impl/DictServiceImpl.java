package com.acss.springbootinit.service.impl;

import com.acss.springbootinit.mapper.DictMapper;
import com.acss.springbootinit.model.entity.Dict;
import com.acss.springbootinit.service.IDictService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements IDictService {
    @Resource
    DictMapper dictMapper;

    @Override
    public List<String> selectType() {
        return dictMapper.selectType();
    }

    @Override
    public Page<Dict> findPage(Page<Dict> page, String type) {
        return dictMapper.findPage(page, type);
    }

    @Override
    public List<Map<String, Object>> queryTypeCount() {
       return dictMapper.queryTypeCount();
    }
}
