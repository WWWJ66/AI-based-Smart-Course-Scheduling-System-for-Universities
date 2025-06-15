package com.acss.springbootinit.service;

import com.acss.springbootinit.model.entity.ClassInfo;
import com.baomidou.mybatisplus.extension.service.IService;



public interface IClassInfoService extends IService<ClassInfo> {

    int lastTwoDigits(String gradeNo, String majorNo);

}
