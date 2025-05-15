package com.acss.springbootinit.service;

import com.acss.springbootinit.model.dto.course.StudentDTO;
import com.acss.springbootinit.model.entity.Student;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;


import java.util.List;


public interface IStudentService extends IService<Student> {

    Page<Student> findPage(Page<Student> page, String college, String grade, String classNo, String studentNo, String studentName);

    int getMaxLastTwoDigits(String classNo);

    List<StudentDTO> export(String college, String grade, String classNo, String studentNo, String studentName);

    int imp(List<StudentDTO> studentDTOS);

    // 新增学生对应用户
    int addStudentUser(Student student);

    // 删除学生对应用户
    int deleteStudentUser(Integer id);

    Page<StudentDTO> findPageByTeacher(Page<StudentDTO> page, String term,String userNo, String classNo, String studentNo, String studentName);

    List<StudentDTO> exportByTeacher(String term,String userNo, String classNo, String studentNo, String studentName);

    int updateClassStudentNumber(String classNo,Integer num);
}
