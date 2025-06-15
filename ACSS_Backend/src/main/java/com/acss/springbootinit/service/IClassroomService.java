package com.acss.springbootinit.service;

import com.acss.springbootinit.model.entity.Classroom;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;



public interface IClassroomService extends IService<Classroom> {
    Page<Classroom> findPage(Page<Classroom> page, String teachingBuilding, String classroomType);

    int lastTwoDigits(String teachingBuildingNo, String classroomType);
}
