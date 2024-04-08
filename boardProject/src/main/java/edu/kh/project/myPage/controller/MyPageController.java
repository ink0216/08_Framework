package edu.kh.project.myPage.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.member.model.dto.Member;
import edu.kh.project.myPage.model.dto.UploadFile;
import edu.kh.project.myPage.model.service.MyPageService;
import lombok.RequiredArgsConstructor;

@Controller //컨트롤러 역할 명시 + bean 등록
@RequestMapping("myPage") //공통 주소 매핑
@RequiredArgsConstructor
@SessionAttributes({"loginMember"})
public class MyPageController {
	private final MyPageService service; //알아서 의존성 주입이 될것이다!!(@RequiredArgsConstructor가 해줌)
	/**내 정보 조회/수정 화면으로 전환
	 * @param loginMember : 세션에 존재하는 loginMember를 얻어와 매개변수에 대입
	 * @param model : 데이터 전달용 객체 (기본 request scope)
	 * @return myPage-info.html로 forward
	 * 
	 */
	@GetMapping("info") //   /myPage/info GET 방식 요청 매핑
	public String info(
			@SessionAttribute("loginMember") Member loginMember,
/*<!-- 주소는 입력하는 곳은 세개인데 컬럼은 하나여서 하나의 문자열로 합쳐서 DB에 저장해둠 -->
                        <!-- 타임리프에서 쪼개는 거 이제 지원 안해서 DB에서 쪼개서 가져오든가 JS로 쪼개도 된다 -->
                        <!-- 백엔드에서 쪼개보자! -->
                        <!-- 서버 Controller에서 로그인한 회원 주소를 ^^^를 기준으로 쪼갤거다 -->
                        */
			Model model //forward같은 것 할 때 데이터 전달하는 객체(기본 request scope, 어노테이션 사용 시 세션으로도 올라간다)
			) {
		//로그인 정보 중 다 필요한 것 아니고 주소만 필요해서 주소만 꺼내옴
		String memberAddress = loginMember.getMemberAddress();
		
		//주소가 필수 입력이 아닌 선택사항이어서 없는(null인) 사람도 있다
		
		//주소가 있을 경우에만 동작할거다
		if(memberAddress !=null) {
			//쪼개기를 해보겠다
			
			String[] arr = memberAddress.split("\\^\\^\\^"); //구분자 "^^^"를 기준으로 쪼개서 String 배열로 반환
			// 정규표현식에서 ^가 시작이라는 뜻이 된다
			// \^로 \를 하나만 쓰면 백슬래시 하나는 escape 문자로 인식돼서
			//	백슬래시 두개쓰기( \\ == "\") (문자 그대로 인식하게 됨)
			//  "\\^" == "^"
			
			// "04540^^^서울시 중구 남대문로 120^^^2층 A강의장"
			// --> ["04540", "서울시 중구 남대문로 120", "2층 A강의장"]
			
			//이 배열을 모델에 세팅해서 보낼거다
			model.addAttribute("postcode", arr[0]);
			model.addAttribute("address", arr[1]);
			model.addAttribute("detailAddress", arr[2]);
		}
		return "myPage/myPage-info"; //  /templates/myPage/myPage-info.html로 forward
	}
	/**프로필 이미지 변경 화면으로 이동(forward)
	 * @return
	 */
	@GetMapping("profile")
	public String profile() {
		return "myPage/myPage-profile";
		
	}
	/**비밀번호 변경 변경 화면으로 이동(forward)
	 * @return
	 */
	@GetMapping("changePw")
	public String changePw() {
		return "myPage/myPage-changePw";
		
	}
	/**회원 탈퇴 화면으로 이동(forward)
	 * @return
	 */
	@GetMapping("secession")
	public String secession() {
		return "myPage/myPage-secession";
		
	}
	/**회원 정보 수정
	 * @param inputMember : 제출된 회원 닉네임, 전화번호, 주소가 담겨있다
	 * @param loginMember : 로그인한 회원 정보(이 중 회원 번호를 사용할 예정)
	 * @param memberAddress : 주소만 따로 받아온 String[]
	 * @param ra : 리다이렉트 시 request scope로 데이터 전달하는 객체(flash)
	 * @return
	 */
	@PostMapping("info")
	public String updateInfo(
			//제출 받은 데이터를 받기
			/*@ModelAttribute*/ Member inputMember,
			//회원 번호가 일치하는 사람의 정보를 수정하겠다(PK여서)
			//세션에서 얻어오기
			@SessionAttribute("loginMember") Member loginMember,
			
			//주소를 세 칸으로 나눠서 입력받기 때문에 가공해야한다
			@RequestParam("memberAddress") String[] memberAddress,
			//주소만 따로 String 배열로 받아오기
			
			//리다이렉트할 때 메시지 보내고싶다
			RedirectAttributes ra
			) {
		
		//정보 수정하기
		
		//inputMember에 로그인한 회원번호 추가
		int memberNo = loginMember.getMemberNo(); //로그인한 회원의 번호를 꺼내와서 세팅
		inputMember.setMemberNo(memberNo); //loginMember에서 꺼내와서 inputMember에 넣기
		
		//회원 정보 수정 서비스 호출
		//DB에서 수정하면 int가 돌아온다
		int result = service.updateInfo(inputMember, memberAddress);
		//memberAddress를 넘겨서 가공할거다
		
		String message;
		
		//SQL의 업데이트 구문 수행 시 DB에서만 바뀐거다
		//홍길동이라고 DB에 저장돼있고 그걸로 로그인 시 홍길동이 세션에도 기록됨
		//고길동이라고 바꾼 경우, 세션의 값은 아직 홍길동이니까 
		//수정 성공 시 세션의 값도 바꾼 고길동으로 바꿔야한다
		//세션은 컨트롤러에서만 다룰 수 있다
		
		//자바 기본 자료형 8개 : byte, short, long, char, boolean, int, double, float
		//얘네만 값 자체를 저장 가능
		
		//그 외는 참조형 변수(주소를 저장해서 해당 주소의 데이터 참조)
		//매개변수로 받아온 loginMember에는, 객체가 아닌, 주소가 저장돼있다!!!
		//주소만 복사해서 나눠줌 -> 얕은 복사
		//loginMember에는 원본의 주소가 저장돼있고, 원본은 세션에 있다
		//변수를 수정 시 원본(세션의 정보)도 수정된다
		if(result>0) {
			//수정 성공
			message = "회원 정보 수정 성공!!";
			//loginMember는
			//세션에 저장된 로그인된 회원 정보가 저장된 객체를
			//참조하고 있다!!!
			// -> loginMember를 수정하면
			//		세션에 저장된 로그인된 회원 정보가 수정된다!!!
			//		== 세션 데이터가 수정된다
			loginMember.setMemberNickname(inputMember.getMemberNickname());
			loginMember.setMemberTel(inputMember.getMemberTel());
			loginMember.setMemberAddress(inputMember.getMemberAddress());
			//참조형은 주소만 대입된다 -> 그 참조형 변수값을 수정하면 그 주소의 원본도 수정된다!!!
			
			//inputMember
		}else {
			//실패
			message="회원 정보 수정 실패...";
		}
		ra.addFlashAttribute("message", message);
		//업데이트 성공/실패 시 내정보 조회하는 페이지로 리다이렉트
		return "redirect:info"; //get방식!
	}
	@PostMapping("changePw")
	public String changePw(
			@SessionAttribute("loginMember") Member loginMember,//회원 번호 얻어오려고
			@RequestParam Map<String, Object> paramMap, // 이거 쓰면 파라미터들이 다 들어가서 넘어온다
			RedirectAttributes ra
			
			
			) {
//		<!-- 제출될 때 새 비밀번호 확인은 새 비밀번호랑 동일한 지 비교할 용도여서
//        현재 비밀번호랑 새 비밀번호만 신경쓰면 된다
//    컨트롤러에 두 개를 파라미터를 전달해서 현재 비밀번호가 일치하는 사람의 비밀번호를 새 비밀번호로 바꿔라
//    누구인지는 세션에서 회원 번호 얻어와서 그 사람이 누구인지 확인하기
//    현재 비밀번호, 새 비밀번호, 회원번호 세 개를 묶어서 서비스로 보내서 호출
//
//    서비스에서 
//    -1. 바로 매퍼 호출하는 게 아니라 비크립트 암호화를 했기 때문에 DB에 비밀번호를 가져가서 비교할 수 없음
//    비크립트 암호화 된 비밀번호를 DB에서 조회해야 함(회원 번호 필요(몇 번 회원의 암호화된 비밀번호 가져와))
//    -2. 현재 비밀번호랑 암호화돼서 저장돼있던 비밀번호 비교
//    (BcryptPasswordEncoder.matches(평문, 암호화된 비밀번호) 메서드 이용) ->둘이 같으면 true, 다르면 false나옴
//        -> 같을 경우 -> 새 비밀번호를 암호화 진행(DB에 암호화해서 저장해야 함)
//                        새 비밀번호로 변경(UPDATE)하는 Mapper 호출
//                        (회원 번호, 새 비밀번호를 하나로 묶기(Member 또는 Map 또는 아무거나))
//                        (매퍼로 전달할 수 있는 파라미터는 항상 1개만 가능)
//                    -> 결과 1 또는 0 반환
//        -> 다를 경우 -> 실패 (0 반환하도록 하기)
//    서비스에서 SQL 두 번 수행하려면 매퍼 두 번 호출하면 된다
//
//[컨트롤러로 돌아옴]
//- 변경 성공 시 alert("비밀번호가 변경 되었습니다.") + /myPage/info 로 리다이렉트
//- 변경 실패 시(현재 비밀번호 틀린 경우) alert("현재 비밀번호가 일치하지 않습니다.") + /myPage/changePw로 리다이렉트 -->
		int memberNo = loginMember.getMemberNo();
		int result = 0;
		
		result = service.changePw(memberNo, paramMap);
		String path=null;
		String message=null;
		if(result>0) {
			path="/myPage/info";
			message="비밀번호가 변경 되었습니다.";
		}else {
			message="현재 비밀번호가 일치하지 않습니다.";
			path="/myPage/changePw";
		}
		ra.addFlashAttribute("message", message);
		return "redirect:"+path;
	}
	
	/*//@SessionAttributes로 올라간 세션만 완료시킨다!!!!
	 *@SessionAttributes : Model에 세팅된 값 중 key가 일치하는 값만 request -> session으로 변경
	 *
	 *SessionStatus : @SessionAttributes를 이용해서 올라간 데이터의 상태를 관리하는 객체
	 */
	/**
	 * @param map : 입력 받은 비밀번호 얻어오는 용
	 * @param ra : 
	 * @param loginMember : 로그인한 회원 세션 정보
	 * @param status : 세션 완료(없애기) 용도의 객체
	 * 			->세션을 통째로 완료시키는 애가 아닌, 해당 컨트롤러 위에 세션어트리뷰츠({"key1", "key2", ..}) 어노테이션이 작성되어있는 경우
	 * 				() 내 key들의 상태를 관리하는 객체
	 * 			
	 * @return
	 */
	@PostMapping("secession")
	public String secession(
			@RequestParam Map<String, Object> map,
			RedirectAttributes ra,
			@SessionAttribute("loginMember") Member loginMember, //세션에 있는 것 중 loginMember를 키로 가지는 것 가져옴
			//로그아웃 == Session에 저장된 로그인된 회원 정보를 없애는(만료시키는, 무효화시키는, "완료시키는") 것!!!
			SessionStatus status //세션 상태
			) {
		String inputPw = (String)map.get("memberPw");
		int memberNo = loginMember.getMemberNo();
		String message;
		String path;
		int result = service.secession(inputPw, memberNo);
		if(result>0) { //메인페이지로 리다이렉트
			path="/";
			message="탈퇴 되었습니다";
			// invalidate는 세션에 있는 모든 정보를 없애는 것
			// status.setComplete는 @SessionAttributes로 올라간 세션정보(loginMember)만 완료시킨다!!!!
			
//			status.isComplete() :만료되었는지 확인하는 것
			status.setComplete();  //해당 클래스 위에 세션 어트리뷰츠로 등록돼있던 정보만 만료됨 
			//세션을 완료 시킴(없앰) ->왼쪽에 나오던, 세션에 저장돼있던 회원 정보도 같이 사라진다!!(세션이 사라져서)
			//로그인멤버만 없애고싶으면 
			//HttpSession session으로 해서 session.removeAttribute("loginMember")로 하나씩 없앨 수 있다
			//세션 만료 시간 SessionConfig 객체 만들어서 시간 지정 가능
		}else {
			//실패
			path="secession"; //상대경로
			message="비밀번호가 일치하지 않습니다.";
		}
		ra.addFlashAttribute("message", message);
		return "redirect:"+path;
	}
	/*파일 업로드 테스트*/
	@GetMapping("fileTest")
	public String fileTest() {
		return "myPage/myPage-fileTest"; //forward하겠다
	}
	/*Spring에서 파일 업로드를 처리하는 방법
	 * - enctype="multipart/form-data"로 클라이언트 요청을 받으면
	 * 이 요청 자체가 여러 데이터 타입을 각자의 인코딩으로 보낸다는 거여서 문자, 숫자, 파일 등이 섞인 요청이다
	 * Spring에서는 이를 MultipartResolver를 이용해서 다음과같이 섞여있는 파라미터를 분리시킨다
	 * 문자열, 숫자 -> String 으로 구분
	 * 파일		->MultipartFile로 구분
	 * ->MultipartFile에 파일이 담겨있다!!
	 * 
	 * */
	//파일 업로드 테스트 1
	/**
	 * @param uploadFile : 실제로 업로드한 파일 + 설정 내용이 담겨있다!
	 * @return
	 * @throws IOException 
	 * @throws IllegalStateException 
	 */
	@PostMapping("file/test1")
	public String fileUpload1(
			@RequestParam("uploadFile") MultipartFile uploadFile,
			//컨트롤러가 MultipartFile 형태로 업로드 된 파일을 얻어올 수 있다
			RedirectAttributes ra
			//input태그의 name속성
			//MultipartFile : 파일을 다루는 객체 ->MultipartFile에 파일이 담겨있다!!
			) throws IllegalStateException, IOException {
		//uploadFile에 실제로 업로드한 파일 + 설정 내용이 담겨있다!
		String path = service.fileUpload1(uploadFile);
		if(path !=null) {
			//파일이 저장되어 웹(인터넷)에서 접근할 수 있는 경로가 반환되었을 때 수행되는 if문
			ra.addFlashAttribute("path", path); //path 값을 그대로 돌려보내겠다 리다이렉트 한 곳으로
			
		}
		return "redirect:/myPage/fileTest";
	}
	/**파일 업로드 + DB
	 * @return
	 * @throws IOException 
	 * @throws IllegalStateException 
	 */
	@PostMapping("file/test2")
	public String fileUpload2(
			@RequestParam("uploadFile") MultipartFile uploadFile,
			//누가 올렸는지도 알고싶으니까 로그인멤버도 얻어오기(그 번호)
			@SessionAttribute("loginMember") Member loginMember,
			RedirectAttributes ra //redirect할 때 메시지 보내기 위해
			) throws IllegalStateException, IOException {
		//로그인한 회원의 번호(누가 파일을 업로드 했는지!)
		int memberNo = loginMember.getMemberNo();
		
		//파일 업로드하고 DB에 파일 정보를 INSERT할거여서
		//업로드 된 파일 정보를 DB에 INSERT후 결과 행의 개수를 반환 받을거여서 결과가 int
		int result = service.fileUpload2(uploadFile, memberNo);
		
		String message;
		if(result>0) {
			message="파일 업로드 성공!!";
		}else {
			message="파일 업로드 실패..";
		}
		ra.addFlashAttribute("message", message);
		return "redirect:/myPage/fileTest"; //파일 업로드 페이지로 리다이렉트
	}
	/**파일 목록 조회
	 * @param model
	 * @return
	 */
	@GetMapping("fileList")
	public String fileList(Model model //forward할 때 DB에서 조회한 파일 목록을 담아서 보내야 해서
			) {
		//파일 목록 조회 서비스 호출
		List<UploadFile> list = service.fileList(); //목록이니까 리스트로 여러 개 받아옴
		model.addAttribute("list", list);
		return "myPage/myPage-fileList"; //forward
	}
	/**여러 파일 한번에 업로드하는 두 방법
	 * @param aaaList
	 * @param bbbList
	 * @return
	 * @throws IOException 
	 * @throws IllegalStateException 
	 */
	@PostMapping("file/test3")
	public String fileUpload3(
			@RequestParam("aaa") List<MultipartFile> aaaList, //배열이나 리스트로 얻어옴
			@RequestParam("bbb") List<MultipartFile> bbbList,
			@SessionAttribute("loginMember") Member loginMember,
			RedirectAttributes ra
			) throws IllegalStateException, IOException {
		//aaa에서 파일 모두 미제출 시 -> 0,1번 인덱스 파일이 모두 비어있음
		//bbb에서 파일 아무것도 미제출 시 ->리스트가 비어있는 게 아니라 0번인덱스 파일이 비어있는거임
		//	(bbb는 리스트가 비어있는 건 아니고 한 칸은 담겨있는데 그게 빈칸일 뿐)
		int memberNo = loginMember.getMemberNo();
		//result == 삽입(업로드)된 파일 개수
		int result = service.fileUpload3(aaaList, bbbList,memberNo);
		String message;
		if(result ==0) {
			//업로드한 파일이 하나도 없는 경우
			message="업로드된 파일이 없습니다.";
		}else {
			message=result+"개 파일이 업로드 되었습니다.";
		}
		ra.addFlashAttribute("message", message);
		return "redirect:/myPage/fileTest"; //redirect
	}
	/**프로필 이미지 변경
	 * @param profileImg
	 * @param loginMember
	 * @param ra
	 * @return
	 * @throws IOException 
	 * @throws IllegalStateException 
	 */
	@PostMapping("profile")
	public String profile(
			@RequestParam("profileImg") MultipartFile profileImg,
			@SessionAttribute("loginMember") Member loginMember, 
			//객체 자체를 가져오는 것이 아닌, 세션에 있는 원본의 주소를 가져옴
			//얕은 복사이기 때문에 그 주소를 가져와서 값을 수정하면 원본도 바뀐다
			//누구의 프로필 이미지 바꾸고 싶은지도 알아야 함
			RedirectAttributes ra
			
			) throws IllegalStateException, IOException {
		
		//로그인한 회원 번호
		int memberNo = loginMember.getMemberNo();
		
		//서비스 호출
		// -> /myPage/profile/변경된파일명 형태의 문자열을
		//		현재 로그인한 회원의 PROFILE_IMG 컬럼 값으로 수정해야 함(UPDATE)
		int result = service.profile(profileImg, loginMember);
		//파일 업로드 할 때에는 테이블 새로 만들어서 삽입하는 식으로 했다
		//서버에 저장할 때에는 이름을 바꿔서 해야 함
		
		//근데 파일과 다르게 프로필 이미지는 클라이언트가 프로필 이미지 다운받을 필요 없다
		//PROFILE_IMG에 /myPage/test/사진파일.jpg
		//원래 있던 회원의 PROFILE_IMG를 위의 모양의 문자열로 만든 것으로 바꿔야 함 UPDATE
		String message=null;
		if(result>0) {
			message="변경 성공!!!";
			//세션에 저장된 로그인 회원 정보에서
			//프로필이미지 수정한 경로가 
		}
		else message="변경 실패...";
		ra.addFlashAttribute("message", message);
		return "redirect:profile";
	}
}
