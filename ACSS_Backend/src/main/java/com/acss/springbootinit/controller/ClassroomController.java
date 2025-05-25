/**
 * @ClassName ClassroomController
 * @Description [处理教室信息有关的请求]
 */
package com.acss.springbootinit.controller;


import com.acss.springbootinit.common.Result;
import com.acss.springbootinit.model.entity.Classroom;
import com.acss.springbootinit.service.IClassroomService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/classroom")
public class ClassroomController {

    @Resource
    private IClassroomService classroomService;

    /**
     * 保存教室信息
     *
     * @param classroom 请求体，包含教室id、教室编号、教学楼编号等信息
     * @return 返回操作结果，根据结果返回信息码
     */
    @PostMapping
    public Result save(@RequestBody Classroom classroom) {
        classroomService.saveOrUpdate(classroom);
        return Result.success();
    }

    /**
     * 删除教室信息
     *
     * @param id 教室id
     * @return 返回操作结果，根据结果返回信息码
     */
    @DeleteMapping("/{id}")

    public Result delete(@PathVariable Integer id) {
        classroomService.removeById(id);
        return Result.success();
    }

    /**
     * 批量教室信息
     *
     * @param ids 教室id列表
     * @return 返回操作结果，根据结果返回信息码
     */
    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {//批量删除
        classroomService.removeByIds(ids);
        return Result.success();
    }

    /**
     * 分页查询教室
     *
     * @param pageNum 页数
     * @param pageSize 每页个数
     * @param teachingBuilding 教学楼名称
     * @param classroomType 教室类型
     * @return 返回分页查询结果
     */
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum, @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String teachingBuilding,
                           @RequestParam(defaultValue = "") String classroomType) {
        QueryWrapper<Classroom> queryWrapper = new QueryWrapper<>();
        // 精确查询
        if (!StringUtils.isEmpty(teachingBuilding)) {
            queryWrapper.eq("teaching_building_no", teachingBuilding);
        }
        // 精确查询
        if (!StringUtils.isEmpty(classroomType)) {
            queryWrapper.eq("classroom_type", classroomType);
        }
        queryWrapper.orderByAsc("id");
        return Result.success(classroomService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

    /**
     * 生成教室编号
     *
     * @param requestData 请求数据，包含教学楼、教室类型
     * @return 教室编号
     */
    @PostMapping("/generateClassroomNo")
    public Result generateStudentNo(@RequestBody Map<String, Object> requestData) {
        // 获取 form 中的参数
        String teachingBuildingNo = String.valueOf(requestData.get("teachingBuildingNo"));
        String classroomType = String.valueOf(requestData.get("classroomType"));
        String classroomNoPrefix = teachingBuildingNo + classroomType;
        //通过查询数据库中同一教学楼同一类型教室的个数，得到教室编号后两位
        int lastTwoDigits = classroomService.lastTwoDigits(teachingBuildingNo, classroomType);
        String classroomNo = classroomNoPrefix + String.format("%02d", lastTwoDigits + 1);
        return Result.success("教室编号生成成功！",classroomNo);
    }
}
