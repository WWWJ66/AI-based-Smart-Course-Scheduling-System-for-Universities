package com.acss.springbootinit.service.impl;

import com.acss.springbootinit.mapper.ScheduleMapper;
import com.acss.springbootinit.model.dto.course.ScheduleDTO;
import com.acss.springbootinit.model.entity.Schedule;
import com.acss.springbootinit.service.IScheduleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class ScheduleServiceImpl extends ServiceImpl<ScheduleMapper, Schedule> implements IScheduleService {

    @Resource
    private ScheduleMapper scheduleMapper;

    @Override
    public List<ScheduleDTO> queryScheduleByClassNo(String term, String classNo) {
        return scheduleMapper.queryScheduleByClassNo(term,classNo);
    }


    @Override
    public List<ScheduleDTO> queryScheduleByTeacherNo(String term, String teacherNo) {
        return scheduleMapper.queryScheduleByTeacherNo(term,teacherNo);
    }

    @Override
    public List<Map<String, Object>> selectDistinctClassesByTermAndTeacher(String term, String teacherNo) {
        return scheduleMapper.selectDistinctClassesByTermAndTeacher(term,teacherNo);
    }

    @Override
    public List<String> getCourseTimeList(String classNo, String teacherNo) {
        return scheduleMapper.getCourseTimeList(classNo,teacherNo);
    }

    @Override
    public List<Map<String, String>> getClassroomList(String classroomNo,String courseTime) {
        return scheduleMapper.getClassroomList(classroomNo,courseTime);
    }
}
