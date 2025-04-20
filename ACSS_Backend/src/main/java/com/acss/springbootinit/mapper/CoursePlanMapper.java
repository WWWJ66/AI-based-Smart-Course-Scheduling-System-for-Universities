package com.acss.springbootinit.mapper;

import com.acss.springbootinit.model.dto.course.EchartsDTO;
import com.acss.springbootinit.model.entity.CoursePlan;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;


public interface CoursePlanMapper extends BaseMapper<CoursePlan> {
    //查询开课班级列表
    @Select("SELECT distinct class_no FROM course_plan")
    List<String> selectClassNo();
    //echart堆叠图查询语句
    List<EchartsDTO> queryCoursePlanProcess(@Param("term")String term, @Param("gradeNo")String gradeNo);
}
