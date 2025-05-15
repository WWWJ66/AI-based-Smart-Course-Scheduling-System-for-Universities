package com.acss.springbootinit.mapper;

import com.acss.springbootinit.model.dto.course.CoursePlanDTO;
import com.acss.springbootinit.model.entity.CourseInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;


public interface CourseInfoMapper extends BaseMapper<CourseInfo> {

    List<CoursePlanDTO> findCourse();

    //统计某个学院某个属性课程的开课数目
    @Select("SELECT MAX(CAST(SUBSTRING(course_no, -3) AS UNSIGNED)) AS max_last_three_digits FROM course_info WHERE college_no = #{college} AND course_attribute=#{courseAttribute};")
    Integer lastThreeDigits(@Param("college") String college, @Param("courseAttribute") String courseAttribute);

    //导出
    List<CourseInfo> export(@Param("college")String college, @Param("courseAttribute")String courseAttribute, @Param("courseName")String courseName);

    //导入
    int imp(List<CourseInfo> courseInfoList);
}
