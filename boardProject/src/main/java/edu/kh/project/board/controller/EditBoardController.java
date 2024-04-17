package edu.kh.project.board.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.board.model.service.BoardService;
import edu.kh.project.board.model.service.EditBoardService;
import edu.kh.project.member.model.dto.Member;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("editBoard")
// @RequestMapping("editBoard/**/fdsd")이런 식으로 패턴도 지정 가능
//중간에는 아무거나 들어가고 뒤엔 이렇게 끝나는 요청 받는다
@RequiredArgsConstructor
public class EditBoardController {
	private final EditBoardService service;
	private final BoardService boardService;
	/**게시글 작성 화면 전환
	 * @return board/boardWrite
	 */
	@GetMapping("{boardCode:[0-9]+}/insert")
	//근데 그 자리에는 숫자만 들어올 수 있는데 0-9 사이의 숫자 들어올 수 있는
	//한 칸 이상 있다(정규식)
	public String boardInsert( ///editBoard/${boardCode}/insert
			@PathVariable("boardCode") int boardCode
			//이렇게 하면 request scope에 자동 등록돼서
			//forward한 곳에서 이 값을 사용 가능하다
			) {
		//게시글 작성 화면으로 포워드
		return "board/boardWrite";
	}
	/**게시글 작성
	 * @param boardCode : 어떤 게시판에 작성한 글인지 구분하는 용도
	 * @param inputBoard(커맨드 객체(제출된 값이 DTO 필드에 담긴 객체)) : 입력된 값(제목,내용)+insert할 때 필요한 추가 데이터 저장
	 * @param loginMember : 로그인한 회원 번호 얻어오는 용도
	 * @param images : 제출된 file 타입 input태그 데이터들
	 * @param ra : 리다이렉트 시 request scope로 데이터 전달
	 * @return
	 * @throws IOException 
	 * @throws IllegalStateException 
	 */
	@PostMapping("{boardCode:[0-9]+}/insert")
	public String boardInsert(
			@PathVariable("boardCode") int boardCode,
			//제목과 내용을 따로 RequestParam으로 받아오지말고
			//이 데이터들이 서비스,매퍼,DB까지 갈거여서 Mybatis까지 갈건데 Mybatis에는 파라미터 한 개만 가능
			//어차피 묶어서 보내야 하니까
			//Map이나 DTO로 묶기
			/*@ModelAttribute*/ Board inputBoard,
			@SessionAttribute("loginMember") Member loginMember, //게시글을 누가 썼는지 알려고
			@RequestParam("images") List<MultipartFile> images, //제출되는 name값이 같으면 배열이나 리스트로 반환된다!!!
			//파일을 아무것도 선택 안한경우 이 리스트가 안만들어지는 게 아니라 빈칸으로 파일 개수대로 제출된다
			RedirectAttributes ra //게시글 작성 성공/실패 시 작성한 게시글의 상세조회로 리다이렉트 시 request scope로 데이터 전달용
			) throws IllegalStateException, IOException {
		//오버로딩 (한 클래스 내에서 같은 이름의 메서드 2개 이상 사용하려면 매개변수가 달라야 함)
		/*전달되는 파라미터 확인
		 * List<MultipartFile>
		 * - 파일이 5개 모두 업로드 시 => 0~4번 인덱스에 파일이 저장됨
		 * - 파일 5개 모두 업로드 안한 경우 => 0~4번 인덱스 모두에 파일이 저장 안됨(filename에 ""으로 들어감)
		 * - 2번 인덱스에만 파일 업로드 시 => 2번 인덱스에만 파일 저장됨
		 * 								+ 0,1,3,4번 인덱스에는 파일 저장 안됨
		 * [문제점]
		 * ==>제출한 파일 인덱스 뿐 아니라 
		 * 		파일이 선택되지 않은 input태그도 제출되고 있음!!
		 * 		(제출은 되었는데 데이터는 ""(빈칸)임!)
		 * 		->파일 선택이 안된 input태그 값을 서버에 저장하려고 하면 오류가 발생할 것이다!
		 * 
		 * [해결방법]
		 * - 무작정 서버에 저장하면 안되고!!
		 *  -> 제출된 파일이 있는 지 확인하는 로직을 추가 구성해야 한다!
		 *  	
		 *  + List 요소의 index 번호 == IMG_ORDER와 같음
		 * */
		
		// 1. boardCode, 로그인한 회원 번호를 inputBoard에 세팅하기(필요한 데이터 하나로 묶기)
		inputBoard.setBoardCode(boardCode);
		inputBoard.setMemberNo(loginMember.getMemberNo());
		
		// 2. 서비스 메서드 호출 후 결과 반환 받기
		// insert 할거여서 result가 int로 나오는데 그걸로 안할거다
		//  -> 성공 시 [상세 조회]를 요청할 수 있도록 삽입된 게시글 번호 반환 받기
		//여기서는 서비스 호출 결과를 result가 아닌 boardNo로 받겠다
		int boardNo = service.boardInsert(inputBoard, images);
		
		
		// 3. 서비스 결과에 따라 message, 리다이렉트 경로 지정하기
		String path = null;
		String message=null;
		if(boardNo>0) {
			//실패하면 0이 반환돼서
			//성공 시 무조건 0보다 큰 게 반환된다
			path="/board/"+boardCode+"/"+boardNo; //상세 조회하는 경로
			message="게시글이 작성되었습니다.";
		}else {
			//실패한 경우
			path="insert";
			message="게시글 작성 실패..";
		}
		ra.addFlashAttribute("message", message);
		
		
		//게시글 작성(insert) 성공 시 작성된 글 상세 조회로 리다이렉트
		//상세조회하려면 boardCode, boardNo가 필요했다
		//작성한 글의 boardNo 가져와야된다
		return "redirect:"+path;
	}
	/* 게시글 삭제 */

//	@GetMapping("{boardCode:[0-9]+}/{boardNo:[0-9]+}/delete")
//	@PostMapping("{boardCode:[0-9]+}/{boardNo:[0-9]+}/delete")


	// 하나의 요청 주소로 GET, POST 주소를 모두 처리하는 방법
	@RequestMapping(value="{boardCode:[0-9]+}/{boardNo:[0-9]+}/delete",
					method = {RequestMethod.GET, RequestMethod.POST}) //GET, POST를 둘다 잡아서 처리하겠다 (두 방법 다 처리 방법 똑같음)
	public String boardDelete(
		@PathVariable("boardCode") int boardCode,
		@PathVariable("boardNo") int boardNo,
		@RequestParam(value="cp", required = false, defaultValue = "1") int cp,
		@SessionAttribute("loginMember") Member loginMember,
		RedirectAttributes ra
		) {

		Map<String, Integer> map = new HashMap<>();
		map.put("boardCode", boardCode);
		map.put("boardNo", boardNo);
		map.put("memberNo", loginMember.getMemberNo());


		int result = service.boardDelete(map);

		String path = null;
		String message = null;

		if(result > 0) {
			path = String.format("/board/%d", boardCode);
			message = "삭제 되었습니다";
		}else {
			path = String.format("/board/%d/%d?cp=%d", boardCode, boardNo, cp);
			// 더하기 하는 것보다 이렇게 하는 게 더 좋다
			message = "삭제 실패";
		}

		ra.addFlashAttribute("message", message);

		return "redirect:" + path;
	}


/*
	@PostMapping("{boardCode:[0-9]+}/{boardNo:[0-9]+}/delete")
	public String boardDeletePost(
		@PathVariable("boardCode") int boardCode,
		@PathVariable("boardNo") int boardNo,
		@RequestParam(value="cp", required = false, defaultValue = "1") int cp,
		@SessionAttribute("loginMember") Member loginMember,
		RedirectAttributes ra
		) {
		
		Map<String, Integer> map = new HashMap<>();
		map.put("boardCode", boardCode);
		map.put("boardNo", boardNo);
		map.put("memberNo", loginMember.getMemberNo());
		
		
		int result = service.boardDelete(map);
		
		String path = null;
		String message = null;
		
		if(result > 0) {
			path = String.format("/board/%d", boardCode);
			message = "삭제 되었습니다";
		}else {
			path = String.format("/board/%d/%d?cp=%d", boardCode, boardNo, cp);
			message = "삭제 실패";
		}
		
		ra.addFlashAttribute("message", message);
		
		return "redirect:" + path;
	}
	
	*/
	/*게시글 수정 화면으로 전환*/
	/**
	 * @param boardCode : 게시판 종류
	 * @param boardNo : 게시글 번호
	 * @param loginMember : update가 get매핑이어서 주소창에 똑같은 요청 치면 요청이 가서
	 * 						로그인한 회원이 작성한 글이 맞는지 검사할 용도로 session에서 loginMember 가져왔다
	 * @param model : forward시 request scope로 값 전달하는 용도
	 * @param ra : 리다이렉트 시 request scope에 있다가 잠깐 session갔다가 다시 request로 내려와서 값 전달 용도 
	 * @return
	 */
	@GetMapping("{boardCode:[0-9]+}/{boardNo:[0-9]+}/update")
	public String boardUpdate(
			@PathVariable("boardCode") int boardCode,
			@PathVariable("boardNo") int boardNo,
			//안전장치 추가
			@SessionAttribute("loginMember") Member loginMember,
			Model model,
			RedirectAttributes ra
			) {
		//수정 화면에 이전의 제목,업로드사진,내용이 들어가있을거다
		//그러려면 수정하려면 해당 글을 상세조회를 해야한다(boardNo를 이용해서)
		//수정 화면에 출력할 제목/내용/이미지 조회
		//	-> 게시글 상세조회랑 똑같다 (이미 있다)
		Map<String, Integer> map = new HashMap<>();
		map.put("boardCode", boardCode);
		map.put("boardNo", boardNo);
		
		//BoardService.selectOne(map) 호출
		//여기에는 BoardService 객체가 없다
		//BoardService 객체를 여기다 만들지 말고 
		//BoardService는 이미 빈으로 등록돼서 스프링이 관리하고 있어서 
		//위에 필드에 final만들기
		//생성자가 만들어지고 자동으로 의존성 주입된다
		
		Board board = boardService.selectOne(map);
		//memberNo는 로그인한 회원 번호가 아닌, 글을 쓴 사람의 번호이다!
		String message=null;
		String path = null;
		
		//get방식 요청이다보니까 조회 자체가 잘 안될 수도 있어서
		if(board ==null) { //조회 실패한 경우
			message="해당 게시글이 존재하지 않습니다";
			path="redirect:/"; //메인페이지로 
			ra.addFlashAttribute("message", message);
		}else if(board.getMemberNo() !=loginMember.getMemberNo()){
			//board는 null이 아니었는데
			//내가 쓴 글이 아닐 때
		message="자신이 작성한 글만 수정할 수 있습니다.";
		path=String.format("redirect:/board/%d/%d", boardCode,boardNo); 
		//해당 글 상세조회로
		//String의 모양을 지정할 수 있다
		ra.addFlashAttribute("message", message);
		}else {
			//이도 저도 뭣도 아닐 때
			//정상적인 경우
			path="board/boardUpdate";
			model.addAttribute("board", board);
		}
		return path; //forward
	}
	/**게시글 수정
	 * @param boardCode : 게시판 종류
	 * @param boardNo : 수정할 게시글 번호
	 * @param inputBoard : 커맨드객체(제목,내용만 담겨있다)
	 * @param loginMember : 로그인해서 게시글 수정하려는 회원 번호랑 작성자의 회원번호가 같은지 검사 용도 
	 * @param images : 제출된 input type="file" 모든 요소 5개가 다 담겨있다
	 * @param ra : 리다이렉트 시 request scope로 값 전달
	 * @param deleteOrder : 삭제된 이미지의 순서가 기록된 문자열 (1,2,3 꼴로 돼있다)
	 * @param querystring : 수정 성공 시 이전 파라미터 유지할 때 사용(cp같은 것 유지할 때)
	 *			
	 * @return
	 * @throws IOException 
	 * @throws IllegalStateException 
	 */
	@PostMapping("{boardCode:[0-9]+}/{boardNo:[0-9]+}/update")
	//바로 위의 화면 전환이랑 주소 똑같은데 POST
	public String boardUpdate(
			@PathVariable("boardCode") int boardCode,
			@PathVariable("boardNo") int boardNo,
			//제목과 내용을 따로 RequestParam으로 받아오지말고
			//이 데이터들이 서비스,매퍼,DB까지 갈거여서 Mybatis까지 갈건데 Mybatis에는 파라미터 한 개만 가능
			//어차피 묶어서 보내야 하니까
			//Map이나 DTO로 묶기
			/*@ModelAttribute*/ Board inputBoard,
			@SessionAttribute("loginMember") Member loginMember, //게시글을 누가 썼는지 알려고
			@RequestParam("images") List<MultipartFile> images, //제출되는 name값이 같으면 배열이나 리스트로 반환된다!!!
			//파일을 아무것도 선택 안한경우 이 리스트가 안만들어지는 게 아니라 빈칸으로 파일 개수대로 제출된다
			RedirectAttributes ra, //게시글 작성 성공/실패 시 작성한 게시글의 상세조회로 리다이렉트 시 request scope로 데이터 전달용
			
			//새로 추가돼서 넘어온 두 개!
			
			@RequestParam(value="deleteOrder", required=false) String deleteOrder ,
			//제출 안된 경우에는 안넘어올 수 있어서 required false로!
			
			@RequestParam(value="querystring", required=false,defaultValue = "") String querystring 
			//쿼리스트링은 제출 안된 경우에는 기본값 빈칸 설정(빈칸 안하면 null이 들어가기 때문에)
			) throws IllegalStateException, IOException {
		// 1. 커맨드 객체(inputBoard)에 boardCode, boardNo, memberNo를 세팅하기 -> 다 묶어서 DB 업데이트 할 때 한번에 보내야 해서
		inputBoard.setBoardCode(boardCode);
		inputBoard.setBoardNo(boardNo);
		inputBoard.setMemberNo(loginMember.getMemberNo());
		
		// -> inputBoard에는 제목,내용, boardCode, boardNo, memberNo 해서 총 다섯 개 들어가있게 된다
		
		// 2. 게시글 수정 서비스 호출 후 결과 반환 받기
		// 게시글 삽입할 때에는 몇번 글이 삽입됐는지 글 번호를 반환받았는데
		// 성공 실패만 받으면 된다
		int result = service.boardUpdate(inputBoard,images,deleteOrder);
		
		// 3. 서비스 호출 결과에 따라 응답 제어하기
		String message=null;
		String path=null;
		
		if(result>0) {
			//성공 시
			message="게시글이 수정 되었습니다.";
			path=String.format("/board/%d/%d%s", boardCode,boardNo, querystring); //상세조회
		}else {
			//실패 시
			message="수정 실패...";
			path="update"; //상대경로->현재주소랑 똑같은데 getMapping으로 바껴서 수정화면으로 전환된다
		}
		ra.addFlashAttribute("message", message);
		return "redirect:"+path;
	}
	
}

