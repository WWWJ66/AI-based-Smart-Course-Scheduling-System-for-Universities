package com.acss.springbootinit.mapper;

import com.acss.springbootinit.model.entity.Role;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


public interface RoleMapper extends BaseMapper<Role> {
    //查询用户角色
    @Select("select id from sys_role where flag=#{flag}")
    Integer selectByFlag(@Param("flag") String role);
}
