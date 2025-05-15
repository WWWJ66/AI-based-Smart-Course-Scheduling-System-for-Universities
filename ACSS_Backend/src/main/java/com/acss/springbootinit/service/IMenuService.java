package com.acss.springbootinit.service;

import com.acss.springbootinit.model.entity.Menu;
import com.baomidou.mybatisplus.extension.service.IService;


import java.util.List;


public interface IMenuService extends IService<Menu> {

    List<Menu> findMenus(String name);
}
