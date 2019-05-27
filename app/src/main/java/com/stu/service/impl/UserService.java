package com.stu.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stu.dao.UserMapper;
import com.stu.pojo.User;
import com.stu.service.IUserService;

@Service
public class UserService implements IUserService {
	@Autowired
	UserMapper userMapper;
	
	public User findUserById(Integer id){
		User user = userMapper.findUserById(1);
//		return new User().setId(1);
		return user;
	}
}
