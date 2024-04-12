package edu.kh.project.board.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.board.model.dto.BoardImg;
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
	public String selectBoardList( //요청 주소 모양 /board/1, /board/2, /board/3이런 경우 화면이 완전 다르면 따로 메서드 쓰는게 맞는데
			//게시판 종류만 다르고 모양은 비슷한 경우 이거 이용 가능
			//주소 중의 특정한 부분의 값을 얻어와서 변수에 저장해서 로직에 이용할 수 있게 하는 것 == @PathVariable
			//뒷자리에 들어오는 boardCode에는 0부터 9까지의 숫자 한 자리 이상 올 수 있다 하는 정규식 사용 가능
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
		return "board/boardList"; //두 개 담아서 boardList.html로 forward
	}
//	@GetMapping("insert") //->이게 있으면 여기에 매핑 되고 없는 거면 selectBoardList에 매핑된다
//	public String test() {
//		log.debug("insert 매핑됨");
//		return "redirect:/";
//	}

	//---------------------------------------------------------------------------------------
	/**
	 * @return
	 */
	@GetMapping("{boardCode:[0-9]+}/{boardNo:[0-9]+}") // /board/1/1998?cp=1 이런 식으로 요청 온다(상세 조회 요청 주소 모양)
	//숫자만 오도록 하기 위해 정규표현식 사용
	public String boardDetail(
			//바로 위의 매핑 주소 중에 boardCode 꺼내서 저장하려면 @PathVariable 사용!
			@PathVariable("boardCode") int boardCode,
			@PathVariable("boardNo") int boardNo,
			Model model, //forward 시 데이터 전달용 객체
			RedirectAttributes ra //해당 글이 없으면 메세지 담아서 리다이렉트 할 용도
			) {
		//게시글 상세 조회 서비스 호출
		//이때 sql에 board_no랑 board_code를 보내야 하는데 하나밖에 못보내서
		//가장 쉬운 게 맵으로 묶는 것!
		
		// 1) Map으로 전달할 파라미터 묶기
		Map<String, Integer> map = new HashMap<>();
		//전달할 두 개가 모두 정수여서
		
		map.put("boardCode", boardCode);
		map.put("boardNo", boardNo);
				
		// 2) 서비스 호출
		Board board = service.selectOne(map);
		//조회 결과가 담긴다
		
		
		String path=null;
		if(board ==null) {//조회 결과가 없는 경우
			//글을 즐겨찾기 해놨는데 게시글 작성자가 그 글을 삭제한 경우
			//조회 결과가 없어짐 
			//리스트는 isEmpty로 하는데, board는 하나니까 null로 함
			path="redirect:/board/"+boardCode; //해당 게시판 목록으로 다시 날려버리겠다
			ra.addFlashAttribute("message", "게시글이 존재하지 않습니다.");
		}else {//조회 결과가 있을 경우
			path="board/boardDetail"; //html로 forward
			model.addAttribute("board", board); //조회한 board를 넘긴다
			//board안에는 게시글 상세 조회 + imageList + commentList에 다 들어있다
			
			
			if(!board.getImageList().isEmpty()) {
				//조회된 이미지 목록(imageList)가 있을 경우
				//imageList는 board 안에 있다 (List 타입으로)
				//아니면 사이즈가 0이다로 해도 된다
				BoardImg thumbnail = null;
				/*우리가 imageList의 0번 인덱스를 썸네일로 하기로 우리가 임의로 정했다
				 * imageList의 0번 인덱스 == 가장 빠른 순서(imgOrder)*/
				if(board.getImageList().get(0).getImgOrder()==0) {
					//List[0] 얻어옴
					//이미지 목록의 첫 번째 행의 순서가 0이다 == 썸네일인 경우
					//썸네일이 없는 경우는, 0번 인덱스 없을 거다
					thumbnail = board.getImageList().get(0); //길게 말하기 힘드니까 thumbnail이라는 변수에 저장해둘게
					//썸네일이 
				}
				//if문 실행 안된 경우는 썸네일이 없는 경우인데, el에서는 빈칸으로 나와서 신경 안써도 된다
				model.addAttribute("thumbnail", thumbnail);
				
				//썸네일이 있을 때/없을 때
				//출력되는 이미지(썸네일 제외하고) 시작 인덱스를 계산,지정하는 코드
				model.addAttribute("start", thumbnail !=null ? 1 : 0);
				//thumbnail이 null이 아니면(존재하면) 1을 start에 넣고, null이면 0을 넣는다(삼항연산자)
				
				/*조회한 이미지 네 장의 순서가 1,2,3,4인 경우 0번이 없으면 썸네일이 없다 -> start에 0이 들어갈거다
				 * 썸네일 없을 때 화면에 앞에서부터 0,1,2,3번째에 넣어라*/
				
				//썸네일의 유무에 따라 아래의 사진 네 칸을 어떤 인덱스로 채울 지 로직을 만든거다
				
			}
		}
		return path; 
	}
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
 * [추가 내용 1]
 * 요청 주소에 정규표현식을 사용해서 요청 주소를 제한할 수 있다
 * 예시
 * @GetMapping("{boardCode:[0-9]+}")
 *	  //boardCode자리에 숫자로만 된 주소만 매핑함
 * 
 * [추가 내용 2]
 * @PathVariable로 얻어온 값은 request scope 속성으로 자동 추가된다
 * ->forward한 곳으로도 넘어가서 거기서 이 값 쓰고싶으면 request scope에 있는 것 얻어오고싶으면 ${key} 쓰면 된다
 * 
 * 
 * 
 * 
 * */
