package com.acss.springbootinit.service;

import com.acss.springbootinit.model.dto.course.EchartsDTO;
import com.acss.springbootinit.model.entity.CoursePlan;
import com.baomidou.mybatisplus.extension.service.IService;


import java.util.List;


public interface ICoursePlanService extends IService<CoursePlan> {

    String courseScheduling(String term);

    List<EchartsDTO> queryCoursePlanProcess(String term, String gradeNo);
}
