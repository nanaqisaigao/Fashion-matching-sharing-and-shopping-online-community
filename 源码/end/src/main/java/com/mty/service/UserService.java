package com.mty.service;

import com.mty.entity.User;
import com.mty.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
/**
 * 用户表业务处理
 **/
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 分页查询
     */
    public List<User> queryAllByLimit(Map mp) {
        return userMapper.queryAllByLimit(mp);
    }

    /**
     * 查询所有
     */
    public List<User> queryCondition(User user) {
        return userMapper.queryCondition(user);
    }

    /**
     * 通过ID查询单条数据
     */
    public User queryById(Integer id) {
        return userMapper.queryById(id);
    }

    /**
     * 新增
     */
    public int insert(User user) {
        return userMapper.insert(user);
    }

    /**
     * 修改
     */
    public boolean update(User user) {
        return userMapper.update(user);
    }

    /**
     * 删除
     */
    public boolean deleteById(Integer id) {
        return userMapper.deleteById(id) > 0;
    }

}