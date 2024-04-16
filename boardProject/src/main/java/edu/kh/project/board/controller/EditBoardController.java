package edu.kh.project.board.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.board.model.dto.Board;
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
	@ResponseBody
	@GetMapping("{boardCode:[0-9]+}/{boardNo:[0-9]+}/delete")
	public int delete(
			@PathVariable("boardCode") int boardCode,
			@PathVariable("boardNo") int boardNo,
			RedirectAttributes ra,
			@RequestBody 
			) {
		int result = service.delete(boardCode, boardNo);
//		String message=null;
//		String path=null;
//		if(result>0) {
//			message="삭제 성공!!";
//			// http://localhost/board/1?cp=1
//			path="/board/"+boardCode+"?cp=1";
//		}else {
//			message="삭제 실패..";
//			// http://localhost/board/2/1979
//			path="/board/"+boardCode+"/"+boardNo;
//		}
//		ra.addFlashAttribute("message", message);
		return result;
	}
	
	
}

