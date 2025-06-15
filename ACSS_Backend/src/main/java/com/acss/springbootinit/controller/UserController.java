/**
 * @ClassName UserController
 * @Description [处理用户相关的请求，如登录、登出、用户信息查询及管理]
 */
package com.acss.springbootinit.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.acss.springbootinit.common.Result;
import com.acss.springbootinit.constant.ConstantInfo;
import com.acss.springbootinit.model.entity.User;
import com.acss.springbootinit.service.IUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private IUserService userService;

    /**
     * 用户登录接口
     *
     * @param user 包含用户账号和密码的User实体
     * @return 登录成功返回用户信息，失败返回错误信息
     */
    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        String userNo = user.getUserNo();
        String password = user.getPassword();
        if (StrUtil.isBlank(userNo) || StrUtil.isBlank(password)) {
            return Result.error(ConstantInfo.CODE_400, "参数错误");
        }
        User userLogin = userService.login(user);
        return Result.success(userLogin);
    }

    /**
     * 用户登出接口
     *
     * @return 登出成功提示
     */
    @GetMapping("/logout")
    public Result logout(){
        // 当前会话注销登录
        StpUtil.logout();
        return Result.success();
    }

    /**
     * 新增或更新用户信息（包括修改电话号码）
     *
     * @param user 用户实体
     * @return 操作结果
     */
    @PostMapping
    public Result save(@RequestBody User user) {
        userService.saveOrUpdate(user);
        if(ConstantInfo.ROLE_STUDENT.equals(user.getRole())){
            userService.editStudentTelephone(user.getUserNo(),user.getTelephone());
        }else{
            userService.editTeacherTelephone(user.getUserNo(),user.getTelephone());
        }
        return Result.success();
    }

    /**
     * 通过用户账号查询用户信息
     *
     * @param userNo 用户账号
     * @return 用户实体，包含电话号码
     */
    @GetMapping("/userNo/{userNo}")    //通过账号查询
    public Result findByUsername(@PathVariable String userNo) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_no", userNo);
        User user = userService.getOne(queryWrapper);
        user.setTelephone(userService.getUserTelephone(userNo));
        return Result.success(user);
    }

    /**
     * 修改用户密码
     *
     * @param user 包含账号和新密码的用户实体
     * @return 操作结果
     */
    @PostMapping("/password")
    public Result password(@RequestBody User user) {
        userService.updatePassword(user);
        return Result.success();
    }

    /**
     * 分页查询用户列表（仅管理员可用）
     *
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param role 角色（可选，精确查询）
     * @param userNo 用户账号（可选，模糊查询）
     * @return 分页后的用户列表
     */
    @GetMapping("/page")
    @SaCheckRole("ROLE_ADMINISTRATOR")
    public Result findPage(@RequestParam Integer pageNum, @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String role, @RequestParam(defaultValue = "") String userNo) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // 精确查询角色
        if (!StringUtils.isEmpty(role)) {
            queryWrapper.eq("role", role);
        }
        // 模糊查询用户账号
        if (!StringUtils.isEmpty(userNo)) {
            queryWrapper.like("user_no", userNo);
        }
        queryWrapper.orderByAsc("id");
        return Result.success(userService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

    /**
     * 获取用户所属学院
     *
     * @param userNo 用户账号
     * @return 学院名称
     */
    @GetMapping("/getUserCollege/{userNo}")
    public Result getUserCollege(@PathVariable String userNo) {
        return Result.success(userService.getUserCollege(userNo));
    }

    /**
     * 修改用户角色（仅管理员可用）
     *
     * @param id 用户ID
     * @param role 新角色
     * @return 操作结果
     */
    @GetMapping("/editUserRole")
    @SaCheckRole("ROLE_ADMINISTRATOR")
    public Result editUserRole(@RequestParam Integer id,
                               @RequestParam String role) {
        return Result.success(userService.editUserRole(id, role));
    }

    /**
     * 重置用户密码为默认密码（仅管理员可用）
     *
     * @param userNo 用户账号
     * @return 操作结果
     */
    @GetMapping("/resetPassword/{userNo}")
    @SaCheckRole("ROLE_ADMINISTRATOR")
    public Result resetPassword(@PathVariable String userNo) {
        return Result.success(userService.resetPassword(userNo));
    }

    /**
     * 获取用户角色和所属学院信息
     *
     * @param userNo 用户账号
     * @return 角色和学院信息
     */
    @GetMapping("/getUserRoleAndCollege/{userNo}")
    public Result getUserRoleAndCollege(@PathVariable String userNo) {
        return Result.success(userService.getUserRoleAndCollege(userNo));
    }

}