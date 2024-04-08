package com.lik.book.main.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lik.book.main.model.dto.Book;
import com.lik.book.main.model.service.MainService;

import lombok.RequiredArgsConstructor;


@Controller
@RequiredArgsConstructor
public class MainController {
	private final MainService service;
	@RequestMapping("/")
	public String mainPage() {
		return "common/main";
	}
	
	
	@GetMapping("add")
	public String add() {
		return "common/add";
	}
	/**도서 삽입
	 * @param book
	 * @param ra
	 * @return
	 */
	@PostMapping("add")
	public String add(
			Book book, //@RequestParam은 하나씩 얻어올 때 사용하고, DTO로 여러 개를 한 번에 받아올 때에는 @ModelAttribute를 쓰는데, 생략 가능하다
			RedirectAttributes ra
			) {
		int result = service.add(book);
		String message=null;
		if(result>0) {
			message="삽입 성공!!!";
		}else {
			message="삽입 실패...";
		}
		ra.addFlashAttribute("message", message);
		return "redirect:/";
	}
	/**조회
	 * @return
	 */
	@GetMapping("selectAll")
	@ResponseBody
	public List<Book> selectAll(){
		return service.selectAll();
	}
	@GetMapping("actions")
	public String actions() {
		return "/common/";
	}
}
