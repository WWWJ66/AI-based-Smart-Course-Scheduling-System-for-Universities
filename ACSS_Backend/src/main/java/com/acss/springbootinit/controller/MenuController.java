/**
 * @ClassName MenuController
 * @Description 菜单管理控制器，提供菜单的增删改查、分页、批量删除和图标获取接口
 */
package com.acss.springbootinit.controller;


import com.acss.springbootinit.common.Result;
import com.acss.springbootinit.constant.ConstantInfo;
import com.acss.springbootinit.mapper.DictMapper;
import com.acss.springbootinit.model.entity.Dict;
import com.acss.springbootinit.model.entity.Menu;
import com.acss.springbootinit.service.IMenuService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;



@RestController
@RequestMapping("/menu")
public class MenuController {

    @Resource
    private IMenuService menuService;

    @Resource
    private DictMapper dictMapper;

    /**
     * 新增或更新菜单
     *
     * @param menu 菜单实体
     * @return 操作结果
     */
    @PostMapping
    public Result save(@RequestBody Menu menu) {
        menuService.saveOrUpdate(menu);
        return Result.success();
    }

    /**
     * 根据ID删除菜单
     *
     * @param id 菜单ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        menuService.removeById(id);
        return Result.success();
    }

    /**
     * 获取所有菜单的ID列表
     *
     * @return 所有菜单的ID集合
     */
    @GetMapping("/ids")
    public Result findAllIds() {
        return Result.success(menuService.list().stream().map(Menu::getId));
    }

    /**
     * 批量删除菜单
     *
     * @param ids 菜单ID列表
     * @return 操作结果
     */
    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {//批量删除
        menuService.removeByIds(ids);
        return Result.success();
    }

    /**
     * 根据菜单名称查询所有菜单（模糊匹配）
     *
     * @param name 菜单名称（可选，默认为空）
     * @return 菜单列表
     */
    @GetMapping
    public Result findAll(@RequestParam(defaultValue = "") String name) {
        return Result.success(menuService.findMenus(name));
    }

    /**
     * 根据ID查询菜单详情
     *
     * @param id 菜单ID
     * @return 菜单信息
     */
    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(menuService.getById(id));
    }

    /**
     * 分页查询菜单信息
     *
     * @param name     菜单名称（用于模糊查询）
     * @param pageNum  当前页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    @GetMapping("/page")
    public Result findPage(@RequestParam String name, @RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("name", name);
        queryWrapper.orderByDesc("id");
        return Result.success(menuService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

    /**
     * 获取图标列表（从字典中查询 type 为 ICON 的数据）
     *
     * @return 图标数据列表
     */
    @GetMapping("/icons")
    public Result getIcons() {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", ConstantInfo.DICT_TYPE_ICON);
        return Result.success(dictMapper.selectList(queryWrapper));
    }
}
