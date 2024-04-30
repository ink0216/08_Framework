package com.kh.test.model.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.kh.test.model.dto.Student;

@Mapper
public interface StudentMapper {

	List<Student> select();


}
