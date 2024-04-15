package com.kh.test.model.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.kh.test.model.dto.User;

@Mapper
public interface UserMapper {

	User select(int memberNo);

}
