package edu.kh.project.board.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.board.model.dto.BoardImg;
import edu.kh.project.board.model.service.BoardService;
import edu.kh.project.member.model.dto.Member;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
	/**게시글 상세 조회
	 * @return
	 * @throws ParseException 
	 */
	@GetMapping("{boardCode:[0-9]+}/{boardNo:[0-9]+}") // /board/1/1998?cp=1 이런 식으로 요청 온다(상세 조회 요청 주소 모양)
	//숫자만 오도록 하기 위해 정규표현식 사용
	//boardCode : 게시판 구분 번호
	//boardNo : 게시글 구분 번호
	// 상세 조회 요청 주소
	//  /board/1/1998?cp=1
	//  /board/2/1956?cp=2
	public String boardDetail(
			//바로 위의 매핑 주소 중에 boardCode 꺼내서 저장하려면 @PathVariable 사용!
			@PathVariable("boardCode") int boardCode,
			@PathVariable("boardNo") int boardNo,
			Model model, //forward 시 데이터 전달용 객체
			RedirectAttributes ra, //해당 글이 없으면 메세지 담아서 리다이렉트 할 용도
//			@SessionAttribute("lobinMember") Member loginMember // 로그인 안 한 경우 null일 수 있어서 오류가 남!!
			@SessionAttribute(value="loginMember", required=false) Member loginMember, // 로그인 안 한 경우 null일 수 있어서 오류가 남!!
			//그래서 value랑 required속성 추가해서 필수 파라미터 아니라고 지정
			//해당 회원이 해당 글에 좋아요 눌렀는 지 여부 알려면 회원 번호가 필요해서 세션에서 로그인 멤버 얻어옴
//			@SessionAttribute(value="lobinMember", required=false) Member loginMember : loginMember 얻어올건데 
															//필수는 아니어서 없어도 오류 아님 -> 해당 속성 값이 없으면 null이 반환됨 
			//@SessionAttribute : Session에서 속성 값 얻어오기
			HttpServletRequest req, //요청에 담긴 쿠키 얻어오기(조회 수 에 이용)
			HttpServletResponse resp // 새로운 쿠키를 만들어서 내보내기(응답하기) (쿠키는 서버가 만들어서 클라이언트에 주면 클라이언트가 저장하고
			//서버 통신 때 보내줄 수 있다 서버로)
			) throws ParseException {
		
		//게시글 상세 조회 서비스 호출
		//이때 sql에 board_no랑 board_code를 보내야 하는데 하나밖에 못보내서
		//가장 쉬운 게 맵으로 묶는 것!
		
		// 1) Map으로 전달할 파라미터 묶기
		Map<String, Integer> map = new HashMap<>();
		//전달할 두 개가 모두 정수여서
		
		// 1-1) 로그인한 상태일 경우에만 memberNo를 map에 추가해서 보내기
		if(loginMember !=null) {
			map.put("memberNo", loginMember.getMemberNo());
		}
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
			
			/************* 쿠키를 이용한 조회수 증가(시작) **************/
			// 1. 비회원(로그인 안 한 회원)이 조회했거나 로그인한 회원의 글이 아닌 경우
			// 자신이 쓴 글을 읽을 때에는 조회수 안늘리게 만들겠다
			// 1번은 글쓴이를 뺀 다른 사람인 경우임!!!!
			if(loginMember == null || 
					loginMember.getMemberNo() !=board.getMemberNo()
					//게시글 쓴 사람의 회원번호(board.getMemberNo())랑 로그인한 회원의 회원 번호(loginMember.getMemberNo())가 다를 때
					
					) {

				//요청에 담겨있는 모든 쿠키 얻어오기
				Cookie[] cookies = req.getCookies(); //요청에 담겨있는 쿠키들을 다 가져옴 -> 여러 개여서 배열 형태로 반환된다
				Cookie c = null;
				for(Cookie temp : cookies) {
					//쿠키에서 이름이 있어서 그 이름을 꺼내왔을 때 
					if(temp.getName().equals("readBoardNo")) { // getName 하면 name값 얻어옴
						//요청에 담긴 쿠키에 "readBoardNo"라는 쿠키가 존재할 때
						c = temp; //존재할 때에만 그 값을 c에 저장해놓겠다
						break;
					}
				}
				int result=0; //조회 수 증가 결과를 저장할 변수
				
				if(c ==null) {
					// "readBoardNo"가 요청 받은 쿠키에 없을 때 
					//없으니까 새로 만들기
					//쿠키는 name, value로 넣어줄 수 있다
					
					//새 쿠키 생성("readBoardNo", [읽은 게시글 번호])
					c= new Cookie("readBoardNo", "["+boardNo+"]"); //@PathVariable로 넣어준 boardNo
					result = service.updateReadCount(boardNo);
					
					
				}else {
					// "readBoardNo"가 요청 받은 쿠키에 존재할 때
					// 새 쿠키 생성("readBoardNo", [20][30][400][2000]) 이런 식으로 읽은 게시물 번호를 점점 늘려나갈거다
					if(c.getValue().indexOf("["+boardNo+"]")==-1) {
						//다른 글 읽었는데 현재 글은 처음 읽은 경우
						
						//값 중에서 이거랑 똑같은 게 있는 지 찾아서 있으면 인덱스번호가 반환됨
						//값 중에서 이거랑 똑같은 게 없으면 -1이 반환됨
						
						//해당 글 번호를 쿠키에 누적해서 더함 여기서는 +=을 쓸 수 없어서
						c.setValue(c.getValue()+"["+boardNo+"]");
						result = service.updateReadCount(boardNo);

					}
				}
				//조회 수 증가 성공 시 
				if(result>0) {
					// 먼저 조회된 board의 readCount값을
					//result 값으로 변환하기
					board.setReadCount(result);//result 값으로 바꾼다
					
					
					//쿠키 파일에 대한 기본 설정
					// 적용 경로 설정
					c.setPath("/"); // "/" 이하 경로 요청 시(모든 요청 주소) 쿠키 서버로 전달
					//어떤 요청 주소에 대해서 쿠키를 담아서 서버로 제출을 시킬건지
					
					//쿠키에는 수명이 있는데 오늘 밤 23시 59분 59초까지만 쿠키가 유지되게 만들기
					//내일이 되면 쿠키가 사라져서 다시 조회수를 늘릴 수 있게 된다
					//하루에 한 번 씩만 특정 게시글의 조회수를 늘릴 수 있는 계산
					
					// 수명 지정
					Calendar cal = Calendar.getInstance(); // 싱글톤 패턴
					cal.add(cal.DATE, 1);

					// 날짜 표기법 변경 객체 (DB의 TO_CHAR()와 비슷)
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

					// java.util.Date
					Date a = new Date(); // 현재 시간

					Date temp = new Date(cal.getTimeInMillis()); // 다음날 (24시간 후)
					// 2024-04-15 12:30:10

					Date b = sdf.parse(sdf.format(temp)); // 다음날 0시 0분 0초

					// 다음날 0시 0분 0초 - 현재 시간
					long diff = (b.getTime() - a.getTime()) / 1000; //소수점 버림을 이용해서 1초 날려버리기
					// -> 다음날 0시 0분 0초까지 남은 시간을 초단위로 반환

					c.setMaxAge((int) diff); // 수명 설정

					resp.addCookie(c); // 응답 객체를 이용해서 클라이언트에게 전달
					
					//쿠키를 이용해서 오늘 읽은 글은 또 못읽음
					//내가 작성한 글이 아닌 남이 작성한 글을 읽을 때에만 조회수 늘어남
					//한 브라우저로 올릴 수 있는 조회수는 게시글 당 하루 한번밖에 안된다
					//다른 브라우저로 들어오면 올릴 수 있다(크롬, 엣지,...)
					//그래서 같은 브라우저로 다른 사람 아이디 로그인해서 해도 조회수 안늘어난다
					
				}
				
				
				
				
				
				
				
				
			}
				
			
			
			
			
			
			
			
			
			/************* 쿠키를 이용한 조회수 증가(끝) **************/
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
	/**게시글 좋아요 체크/해제
	 * @return result
	 */
	@PostMapping("like")
	@ResponseBody
	public int boardLike(
			@RequestBody Map<String, Integer> map //Map은 Object타입
			//JSON은 String타입이다 (모든 언어에서 String 타입은 호환돼서 이렇게 json으로 해서 보냄)
			//JSON : JS Object Notation, JS 객체 표기법
			// == js 객체 양쪽에 ""찍은 게 
			// == "{K:V, K:V}" -> 근데 사용하기 어려움
			//HttpMessageConverter가 자바에서 쓸 수 있도록
			//String, Object, 파일로 바꿔줄 수 있다
			//@RequestBody String jsonData로 하면 "{K:V, K:V}"이 형태로 받아와진다
			) {
		return service.boardLike(map);
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
