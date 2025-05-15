package com.acss.springbootinit.service.impl;

import com.acss.springbootinit.mapper.ClassInfoMapper;
import com.acss.springbootinit.model.entity.ClassInfo;
import com.acss.springbootinit.service.IClassInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
public class ClassInfoServiceImpl extends ServiceImpl<ClassInfoMapper, ClassInfo> implements IClassInfoService {
    @Resource
    private ClassInfoMapper classInfoMapper;

    @Override
    public int lastTwoDigits(String gradeNo, String majorNo) {
        Integer result = classInfoMapper.lastTwoDigits(gradeNo, majorNo);
        return result != null ? result : 0;
    }
}
