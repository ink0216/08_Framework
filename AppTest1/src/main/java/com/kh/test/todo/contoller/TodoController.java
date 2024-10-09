package com.kh.test.todo.contoller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.kh.test.todo.model.dto.Todo;
import com.kh.test.todo.model.service.TodoService;

import lombok.RequiredArgsConstructor;

@Controller

@RequiredArgsConstructor

public class TodoController {

	private final TodoService service;

	@GetMapping("/")

	public String getTodoList(Model model) {

		List<Todo> todoList = service.getTodoList();

		model.addAttribute("todoList", todoList);

		return "todoList";

	}

}