package edu.kh.project.board.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.kh.project.board.model.dto.Comment;
import edu.kh.project.board.model.service.CommentService;
import lombok.RequiredArgsConstructor;
//@RestController : REST API 구축을 위해 사용하는 컨트롤러
//@RestController : @Controller(요청/응답 제어 + bean 등록)  
//					+ @ResponseBody(응답 본문으로 데이터만 반환)
//	=>이 컨트롤러 내의 모든 응답을 응답 본문(ajax)으로 반환하는 컨트롤러
// REST는 요청이 오면 데이터만 돌려보내줌
//	->RestController쓰면 메서드 위에 다 @ResponseBody안써도 된다!
@RestController
@RequiredArgsConstructor
@RequestMapping("comment")
public class CommentController {
	private final CommentService service;
	//이 컨트롤러 내 메서드가 다 @ResponseBody일때에는 
	// @Controller
	//value 속성 : 매핑할 주소
	//produces 속성 : 응답할 데이터의 형식을 지정하는 속성
	/**댓글 조회
	 * @param boardNo
	 * @return commentList
	 */
	@GetMapping(value="", produces = "application/json") //응답 이 상태로 만들어서 보낼거야(혹시 JSON으로 안바꿔서 보낼까봐)
	//@ResponseBody //비동기니까 값만 그대로 보냄
	public List<Comment> select(
			@RequestParam("boardNo") int boardNo
			){
		//자바의 List는 JS에서 사용 불가
		// HttpMessageConverter가 List를 JSON(문자열)로 변환해서 응답해준다
		// 근데 가끔 동작을 안할 떄가 있어서
		// 확실하게 명시하는 방법
		return service.select(boardNo); 
	}
	
	/**댓글 등록
	 * @return result
	 */
	@PostMapping("")
	public int insert(
	//@RequestParam : 파라미터  
	//@RequestMapping : 요청 주소 매핑
	//@RequestBody : 비동기 요청 시 body에 담겨져서 오는 것 받기
	@RequestBody Comment comment 
	//JSON이면 String인데
	//Comment DTO를 쓰면
	//요청 데이터가 JSON 데이터로 명시됨({"Content-Type" : "application/json"})
	
	//ArgumentsResolver가 JSON을 DTO로 자동 변경해준다 : 얘는 받아올 때 작동하는 것!!
	//	(내장돼있는 JACKSON 라이브러리가 기능함)
	// HttpMessageConverter : 얘는 내보낼 때 작동하는 것!!
			) {
		return service.insert(comment);
	}
	
	/**댓글 수정
	 * @param comment(번호, 내용 담겨있다)
	 * @return result
	 */
	@PutMapping("")
	public int update(
			//두 개 받아오니까 Map으로 받아도 되고
			//Comment DTO로 받아도 된다
			@RequestBody Comment comment
			) {
		return service.update(comment);
	}
	
	/**댓글 삭제
	 * @return result
	 */
	@DeleteMapping("")
	public int delete(
			@RequestBody int commentNo
			//안전하게 하고 싶으면 세션에서 회원 번호도 받아오면 된다
			) {
		return service.delete(commentNo);
	}
	
	
	
}
