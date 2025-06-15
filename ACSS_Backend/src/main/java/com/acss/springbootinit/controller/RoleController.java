/**
 * @ClassName RoleController
 * @Description [处理角色信息相关的请求]
 */
package com.acss.springbootinit.controller;


import com.acss.springbootinit.common.Result;
import com.acss.springbootinit.model.entity.Role;
import com.acss.springbootinit.service.IRoleService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


@RestController
@RequestMapping("/role")
public class RoleController {

    @Resource
    private IRoleService roleService;

    /**
     * 新增或更新角色信息
     *
     * @param role 请求体，包含角色实体信息
     * @return 返回操作结果
     */
    @PostMapping
    public Result save(@RequestBody Role role) {
        roleService.saveOrUpdate(role);
        return Result.success();
    }

    /**
     * 删除指定id的角色
     *
     * @param id 角色id
     * @return 返回操作结果
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        roleService.removeById(id);
        return Result.success();
    }

    /**
     * 批量删除角色
     *
     * @param ids 角色id列表
     * @return 返回操作结果
     */
    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {//批量删除
        roleService.removeByIds(ids);
        return Result.success();
    }

    /**
     * 查询所有角色
     *
     * @return 返回角色列表
     */
    @GetMapping
    public Result findAll() {
        return Result.success(roleService.list());
    }

    /**
     * 根据id查询单个角色
     *
     * @param id 角色id
     * @return 返回对应角色信息
     */
    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(roleService.getById(id));
    }

    /**
     * 分页查询角色列表，支持根据名称模糊搜索
     *
     * @param name 角色名称（模糊查询）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页角色列表
     */
    @GetMapping("/page")
    public Result findPage(@RequestParam String name,
                           @RequestParam Integer pageNum, @RequestParam Integer pageSize){
        QueryWrapper<Role> queryWrapper=new QueryWrapper<>();
        queryWrapper.like("name",name);
        queryWrapper.orderByDesc("id");
        return Result.success(roleService.page(new Page<>(pageNum,pageSize),queryWrapper));
    }

    /**
     * 绑定角色与菜单的关系
     *
     * @param roleId 角色id
     * @param menuIds 菜单id列表
     * @return 返回操作结果
     */
    @PostMapping("/roleMenu/{roleId}")
    public Result save(@PathVariable Integer roleId,@RequestBody List<Integer> menuIds) {
        roleService.saveRoleMenu(roleId,menuIds);
        return Result.success();
    }

    /**
     * 查询某个角色对应的菜单列表
     *
     * @param roleId 角色id
     * @return 返回菜单id列表
     */
    @GetMapping("/roleMenu/{roleId}")
    public Result getRoleMenu(@PathVariable Integer roleId) {
        return Result.success(roleService.getRoleMenu(roleId));
    }
}
