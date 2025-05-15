package com.acss.springbootinit.service;

import com.acss.springbootinit.model.entity.Dict;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;


import java.util.List;
import java.util.Map;


public interface IDictService extends IService<Dict> {

    List<String> selectType();

    Page<Dict>  findPage(Page<Dict> page, String type);

    List<Map<String, Object>> queryTypeCount();


}
