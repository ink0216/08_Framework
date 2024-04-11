package edu.kh.project.board.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.kh.project.board.model.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Controller
@RequestMapping("board") //board로 시작하는 요청을 다 받는다
@RequiredArgsConstructor //서비스 의존성 주입 받기
public class BoardController {
	private final BoardService service;
	
	//게시판 클릭 시마다 화면이 다 다를 때에는 1,2,3으로 각각 해도 되지만
	//모든 게시판은 다 비슷한 형태여서 하나로 할 수 있다
	/**게시글 목록 조회
	 * @param boardCode : 어떤 게시판인지 게시판 종류 구분
	 * @param cp : 현재 조회 요청한 게시판 페이지 (없으면 1)
	 * @return
	 * - /board/OOO
	 * 		/board 이하 1레밸 자리에 숫자로만 된 요청주소가 작성되어 있을 때에만 여기로 매핑되게 하는 법
	 * 			-> 정규표현식을 이용하면 된다!!
	 * 		:뒤부터 
	 * 		[] : 문자 1 칸
	 * 		[1234] : 이 한 칸에 1,2,3,4중 하나만 들어올 수 있다
	 * 		[0-9] : 이 한 칸에 0부터 9 사이의 숫자 하나만 들어올 수 있다
	 * 		 + : 하나 이상
	 */
	@GetMapping("{boardCode:[0-9]+}") //0-9까지 들어올 수 있는 칸이 하나 이상 있다 -> 모든 숫자 완성됨!!!
	public String selectBoardList(
			@PathVariable("boardCode") int boardCode,
			@RequestParam(value="cp", required=false, defaultValue="1") int cp,
			Model model //request scope로 값 전달하는 용도
			//current page
			//cp가 없으면 기본값은 1로 해서 1페이지 보여지게 할 거야
			
			//getMapping이면 파라미터가 쿼리스트링에 담겨있는데 쿼리스트링이 없음
			//요청인데 쿼리스트링이 없을 때 -> 1이 들어감
			//2페이지 이상부터는 쿼리스트링으로 생긴다(cp)
			//위의 메뉴 눌렀을 때에는 1페이지 보여지고
			//cp == 8 ->8페이지 보겠다
			//cp == 현재 요청한 페이지임()
			
			// /board뒤에 하나만 있을 때 그 값이 무엇이든 다 얻어와서 boardCode에 넣는 문제가 있다
			// 만약 /board/insert 시, insert를 처리하는 컨트롤러와 매핑돼야하는데
			// 여기로 잡혀버리는 문제가 생긴다
			
			//경로 중에 값이 boardCode인 애를 얻어와서 여기에 저장하겠다
			// /board/1 요청 오면 그 중에 1을 얻어와서 boardCode 변수에 저장하겠다
			//->깃허브에서 이용 가능
			// 주소의 닉네임 자리에 다른 사람 닉네임 넣으면 그 사람 깃허브 들어갈 수 잇다
			) {
		log.debug("boardCode : "+boardCode);
		
		//조회 서비스 호출 후 결과 반환 받기
		//반환돼야 하는 결과가 두 개인데 
		//메서드는 하나씩만 반환할 수 있어서
		Map<String, Object> map = service.selectBoardTypeList(boardCode, cp);
		//몇 번 게시판의 몇 페이지 분량 조회할거야
		
		//map을 쪼개서 보내기
		//묶은 이유 : 반환할 수 있는 것은 하나만 할 수 있어서 묶어서 받았음
		//쪼갠 이유 : html에서 쪼개서 쓰기 힘들어서 여기서 쪼개서 보낸다
		model.addAttribute("pagination", map.get("pagination"));
		model.addAttribute("boardList", map.get("boardList"));
		return "board/boardList"; //boardList.html로 forward
	}
//	@GetMapping("insert") //->이게 있으면 여기에 매핑 되고 없는 거면 selectBoardList에 매핑된다
//	public String test() {
//		log.debug("insert 매핑됨");
//		return "redirect:/";
//	}

}
/*@PathVariable("key")
 * @Request/Get/Post/Put/DeleteMapping()에 작성된 
 * URL에서 특정 경로를 얻어와 변수에 저장하는 어노테이션
 * 
 * 예시
 *  @GetMapping("test/{apple}/{banana}") //이런 꼴로 요청 오면
 *  public String test(
 *  	@PathVariable("apple") String a, //요청 왔을 때 위의 apple위치에 온 값을 a에 대입하겠다
 *  	@PathVariable("banana") String b,//요청 왔을 때 위의 banana위치에 온 값을 b에 대입하겠다
 *  ){
 *  }
 *  요청 주소 : /test/영주/하와이
 *  	=> a에 저장된 값 == 영주
 *  	=> b에 저장된 값 == 하와이
 *  
 * [추가 내용]
 * 요청 주소에 정규표현식을 사용해서 요청 주소를 제한할 수 있다
 * 예시
 * @GetMapping("{boardCode:[0-9]+}")
 *	  //boardCode자리에 숫자로만 된 주소만 매핑함
 * 
 * */
