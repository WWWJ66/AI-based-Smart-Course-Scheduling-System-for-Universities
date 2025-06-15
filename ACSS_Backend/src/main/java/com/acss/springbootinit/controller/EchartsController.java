/**
 * @ClassName EchartsController
 * @Description 提供系统首页数据统计相关接口，用于图表数据展示（如学生人数、教师人数等）
 */
package com.acss.springbootinit.controller;


import cn.hutool.core.collection.CollUtil;

import com.acss.springbootinit.common.Result;
import com.acss.springbootinit.service.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/echarts")
public class EchartsController {

    @Resource
    private IStudentService studentService;
    @Resource
    private ITeacherService teacherService;
    @Resource
    private IClassInfoService classInfoService;
    @Resource
    private ICourseInfoService courseInfoService;
    @Resource
    private IDictService dictService;
    @Resource
    private ICoursePlanService coursePlanService;

    /**
     * 获取首页统计数据，包括学生人数、教师人数、班级总数、课程总数
     *
     * @return 包含统计项及对应数值的结果集
     */
    @GetMapping("/count")
    public Result get() {
        Map<String, Object> map = new HashMap<>();
        map.put("key", CollUtil.newArrayList("学生人数", "教师人数", "班级总数", "课程总数"));
        map.put("value", CollUtil.newArrayList(studentService.count(), teacherService.count(), classInfoService.count(), courseInfoService.count()));
        return Result.success(map);
    }

    /**
     * 查询不同 type（字典类型）的数量，用于类型分布统计
     *
     * @return 字典类型及对应数量的集合
     */
    @GetMapping("/queryTypeCount")
    public Result queryTypeCount() {
        return Result.success(dictService.queryTypeCount());
    }

    /**
     * 查询课程计划的执行进度，支持根据学期和年级筛选
     *
     * @param term    学期（可选）
     * @param gradeNo 年级编号（可选）
     * @return 课程计划执行进度数据
     */
    @GetMapping("/queryCoursePlanProcess")
    public Result queryCoursePlanProcess(
            @RequestParam(defaultValue = "") String term, @RequestParam(defaultValue = "") String gradeNo) {
        return Result.success(coursePlanService.queryCoursePlanProcess(term,gradeNo));
    }
}