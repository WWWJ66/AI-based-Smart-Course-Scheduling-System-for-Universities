package com.acss.springbootinit.service;

import com.acss.springbootinit.model.dto.course.CourseInfoDTO;
import com.acss.springbootinit.model.dto.course.CoursePlanDTO;
import com.acss.springbootinit.model.entity.CourseInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


public interface ICourseInfoService extends IService<CourseInfo> {
    List<CoursePlanDTO> findCourse();

    int lastThreeDigits(String college, String courseAttribute);

    List<CourseInfo> export(String college, String courseAttribute, String courseName);

    int imp(List<CourseInfoDTO> list);
}
