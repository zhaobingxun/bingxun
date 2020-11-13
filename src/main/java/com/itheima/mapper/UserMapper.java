package com.itheima.mapper;

import com.itheima.domain.User;

public interface UserMapper {

	public User findByName(String name);
	
	public User findById(Integer id);
}
