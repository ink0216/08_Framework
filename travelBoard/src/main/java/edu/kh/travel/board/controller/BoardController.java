package edu.kh.travel.board.controller;

import org.springframework.stereotype.Controller;

import edu.kh.travel.board.model.service.BoardService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class BoardController {
	private final BoardService service;
}
