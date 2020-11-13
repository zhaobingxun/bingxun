package com.itheima.service;

import com.itheima.domain.User;

public interface UserService {

	public User findByName(String name);
	
	public User findById(Integer id);
}
