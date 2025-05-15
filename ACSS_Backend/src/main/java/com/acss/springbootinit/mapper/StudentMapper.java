package com.acss.springbootinit.mapper;

import com.acss.springbootinit.model.dto.course.StudentDTO;
import com.acss.springbootinit.model.entity.Student;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;



public interface StudentMapper extends BaseMapper<Student> {
        //管理员查询导出导入数据
        Page<Student> findPage(Page<Student> page,  @Param("college") String college, @Param("grade") String grade, @Param("classNo") String classNo, @Param("studentNo") String studentNo, @Param("studentName") String studentName);
        List<StudentDTO> export(@Param("college") String college, @Param("grade") String grade, @Param("classNo") String classNo, @Param("studentNo") String studentNo, @Param("studentName") String studentName);
        int imp(List<Student> student);
        //获取最后两位
        Integer getMaxLastTwoDigits(@Param("classNo")String classNo);

        //任课教师查询导出数据
        Page<StudentDTO> findPageByTeacher(Page<StudentDTO> page, @Param("term")String term,@Param("userNo")String userNo, @Param("classNo")String classNo, @Param("studentNo")String studentNo, @Param("studentName")String studentName);
        List<StudentDTO> exportByTeacher(@Param("term")String term, @Param("userNo")String userNo, @Param("classNo")String classNo, @Param("studentNo")String studentNo, @Param("studentName")String studentName);
       //班级人数更新
        @Update("UPDATE class_info SET student_number = student_number + #{num}  WHERE class_no = #{classNo};")
        int updateClassStudentNumber(@Param("classNo")String classNo,@Param("num")Integer num);
}
