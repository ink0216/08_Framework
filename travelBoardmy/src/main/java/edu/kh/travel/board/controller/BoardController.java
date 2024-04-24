package edu.kh.travel.board.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.kh.travel.board.model.service.BoardService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("board")
public class BoardController {
	
	private final BoardService service;
//	@GetMapping("{boardCode:[0-9]+}/{boardNo:[0-9]+}") // /board/1/1998?cp=1 이런 식으로 요청 온다(상세 조회 요청 주소 모양)
//	@GetMapping("/{selectContinent:[A-Z]{2}}")
	@GetMapping("{selectContinent:[A-Z]{2}}")
	public String afterLogin(
			@PathVariable("selectContinent") String selectContinent
			) {
		return null;
	}
}
	
