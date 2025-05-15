package com.acss.springbootinit.service;

import com.acss.springbootinit.model.dto.course.ScheduleDTO;
import com.acss.springbootinit.model.entity.Schedule;
import com.baomidou.mybatisplus.extension.service.IService;


import java.util.List;
import java.util.Map;


public interface IScheduleService extends IService<Schedule> {

    List<ScheduleDTO> queryScheduleByClassNo(String term, String classNo);

    List<ScheduleDTO> queryScheduleByTeacherNo(String term, String teacherNo);

    List<Map<String, Object>> selectDistinctClassesByTermAndTeacher(String term, String teacherNo);

    List<String> getCourseTimeList(String classNo, String teacherNo);

    List<Map<String, String>> getClassroomList(String classroomNo,String courseTime);
}
