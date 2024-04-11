package com.lik.book.main.controller;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
	@RequestMapping("/") //method 가리지 않음
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
			Book book, 
			//@RequestParam은 하나씩 얻어올 때 사용하고, 
			//DTO로 여러 개를 한 번에 받아올 때에는 @ModelAttribute를 쓰는데, 
			//생략 가능하다
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
	/**검색 페이지로 포워드
	 * @return
	 */
	@GetMapping("actions")
	public String actions() {
		return "/common/actions";
	}
	/**책 검색
	 * @return
	 */
	@GetMapping("search")
	@ResponseBody
	public List<Book> search(
			@RequestParam("keyword") String keyword
			) {
		List<Book> bookList = service.search(keyword);
		return bookList;
	}
	/**가격 수정
	 * @return
	 */
	@PutMapping("edit")
	@ResponseBody
	public int edit(
			@RequestBody Map<String, Object> map
			//js에서 body에 두 개 이상 담아서 보낼 때에는 js객체고 묶고
			//그걸 json으로 Stringify 해서 한 번에 보내야 하고
			//컨트롤러에서는 맵으로 받으면 자동으로 변환된다
			) {
		return service.edit(map);
	}
	/**삭제하기
	 * @return
	 */
	@DeleteMapping("delete")
	@ResponseBody
	public int delete(
			@RequestBody int bookNo
			//body에는 항상 하나만 받을 수 있기 때문에
			//어노테이션 뒤에 key안쓴다!!
			) {
		return service.delete(bookNo);
	}
}
