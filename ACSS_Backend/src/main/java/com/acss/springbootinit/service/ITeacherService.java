package com.acss.springbootinit.service;

import com.acss.springbootinit.model.dto.course.TeacherDTO;
import com.acss.springbootinit.model.entity.Teacher;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;


import java.util.List;


public interface ITeacherService extends IService<Teacher> {

    Page<Teacher> findPage(Page<Teacher> page, String teacherNo, String teacherName, String college);

    int getMaxLastThreeDigits(String college);

    List<TeacherDTO> export(String college, String teacherNo, String teacherName);

    int imp(List<TeacherDTO> list);

    int addTeacherUser(Teacher teacher);

    int deleteTeacherUser(Integer id);
}
