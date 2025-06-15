/**
 * @ClassName DictController
 * @Description [处理数据字典相关的请求，支持增删改查等操作，仅限管理员访问]
 */
package com.acss.springbootinit.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.acss.springbootinit.common.Result;
import com.acss.springbootinit.model.entity.Dict;
import com.acss.springbootinit.service.IDictService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


@RestController
@RequestMapping("/dict")
public class DictController {

    @Resource
    private IDictService dictService;

    /**
     * 新增或更新数据字典
     *
     * @param dict 请求体，包含字典项的各项信息
     * @return 返回操作结果
     */
    @SaCheckRole("ROLE_ADMINISTRATOR")
    @PostMapping
    public Result save(@RequestBody Dict dict) {
        dictService.saveOrUpdate(dict);
        return Result.success();
    }

    /**
     * 删除指定ID的数据字典项
     *
     * @param id 字典项ID
     * @return 返回操作结果
     */
    @SaCheckRole("ROLE_ADMINISTRATOR")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        dictService.removeById(id);
        return Result.success();
    }

    /**
     * 批量删除字典项
     *
     * @param ids 字典项ID列表
     * @return 返回操作结果
     */
    @SaCheckRole("ROLE_ADMINISTRATOR")
    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {//批量删除
        dictService.removeByIds(ids);
        return Result.success();
    }

    /**
     * 查询所有数据字典项
     *
     * @return 返回所有字典项列表
     */
    @SaCheckRole("ROLE_ADMINISTRATOR")
    @GetMapping
    public Result findAll() {
        return Result.success(dictService.list());
    }

    /**
     * 获取字典类型列表（去重）
     *
     * @return 返回所有字典类型
     */
    @SaCheckRole("ROLE_ADMINISTRATOR")
    @GetMapping("/selectType")
    public Result selectType() {
        return Result.success(dictService.selectType());
    }

    /**
     * 根据字典类型查询字典项
     *
     * @param type 字典类型
     * @return 返回对应类型的字典项列表
     */
    @GetMapping("/{type}")
    public Result findType(@PathVariable String type) {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", type);
        return Result.success(dictService.list(queryWrapper));
    }

    /**
     * 分页查询数据字典项
     *
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @param type 字典类型（可选）
     * @return 返回分页查询结果
     */
    @SaCheckRole("ROLE_ADMINISTRATOR")
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum, @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String type) {
        return Result.success(dictService.findPage(new Page<>(pageNum, pageSize), type));
    }
}
