/**
 * @ClassName StudentController
 * @Description [学生信息相关的控制器，提供学生信息的增删改查、导出等接口]
 */
package com.acss.springbootinit.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.acss.springbootinit.common.Result;
import com.acss.springbootinit.constant.ConstantInfo;
import com.acss.springbootinit.model.dto.course.StudentDTO;
import com.acss.springbootinit.model.dto.user.UserDTO;
import com.acss.springbootinit.model.entity.Student;
import com.acss.springbootinit.service.IStudentService;
import com.acss.springbootinit.service.IUserService;
import com.acss.springbootinit.utils.StudentExcelListener;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

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
@RequestMapping("/student")
public class StudentController {

    @Resource
    private IStudentService studentService;

    @Resource
    private IUserService userService;

    /**
     * 新增或更新学生信息接口
     * 只有系统管理员或对应学院管理员有权限操作
     *
     * @param student 学生实体
     * @return 操作结果
     */
    @PostMapping
    public Result save(@RequestBody Student student) {
        UserDTO userInfo = userService.getUserRoleAndCollege(StpUtil.getLoginIdAsString());
        String role = userInfo.getRole();
        String college = userInfo.getCollege();
        if (ConstantInfo.ROLE_ADMINISTRATOR.equals(role) || (ConstantInfo.ROLE_ADMIN.equals(role) && college.equals(student.getCollegeNo()))) {
            // 系统管理员有权限操作所有学生，或者管理员有权限操作本学院学生

            saveStudentData(student);
            return Result.success();
        } else {
            // 其他角色无权限
            return Result.error(ConstantInfo.CODE_401, "权限不足");
        }
    }

    /**
     * 内部方法：保存学生数据
     * 包含新增学生时创建对应用户和更新班级学生人数
     *
     * @param student 学生实体
     */
    private void saveStudentData(Student student) {
        try {
            // 若为新增学生，则添加用户数据
            if (student.getId() == null) {
                studentService.addStudentUser(student);
                //更新对应班级人数
                studentService.updateClassStudentNumber(student.getClassNo(),1);
            }
            // 保存或更新学生信息
            studentService.saveOrUpdate(student);
        } catch (Exception e) {
            // 异常处理，返回错误信息
            throw new RuntimeException("保存学生数据失败：" + e.getMessage());
        }
    }

    /**
     * 删除学生接口
     * 只有系统管理员或对应学院管理员有权限操作
     *
     * @param id 学生ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        UserDTO userInfo = userService.getUserRoleAndCollege(StpUtil.getLoginIdAsString());
        String role = userInfo.getRole();
        String college = userInfo.getCollege();

        Student student = studentService.getById(id);
        if (student == null) {
            return Result.error(ConstantInfo.CODE_404, "学生不存在");
        }
        if (ConstantInfo.ROLE_ADMINISTRATOR.equals(role) || (ConstantInfo.ROLE_ADMIN.equals(role) && college.equals(student.getCollegeNo()))) {
            // 系统管理员有权限操作所有学生，或者管理员有权限操作本学院学生
            deleteStudentData(id);
            //修改对应班级人数
            studentService.updateClassStudentNumber(student.getClassNo(),-1);
            return Result.success();
        } else {
            // 其他角色无权限
            return Result.error(ConstantInfo.CODE_401, "权限不足");
        }
    }

    /**
     * 内部方法：删除学生相关数据
     * 包含删除学生对应的用户账号和学生实体
     *
     * @param id 学生ID
     */
    private void deleteStudentData(Integer id) {
        studentService.deleteStudentUser(id);
        studentService.removeById(id);
    }

    /**
     * 批量删除学生接口
     * 系统管理员可删除所有学生，管理员可删除本学院学生
     *
     * @param ids 学生ID列表
     * @return 操作结果
     */
    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) { //批量删除
        UserDTO userInfo = userService.getUserRoleAndCollege(StpUtil.getLoginIdAsString());
        String role = userInfo.getRole();
        String college = userInfo.getCollege();

        if (ConstantInfo.ROLE_ADMINISTRATOR.equals(role)) {
            // 系统管理员有权限操作所有学生
            if (!ids.isEmpty()) {
                for (int id : ids) {
                    Student student = studentService.getById(id);
                    studentService.deleteStudentUser(id);     //先删用户，后删学生
                    //修改对应班级人数
                    studentService.updateClassStudentNumber(student.getClassNo(),-1);
                }
                studentService.removeByIds(ids);
            }
            return Result.success();
        } else if (ConstantInfo.ROLE_ADMIN.equals(role)) {
            // 管理员有权限操作本学院学生
            if (!ids.isEmpty()) {
                for (int id : ids) {
                    Student student = studentService.getById(id);
                    if (student != null && college.equals(student.getCollegeNo())) {
                        studentService.deleteStudentUser(id);
                        studentService.removeById(id);
                        //修改对应班级人数
                        studentService.updateClassStudentNumber(student.getClassNo(),-1);
                    }
                }
            }
            return Result.success();
        } else {
            // 其他角色无权限
            return Result.error(ConstantInfo.CODE_401, "权限不足");
        }
    }


    /**
     * 根据学生ID查询学生信息接口（编辑用）
     * 只有系统管理员或对应学院管理员有权限访问
     *
     * @param id 学生ID
     * @return 学生信息或错误信息
     */
    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        UserDTO userInfo = userService.getUserRoleAndCollege(StpUtil.getLoginIdAsString());
        String role = userInfo.getRole();
        String college = userInfo.getCollege();

        Student student = studentService.getById(id);
        if (student == null) {
            return Result.error(ConstantInfo.CODE_404, "学生不存在");
        }
        if (ConstantInfo.ROLE_ADMINISTRATOR.equals(role) ||(ConstantInfo.ROLE_ADMIN.equals(role) && college.equals(student.getCollegeNo()))) {
            // 系统管理员有权限操作所有学生，或者管理员有权限操作本学院学生
            return Result.success(student);
        } else {
            // 其他角色无权限
            return Result.error(ConstantInfo.CODE_401, "权限不足");
        }
    }

    /**
     * 根据学生学号查询所属班级号（学生角色专用）
     *
     * @param studentNo 学生学号
     * @return 班级号或错误信息
     */
    @GetMapping("/getClassNoByStudentNo/{studentNo}")
    public Result getClassNoByStudentNo(@PathVariable String studentNo) {
        UserDTO userInfo = userService.getUserRoleAndCollege(StpUtil.getLoginIdAsString());
        String role = userInfo.getRole();
        if (ConstantInfo.ROLE_STUDENT.equals(role) ) {
            QueryWrapper queryWrapper=new QueryWrapper();
            queryWrapper.eq("student_no",studentNo);
            Student student = studentService.getOne(queryWrapper);
            if (student != null) {
                String classNo = student.getClassNo();
                return Result.success("查询成功",classNo);
            }
            return Result.error(ConstantInfo.CODE_404, "班级信息查询失败");
        } else {
            // 其他角色无权限
            return Result.error(ConstantInfo.CODE_401, "权限不足");
        }
    }

    /**
     * 分页查询学生信息接口
     * 根据用户角色限制可访问的数据范围
     *
     * @param pageNum 当前页码
     * @param pageSize 每页条数
     * @param college 学院筛选
     * @param grade 年级筛选
     * @param classNo 班级筛选
     * @param studentNo 学号筛选
     * @param studentName 姓名筛选
     * @param term 学期筛选
     * @return 分页查询结果
     */
    @GetMapping("/page")
    public Result findPage(
            @RequestParam Integer pageNum, @RequestParam Integer pageSize,
            @RequestParam(defaultValue = "") String college,
            @RequestParam(defaultValue = "") String grade,
            @RequestParam(defaultValue = "") String classNo,
            @RequestParam(defaultValue = "") String studentNo,
            @RequestParam(defaultValue = "") String studentName,
            @RequestParam(defaultValue = "") String term){
        //如果是 ROLE_ADMINISTRATOR，则无论 college 是什么都能访问所有数据。如果是 ROLE_ADMIN，则只能访问当前用户所在学院的数据。
        // 获取当前用户的角色信息
        UserDTO userInfo = userService.getUserRoleAndCollege(StpUtil.getLoginIdAsString());
        String role = userInfo.getRole();
        // 判断权限
        if (ConstantInfo.ROLE_ADMINISTRATOR.equals(role)) {
            // 如果是系统管理员，使用当前用户的学院信息
            return Result.success(studentService.findPage(new Page<>(pageNum, pageSize), college, grade, classNo, studentNo, studentName));

        } else if (ConstantInfo.ROLE_ADMIN.equals(role)) {
            // 如果是管理员，只能访问当前用户所在学院的数据
            college = userInfo.getCollege();
            return Result.success(studentService.findPage(new Page<>(pageNum, pageSize), college, grade, classNo, studentNo, studentName));
        } else if (ConstantInfo.ROLE_TEACHER.equals(role)) {
            return Result.success(studentService.findPageByTeacher(new Page<>(pageNum, pageSize),term,StpUtil.getLoginIdAsString(), classNo,studentNo, studentName));
        } else {
            // 其他角色没有权限
            return Result.error(ConstantInfo.CODE_401, "权限不足");
        }
    }

    /**
     * 生成学号
     *
     * @param requestData 请求数据
     * @return 生成的学号
     */
    @PostMapping("/generateStudentNo")
    public Result generateStudentNo(@RequestBody Map<String, Object> requestData) {
        // 获取 form 中的参数
        String classNo = String.valueOf(requestData.get("classNo"));

        // 查询数据库中学号最后两位最大的数
        int maxLastTwoDigits = studentService.getMaxLastTwoDigits(classNo);

        // 生成新的学号
        String studentNo = classNo + String.format("%02d", maxLastTwoDigits + 1);

        // 返回生成的学号到前端
        return Result.success("学号生成成功！",studentNo);
    }

    /**
     * 导出数据
     *
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response,
                       @RequestParam(defaultValue = "") String college,
                       @RequestParam(defaultValue = "") String grade,
                       @RequestParam(defaultValue = "") String classNo,
                       @RequestParam(defaultValue = "") String studentNo,
                       @RequestParam(defaultValue = "") String studentName,
                       @RequestParam(defaultValue = "") String term) throws IOException {
        UserDTO userInfo = userService.getUserRoleAndCollege(StpUtil.getLoginIdAsString());
        String role = userInfo.getRole();
        if (ConstantInfo.ROLE_ADMIN.equals(role)) {
            // 管理员只能导出本学院的数据
            college = userInfo.getCollege();
        }
        try {
            List<StudentDTO> list;
            // 根据角色调用不同的导出方法
            if (ConstantInfo.ROLE_TEACHER.equals(role)) {
                list=studentService.exportByTeacher(term,StpUtil.getLoginIdAsString(), classNo,studentNo, studentName);
            } else {
                // 从数据库查询出所有的数据
                list= studentService.export(college, grade, classNo, studentNo, studentName);
            }

            // 生成导出文件名
            String fileName = generateExportFileName("学生信息");
            // 设置响应头信息
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            // 设置响应头中的Content-disposition，指定导出文件名，并使用UTF-8编码
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setHeader("Access-Control-Expose-Headers","Content-Disposition");

            // 使用EasyExcel进行数据导出
            EasyExcel.write(response.getOutputStream(), StudentDTO.class).sheet("学生信息").doWrite(list);

        } catch (Exception e) {
            // 异常处理，抛出异常
            throw new RuntimeException("导出数据失败：" + e.getMessage());
        }
    }


    /**
     * 生成导出文件名
     */
    private String generateExportFileName(String prefix) throws UnsupportedEncodingException {
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());   //年月日时分秒
        String fileName = prefix + "_" + timestamp;
        // 使用URLEncoder编码文件名，防止中文乱码
        return  URLEncoder.encode(fileName, "UTF-8");
    }

    /**
     * 导入数据
     *
     * @param file 请求数据
     */
    @RequestMapping("/import")
    @ResponseBody
    public Result importExcel(/*@RequestParam("excelFile")*/ MultipartFile file){
        // 参数验证
        if (file == null || file.isEmpty()) {
            return Result.error(ConstantInfo.CODE_400, "上传的文件不能为空");
        }
        try {
            // 使用 EasyExcel 读取 Excel 数据，并交给 StudentExcelListener 处理
            StudentExcelListener listener = new StudentExcelListener(studentService);
            EasyExcel.read(file.getInputStream(), StudentDTO.class,listener).sheet().doRead();
            // 获取成功导入的数据条数
            int importedCount = listener.getImportedCount();
            return Result.success("导入成功，共导入 " + importedCount + " 条数据");
        } catch (Exception e) {
            // 异常处理，返回错误信息
            return Result.error(ConstantInfo.CODE_500, "导入失败：" + e.getMessage());
        }
    }

}