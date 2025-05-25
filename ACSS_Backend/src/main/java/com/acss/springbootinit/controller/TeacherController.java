/**
 * @ClassName TeacherController
 * @Description [教师信息管理相关接口]
 */
package com.acss.springbootinit.controller;


import cn.dev33.satoken.stp.StpUtil;
import com.acss.springbootinit.common.Result;
import com.acss.springbootinit.constant.ConstantInfo;
import com.acss.springbootinit.model.dto.course.TeacherDTO;
import com.acss.springbootinit.model.dto.user.UserDTO;
import com.acss.springbootinit.model.entity.Teacher;
import com.acss.springbootinit.service.ITeacherService;
import com.acss.springbootinit.service.IUserService;
import com.acss.springbootinit.utils.TeacherExcelListener;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;



@RestController
@RequestMapping("/teacher")
public class TeacherController {

    @Resource
    private ITeacherService teacherService;

    @Resource
    private IUserService userService;

    /**
     * 新增或更新教师信息
     * 系统管理员可以操作所有教师，管理员仅可操作本学院教师
     * @param teacher 教师实体对象
     * @return 操作结果
     */
    @PostMapping
    @Transactional
    public Result save(@RequestBody Teacher teacher) {
        UserDTO userInfo = userService.getUserRoleAndCollege(StpUtil.getLoginIdAsString());
        String role = userInfo.getRole();
        String college = userInfo.getCollege();

        if (ConstantInfo.ROLE_ADMINISTRATOR.equals(role) || (ConstantInfo.ROLE_ADMIN.equals(role) && college.equals(teacher.getCollegeNo()))) {
            // 系统管理员有权限操作所有教师，或者管理员有权限操作本学院教师
            teacherService.saveOrUpdate(teacher);
            // 若为新增教师，则添加用户数据
            if (teacher.getId() == null) {
                teacherService.addTeacherUser(teacher);
            }
            return Result.success();
        } else {
            // 其他角色无权限
            return Result.error(ConstantInfo.CODE_401, "权限不足");
        }
    }

    /**
     * 删除指定ID的教师信息
     * 系统管理员可删除所有教师，管理员仅可删除本学院教师
     * @param id 教师ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        UserDTO userInfo = userService.getUserRoleAndCollege(StpUtil.getLoginIdAsString());
        String role = userInfo.getRole();
        String college = userInfo.getCollege();

        Teacher teacher = teacherService.getById(id);
        if (teacher == null) {
            return Result.error(ConstantInfo.CODE_404, "教师不存在");
        }
        if (ConstantInfo.ROLE_ADMINISTRATOR.equals(role) || (ConstantInfo.ROLE_ADMIN.equals(role) && college.equals(teacher.getCollegeNo()))) {
            // 系统管理员有权限操作所有教师，或者管理员有权限操作本学院教师
            teacherService.removeById(id);
            teacherService.deleteTeacherUser(id);
            return Result.success();
        } else {
            // 其他角色无权限
            return Result.error(ConstantInfo.CODE_401, "权限不足");
        }
    }

    /**
     * 批量删除教师
     * 系统管理员删除所有教师，管理员删除本学院教师
     * @param ids 教师ID列表
     * @return 操作结果
     */
    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        UserDTO userInfo = userService.getUserRoleAndCollege(StpUtil.getLoginIdAsString());
        String role = userInfo.getRole();
        String college = userInfo.getCollege();

        if (ConstantInfo.ROLE_ADMINISTRATOR.equals(role)) {
            // 系统管理员有权限操作所有教师
            deleteTeacherBatch(ids);
            return Result.success();
        } else if (ConstantInfo.ROLE_ADMIN.equals(role)) {
            // 管理员有权限操作本学院教师
            deleteTeacherBatchInCollege(ids, college);
            return Result.success();
        } else {
            // 其他角色无权限
            return Result.error(ConstantInfo.CODE_401, "权限不足");
        }
    }

    /**
     * 批量删除所有教师
     * @param ids 教师ID列表
     */
    private void deleteTeacherBatch(List<Integer> ids) {
        if (!ids.isEmpty()) {
            teacherService.removeByIds(ids);
            for (int id : ids) {
                teacherService.deleteTeacherUser(id);
            }
        }
    }

    /**
     * 批量删除指定学院的教师
     * @param ids 教师ID列表
     * @param college 学院编号
     */
    private void deleteTeacherBatchInCollege(List<Integer> ids, String college) {
        if (!ids.isEmpty()) {
            for (int id : ids) {
                Teacher teacher = teacherService.getById(id);
                if (teacher != null && college.equals(teacher.getCollegeNo())) {
                    teacherService.removeById(id);
                    teacherService.deleteTeacherUser(id);
                }
            }
        }
    }

    /**
     * 查询所有教师信息
     * 系统管理员可查看所有，管理员只能查看本学院教师，其他角色无权限
     * @return 教师列表
     */
    @GetMapping
    public Result findAll() {
        UserDTO userInfo = userService.getUserRoleAndCollege(StpUtil.getLoginIdAsString());
        String role = userInfo.getRole();
        String college = userInfo.getCollege();
        QueryWrapper<Teacher> queryWrapper = new QueryWrapper<>();

        if (ConstantInfo.ROLE_ADMINISTRATOR.equals(role)) {
            // 系统管理员可以查看所有教师信息
            // 不添加学院条件，查询所有
        } else if (ConstantInfo.ROLE_ADMIN.equals(role)) {
            // 管理员只能查看本学院教师信息
            queryWrapper.eq("college_no", college);
        } else {
            // 其他角色无权限查看所有教师信息
            return Result.error(ConstantInfo.CODE_401, "权限不足");
        }
        return Result.success(teacherService.list(queryWrapper));
    }

    /**
     * 根据ID查询教师信息
     * 系统管理员或本学院管理员有权限查看，其他角色无权限
     * @param id 教师ID
     * @return 教师信息
     */
    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        UserDTO userInfo = userService.getUserRoleAndCollege(StpUtil.getLoginIdAsString());
         String role = userInfo.getRole();
        String college = userInfo.getCollege();
        Teacher teacher = teacherService.getById(id);
        if (teacher == null) {
            return Result.error(ConstantInfo.CODE_404, "教师不存在");
        }

        if (ConstantInfo.ROLE_ADMINISTRATOR.equals(role) || (ConstantInfo.ROLE_ADMIN.equals(role) && college.equals(teacher.getCollegeNo()))) {
            // 系统管理员有权限操作所有教师，或者管理员有权限操作本学院教师
            return Result.success(teacher);
        } else {
            // 其他角色无权限
            return Result.error(ConstantInfo.CODE_401, "权限不足");
        }
    }

    /**
     * 根据教师工号查询教师姓名
     * @param teacherNo 教师工号
     * @return 教师姓名
     */
    @GetMapping("/selectByTeacherNo/{teacherNo}")
    public Result selectByTeacherNo(@PathVariable String teacherNo) {
        QueryWrapper<Teacher> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teacher_no", teacherNo).select("teacher_name");
        Teacher teacher = teacherService.getOne(queryWrapper);
        if (teacher != null) {
            return Result.success("查找成功",teacher.getTeacherName());
        } else {
            return Result.error("未找到相关记录");
        }
    }

    /**
     * 根据学院编号查询教师列表
     * @param college 学院编号
     * @return 教师列表
     */
    @GetMapping("/selectByCollege/{college}")
    public Result selectByCollege(@PathVariable String college) {
        QueryWrapper<Teacher> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("college_no", college);
        return Result.success(teacherService.list(queryWrapper));
    }

    /**
     * 根据学院信息生成新的教师工号
     * @param requestData 请求数据，包含学院编号
     * @return 生成的教师工号
     */
    @PostMapping("/generateTeacherNo")
    public Result generateTeacherNo(@RequestBody Map<String, Object> requestData) {

        // 获取 form 中的参数
        String college = String.valueOf(requestData.get("college"));

        // 查询数据库中工号最后三位最大的数
        int maxLastTwoDigits = teacherService.getMaxLastThreeDigits(college);

        // 生成新的学号
        String teacherNo = college + String.format("%03d", maxLastTwoDigits + 1);

        // 返回生成的工号到前端
        return Result.success("工号生成成功！",teacherNo);
    }

    /**
     * 分页查询教师信息
     * 管理员仅能查询本学院教师，系统管理员查询所有
     * @param pageNum 当前页码
     * @param pageSize 页面大小
     * @param teacherNo 教师工号（可选）
     * @param teacherName 教师姓名（可选）
     * @param college 学院编号（可选）
     * @return 分页结果
     */
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum, @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String teacherNo,
                           @RequestParam(defaultValue = "") String teacherName,
                           @RequestParam(defaultValue = "") String college) {
        UserDTO userInfo = userService.getUserRoleAndCollege(StpUtil.getLoginIdAsString());

        String role = userInfo.getRole();
        // 对管理员进行权限控制
        if (ConstantInfo.ROLE_ADMIN.equals(role)) {
            // 如果是 ROLE_ADMIN，使用当前用户的学院信息
            college = userInfo.getCollege();
        }
        return Result.success(teacherService.findPage(new Page<>(pageNum, pageSize), teacherNo, teacherName, college));
    }

    @GetMapping("/export")
    public void export(HttpServletResponse response,
                         @RequestParam(defaultValue = "") String college,
                         @RequestParam(defaultValue = "") String teacherNo,
                         @RequestParam(defaultValue = "") String teacherName) throws IOException {
            UserDTO userInfo = userService.getUserRoleAndCollege(StpUtil.getLoginIdAsString());
            String role = userInfo.getRole();
            // 对管理员进行权限控制
            if (ConstantInfo.ROLE_ADMIN.equals(role)) {
                // 如果是 ROLE_ADMIN，使用当前用户的学院信息
                college = userInfo.getCollege();
            }

        try {  // 从数据库查询出所有的数据
            List<TeacherDTO> list = teacherService.export(college, teacherNo, teacherName);

            // 设置响应头信息
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");

            // 生成导出文件名
            String fileName = generateExportFileName("教师信息");

            // 设置响应头中的Content-disposition，指定导出文件名
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setHeader("Access-Control-Expose-Headers","Content-Disposition");
            // 使用EasyExcel进行数据导出
            EasyExcel.write(response.getOutputStream(), TeacherDTO.class).sheet("教师信息").doWrite(list);

        } catch (Exception e) {
            // 异常处理，抛出异常
            throw new RuntimeException("导出数据失败：" + e.getMessage());
        }
    }
    /**
     * 生成导出文件名
     */
    private String generateExportFileName(String prefix) throws UnsupportedEncodingException {
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String fileName = prefix + "_" + timestamp;
        // 使用URLEncoder编码文件名，防止中文乱码
        return URLEncoder.encode(fileName, "UTF-8");
    }

    //导入Excel
    @RequestMapping("/import")
    @ResponseBody
    public Result importExcel(/*@RequestParam("excelFile")*/ MultipartFile file) {
        // 参数验证
        if (file == null || file.isEmpty()) {
            return Result.error(ConstantInfo.CODE_400, "上传的文件不能为空");
        }
        try {
           TeacherExcelListener listener=new TeacherExcelListener(teacherService);
            // 使用 EasyExcel 读取 Excel 数据，并交给 TeacherExcelListener 处理
            // 获取成功导入的数据条数
            int importedCount = listener.getImportedCount();
            return Result.success("导入成功，共导入 " + importedCount + " 条数据");
        } catch (Exception e) {
            // 异常处理，返回错误信息
            return Result.error(ConstantInfo.CODE_500, "导入失败：" + e.getMessage());
        }
    }

}
