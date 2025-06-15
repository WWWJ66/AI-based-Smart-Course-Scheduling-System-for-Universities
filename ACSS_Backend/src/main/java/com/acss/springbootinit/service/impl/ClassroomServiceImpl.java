package com.acss.springbootinit.service.impl;

import com.acss.springbootinit.mapper.ClassroomMapper;
import com.acss.springbootinit.model.entity.Classroom;
import com.acss.springbootinit.service.IClassroomService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ClassroomServiceImpl extends ServiceImpl<ClassroomMapper, Classroom> implements IClassroomService {

    @Resource
    private ClassroomMapper classroomMapper;

    @Override
    public Page<Classroom> findPage(Page<Classroom> page, String teachingBuilding, String type) {
        return classroomMapper.findPage(page, teachingBuilding, type);
    }

    @Override
    public int lastTwoDigits(String teachingBuildingNo, String classroomType) {
        Integer result = classroomMapper.lastTwoDigits(teachingBuildingNo, classroomType);
        return result != null ? result : 0;
    }

}
