package com.acss.springbootinit.service.impl;

import com.acss.springbootinit.mapper.NotificationMapper;
import com.acss.springbootinit.model.entity.Notification;
import com.acss.springbootinit.service.INotificationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Jack Chen
 * @since 2023-05-20
 */
@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements INotificationService {

}
