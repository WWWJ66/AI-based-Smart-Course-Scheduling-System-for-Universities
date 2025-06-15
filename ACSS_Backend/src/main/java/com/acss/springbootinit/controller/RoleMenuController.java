/**
 * @ClassName RoleMenuController
 * @Description [处理角色菜单的请求]
 */
package com.acss.springbootinit.controller;


import com.acss.springbootinit.common.Result;
import com.acss.springbootinit.model.entity.RoleMenu;
import com.acss.springbootinit.service.IRoleMenuService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


@RestController
@RequestMapping("/role-menu")
public class RoleMenuController {

    @Resource
    private IRoleMenuService roleMenuService;

    /**
     * 新增或更新角色菜单关系
     *
     * @param roleMenu 角色菜单实体
     * @return 统一返回结果
     */
    @PostMapping
    public Result save(@RequestBody RoleMenu roleMenu) {
        roleMenuService.saveOrUpdate(roleMenu);
            return Result.success();
    }

    /**
     * 根据id删除角色菜单关系
     *
     * @param id 主键id
     * @return 统一返回结果
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        roleMenuService.removeById(id);
            return Result.success();
    }

    /**
     * 批量删除角色菜单关系
     *
     * @param ids id列表
     * @return 统一返回结果
     */
    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {//批量删除
        roleMenuService.removeByIds(ids);
            return Result.success();
    }

    /**
     * 查询所有角色菜单关系
     *
     * @return 角色菜单列表
     */
    @GetMapping
    public Result findAll() {
            return Result.success(roleMenuService.list());
            }

    /**
     * 根据id查询角色菜单关系
     *
     * @param id 主键id
     * @return 单条记录
     */
    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
            return Result.success(roleMenuService.getById(id));
        }

    /**
     * 分页查询角色菜单关系
     *
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 分页数据
     */
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum, @RequestParam Integer pageSize){
            QueryWrapper<RoleMenu> queryWrapper=new QueryWrapper<>();
            queryWrapper.orderByDesc("id");
            return Result.success(roleMenuService.page(new Page<>(pageNum,pageSize),queryWrapper));
    }
}
