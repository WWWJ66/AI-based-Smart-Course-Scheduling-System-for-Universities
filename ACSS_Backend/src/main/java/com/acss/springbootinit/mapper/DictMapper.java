package com.acss.springbootinit.mapper;

import com.acss.springbootinit.model.entity.Dict;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;


public interface DictMapper extends BaseMapper<Dict> {
   //查询字典类型列表
    @Select("SELECT DISTINCT type FROM sys_dict")
    List<String> selectType();

    Page<Dict> findPage(Page<Dict> page, @Param("type")String type);
    //echart饼图查询语句
    List<Map<String, Object>> queryTypeCount();
}
