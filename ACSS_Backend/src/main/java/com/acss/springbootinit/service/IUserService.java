package com.acss.springbootinit.service;

import com.acss.springbootinit.model.dto.user.UserDTO;

import com.baomidou.mybatisplus.extension.service.IService;

import com.acss.springbootinit.model.entity.User;



/**
 * 用户服务
 */
public interface IUserService extends IService<User> {

    User login(User user);

    void updatePassword(User user);

    String getUserCollege(String userNo);

    int editUserRole(Integer id, String role);

    int resetPassword(String userNo);

    UserDTO getUserRoleAndCollege(String userNo);

    String getUserTelephone(String userNo);

    int editStudentTelephone(String userNo, String telephone);

    int editTeacherTelephone(String userNo, String telephone);
}
