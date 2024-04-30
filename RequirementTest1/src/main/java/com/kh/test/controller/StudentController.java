package com.kh.test.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kh.test.model.dto.Student;
import com.kh.test.model.service.StudentService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("")
public class StudentController {
	private final StudentService service;
	@ResponseBody
	@GetMapping("select")
	public List<Student> select(){
		return service.select();
	}
	
}
