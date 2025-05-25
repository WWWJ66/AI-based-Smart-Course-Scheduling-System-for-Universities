/**
 * @ClassName ScheduleController
 * @Description [处理排课的请求]
 */
package com.acss.springbootinit.controller;

import cn.dev33.satoken.annotation.SaCheckRole;

import com.acss.springbootinit.common.Result;
import com.acss.springbootinit.model.dto.course.ScheduleDTO;
import com.acss.springbootinit.model.entity.Schedule;
import com.acss.springbootinit.service.IScheduleService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;



@RestController
@RequestMapping("/schedule")
public class ScheduleController {
    @Resource
    private IScheduleService scheduleService;

    /**
     * 根据学期和班级编号查询课程表
     *
     * @param term 学期
     * @param classNo 班级编号
     * @return 课程表列表（ScheduleDTO）
     */
    @GetMapping
    public Result queryScheduleByClassNo(@RequestParam("term") String term, @RequestParam("classNo") String classNo) {
        List<ScheduleDTO> scheduleList = scheduleService.queryScheduleByClassNo(term,classNo);

        return Result.success(scheduleList);
    }

    /**
     * 根据学期和教师编号查询课程表
     *
     * @param term 学期
     * @param teacherNo 教师编号
     * @return 课程表列表（ScheduleDTO）
     */
    @SaCheckRole("ROLE_TEACHER")
    @GetMapping("/teacher")
    public Result queryScheduleByTeacherNo(@RequestParam("term") String term,@RequestParam("teacherNo") String teacherNo) {
        List<ScheduleDTO> scheduleList = scheduleService.queryScheduleByTeacherNo(term,teacherNo);
        return Result.success(scheduleList);
    }

    /**
     * 通过学期和教师编号查询所任课的不同班级列表
     *
     * @param term 学期
     * @param teacherNo 教师编号
     * @return 不同班级列表
     */
    @SaCheckRole("ROLE_TEACHER")
    @GetMapping("/term")
    public Result selectDistinctClassesByTermAndTeacher(@RequestParam("term") String term,@RequestParam("teacherNo") String teacherNo) {
        return Result.success(scheduleService.selectDistinctClassesByTermAndTeacher(term,teacherNo));
    }

    /**
     * 获取课程可修改的时间列表
     * 通过查询班级空余时间和教师空余时间得到
     *
     * @param id 课程表ID
     * @return 可修改的课程时间列表
     */
    @GetMapping("/courseTime/{id}")
    //获取课程可修改的时间列表，通过查询班级空余时间和老师空余时间
    public Result getCourseTimeList(@PathVariable Integer id) {
        Schedule schedule = scheduleService.getById(id);
        String classNo=schedule.getClassNo();
        String teacherNo=schedule.getTeacherNo();
        return Result.success(scheduleService.getCourseTimeList(classNo,teacherNo));
    }

    /**
     * 获取教室列表
     * 通过当前课程时间和教室编号查询
     *
     * @param id 课程表ID
     * @return 可用教室列表
     */
    @GetMapping("/classroom/{id}")
    public Result getClassroomList(@PathVariable Integer id) {
        Schedule schedule = scheduleService.getById(id);
        String courseTime = schedule.getCourseTime();
        String classroomNo = schedule.getClassroomNo();
        return Result.success(scheduleService.getClassroomList(classroomNo,courseTime));
    }

    /**
     * 新增或更新课程表记录
     *
     * @param schedule 课程表实体
     * @return 操作结果
     */
    @PostMapping
    public Result save(@RequestBody Schedule schedule) {
        scheduleService.saveOrUpdate(schedule);
        return Result.success();
    }
}