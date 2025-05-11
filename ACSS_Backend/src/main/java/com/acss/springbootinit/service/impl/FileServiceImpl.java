package com.acss.springbootinit.service.impl;

import com.acss.springbootinit.mapper.FileMapper;
import com.acss.springbootinit.model.entity.Files;
import com.acss.springbootinit.service.IFileService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.stereotype.Service;


@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, Files> implements IFileService {

}
