package com.kh.test.board.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kh.test.board.model.dto.Board;
import com.kh.test.board.model.service.BoardService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class BoardController {
	private final BoardService service;
	
	/**제목으로 검색
	 * @return forward
	 */
	@GetMapping("search")
	public String search(
			@RequestParam("title") String title,
			Model model
			) {
		List<Board> boardList = service.search(title);
		int count = boardList.size();
		if(count>0) {
			model.addAttribute("boardList", boardList);
			return "searchSuccess";
		}
		return "searchFail";
				
	}
}
