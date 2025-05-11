package com.acss.springbootinit.mapper;

import com.acss.springbootinit.model.dto.course.TeacherDTO;
import com.acss.springbootinit.model.entity.Teacher;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface TeacherMapper extends BaseMapper<Teacher> {
    Page<Teacher> findPage(Page<Teacher> page, @Param("teacherNo")String teacherNo, @Param("teacherName")String teacherName, @Param("college") String college);
    //从int改为Integer。这样可以允许方法返回null。
    Integer getMaxLastThreeDigits(@Param("college")String college);
    //教师信息导出导入
    List<TeacherDTO> export(@Param("college")String college, @Param("teacherNo")String teacherNo, @Param("teacherName")String teacherName);
    int imp(List<Teacher> teacherList);
}
