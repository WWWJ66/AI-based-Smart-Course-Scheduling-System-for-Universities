package com.acss.springbootinit.controller;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.acss.springbootinit.common.Result;
import com.acss.springbootinit.mapper.FileMapper;
import com.acss.springbootinit.model.entity.Files;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;


//
// import cn.hutool.core.io.FileUtil;
// import com.acss.springbootinit.common.BaseResponse;
// import com.acss.springbootinit.common.ErrorCode;
// import com.acss.springbootinit.common.ResultUtils;
// import com.acss.springbootinit.constant.FileConstant;
// import com.acss.springbootinit.exception.BusinessException;
// import com.acss.springbootinit.manager.CosManager;
// import com.acss.springbootinit.model.dto.file.UploadFileRequest;
// import com.acss.springbootinit.model.entity.User;
// import com.acss.springbootinit.model.enums.FileUploadBizEnum;
// import com.acss.springbootinit.service.IUserService;
// import java.io.File;
// import java.util.Arrays;
// import javax.annotation.Resource;
// import javax.servlet.http.HttpServletRequest;
// import lombok.extern.slf4j.Slf4j;
// import org.apache.commons.lang3.RandomStringUtils;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestPart;
// import org.springframework.web.bind.annotation.RestController;
// import org.springframework.web.multipart.MultipartFile;
//
// /**
//  * 文件接口
//  */
// @RestController
// @RequestMapping("/file")
// @Slf4j
// public class FileController {
//
//     @Resource
//     private IUserService IUserService;
//
//     @Resource
//     private CosManager cosManager;
//
//     /**
//      * 文件上传
//      *
//      * @param multipartFile
//      * @param uploadFileRequest
//      * @param request
//      * @return
//      */
//     @PostMapping("/upload")
//     public BaseResponse<String> uploadFile(@RequestPart("file") MultipartFile multipartFile,
//             UploadFileRequest uploadFileRequest, HttpServletRequest request) {
//         String biz = uploadFileRequest.getBiz();
//         FileUploadBizEnum fileUploadBizEnum = FileUploadBizEnum.getEnumByValue(biz);
//         if (fileUploadBizEnum == null) {
//             throw new BusinessException(ErrorCode.PARAMS_ERROR);
//         }
//         validFile(multipartFile, fileUploadBizEnum);
//         User loginUser = IUserService.getLoginUser(request);
//         // 文件目录：根据业务、用户来划分
//         String uuid = RandomStringUtils.randomAlphanumeric(8);
//         String filename = uuid + "-" + multipartFile.getOriginalFilename();
//         String filepath = String.format("/%s/%s/%s", fileUploadBizEnum.getValue(), loginUser.getId(), filename);
//         File file = null;
//         try {
//             // 上传文件
//             file = File.createTempFile(filepath, null);
//             multipartFile.transferTo(file);
//             cosManager.putObject(filepath, file);
//             // 返回可访问地址
//             return ResultUtils.success(FileConstant.COS_HOST + filepath);
//         } catch (Exception e) {
//             log.error("file upload error, filepath = " + filepath, e);
//             throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
//         } finally {
//             if (file != null) {
//                 // 删除临时文件
//                 boolean delete = file.delete();
//                 if (!delete) {
//                     log.error("file delete error, filepath = {}", filepath);
//                 }
//             }
//         }
//     }
//
//     /**
//      * 校验文件
//      *
//      * @param multipartFile
//      * @param fileUploadBizEnum 业务类型
//      */
//     private void validFile(MultipartFile multipartFile, FileUploadBizEnum fileUploadBizEnum) {
//         // 文件大小
//         long fileSize = multipartFile.getSize();
//         // 文件后缀
//         String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
//         final long ONE_M = 1024 * 1024L;
//         if (FileUploadBizEnum.USER_AVATAR.equals(fileUploadBizEnum)) {
//             if (fileSize > ONE_M) {
//                 throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小不能超过 1M");
//             }
//             if (!Arrays.asList("jpeg", "jpg", "svg", "png", "webp").contains(fileSuffix)) {
//                 throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件类型错误");
//             }
//         }
//     }
// }
@RestController
@RequestMapping("/file")
public class FileController {
    @Resource
    private FileMapper fileMapper;

    //从配置文件中取出路径
    @Value("${files.upload.path}")
    private String fileUploadPath;

    /**
     * 文件上传接口
     *
     * @param file 前端传递过来的文件
     * @return
     * @throws IOException
     */
    @SaIgnore
    @PostMapping("/upload")
    public String upload(@RequestParam MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        //获取文件类型
        String type = FileUtil.extName(originalFilename);
        //文件大小
        long size = file.getSize();

        // 定义一个文件唯一的标识码
        String uuid = IdUtil.fastSimpleUUID();
        String fileUUID = uuid + StrUtil.DOT + type;

        java.io.File uploadFile = new java.io.File(fileUploadPath + fileUUID);
        // 判断配置的文件目录是否存在，若不存在则创建一个新的文件目录
        java.io.File parentFile = uploadFile.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        String url;
        // 获取文件的md5
        String md5 = SecureUtil.md5(file.getInputStream());
        // 从数据库查询是否存在相同的记录
        Files dbFiles = getFileByMd5(md5);
        if (dbFiles != null) { // 文件已存在
            url = dbFiles.getUrl();
        } else {
            // 上传文件到磁盘
            file.transferTo(uploadFile);
            // 数据库若不存在重复文件，则不删除刚才上传的文件
            url = "http://localhost:8081/file/" + fileUUID;
        }

        // 存储数据库
        Files saveFile = new Files();
        saveFile.setName(originalFilename);
        saveFile.setType(type);
        saveFile.setSize(size / 1024);
        saveFile.setUrl(url);
        saveFile.setMd5(md5);
        fileMapper.insert(saveFile);
        return url;
    }

    /**
     * 文件下载接口   http://localhost:8081/file/{fileUUID}
     *
     * @param fileUUID
     * @param response
     * @throws IOException
     */
    @SaIgnore  //所有人都可以访问这个接口
    @GetMapping("/{fileUUID}")
    public void download(@PathVariable String fileUUID, HttpServletResponse response) throws IOException {
        // 根据文件的唯一标识码获取文件
        java.io.File uploadFile = new File(fileUploadPath + fileUUID);
        // 设置输出流的格式
        ServletOutputStream os = response.getOutputStream();
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileUUID, "UTF-8"));
        response.setContentType("application/octet-stream");

        // 读取文件的字节流
        os.write(FileUtil.readBytes(uploadFile));
        os.flush();
        os.close();
    }

    /**
     * 通过文件的md5查询文件
     *
     * @param md5
     * @return
     */
    private Files getFileByMd5(String md5) {
        // 查询文件的md5是否存在
        QueryWrapper<Files> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("md5", md5);
        List<Files> filesList = fileMapper.selectList(queryWrapper);
        return filesList.size() == 0 ? null : filesList.get(0);
    }

    @PostMapping("/update")
    public Result update(@RequestBody Files files) {
        fileMapper.updateById(files);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        Files files = fileMapper.selectById(id);
        files.setIsDelete(true);
        fileMapper.updateById(files);
        return Result.success();
    }

    @GetMapping("/detail/{id}")
    public Result getById(@PathVariable Integer id) {
        return Result.success(fileMapper.selectById(id));
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        // select * from sys_file where id in (id,id,id...)
        QueryWrapper<Files> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", ids);
        List<Files> files = fileMapper.selectList(queryWrapper);
        for (Files file : files) {
            file.setIsDelete(true);
            fileMapper.updateById(file);
        }
        return Result.success();
    }

    /**
     * 分页查询接口
     *
     * @param pageNum
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String name) {

        QueryWrapper<Files> queryWrapper = new QueryWrapper<>();
        // 查询未删除的记录
        queryWrapper.eq("delete", false);
        queryWrapper.orderByDesc("id");
        if (!"".equals(name)) {
            queryWrapper.like("name", name);
        }
        return Result.success(fileMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper));
    }
}