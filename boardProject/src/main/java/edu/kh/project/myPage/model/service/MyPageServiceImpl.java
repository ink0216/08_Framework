package edu.kh.project.myPage.model.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import edu.kh.project.common.util.Utility;
import edu.kh.project.member.model.dto.Member;
import edu.kh.project.myPage.model.dto.UploadFile;
import edu.kh.project.myPage.model.mapper.MyPageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service //bean 등록
@Transactional(rollbackFor = Exception.class) //모든 예외 발생 시 롤백하라는 뜻!(checked, unchecked가리지 않고) 
//수정 삭제 이런거 할거니까(그냥 하면 Unchecked만 처리해서 rollbackFor 속성 이용해서 예외 범위 수정)
//RuntimeException이 UncheckedException의 최상위 부모!!
@RequiredArgsConstructor //final 필드(mapper)의 값을 의존성 주입해주는 코드를 자동완성해주는 어노테이션!!!!
@PropertySource("classpath:/config.properties") //config.properties에 있는 값을 여기 가져와서 쓸거라는 어노테이션
public class MyPageServiceImpl implements MyPageService{
	private final MyPageMapper mapper;
	
	//BCrypt 암호화 객체 의존성 주입(Security Config 참고)
	//@Autowired //이건 권장 X -> @RequiredArgsConstructor를 쓰는 게 좋은데 쓸려면 final 필드여야 한다!
	private final BCryptPasswordEncoder bcrypt;
	
	//@RequiredArgsConstructor를 이용했을 때 자동 완성되는 구문
//	@Autowired
//	public MyPageServiceImpl(MyPageMapper mapper) {
//		this.mapper=mapper;
//	}
	@Value("${my.profile.web-path}")
	private String profileWebPath; // /myPage/profile/
	
	@Value("${my.profile.folder-path}")
	private String profileFolderPath; //  C:/uploadFiles/profile/
	
	
	//회원 정보 수정
	@Override
	public int updateInfo(Member inputMember, String[] memberAddress) {
		//입력된 주소가 있을 경우 memberAddress를 A^^^B^^^C 형태로 가공
		//주소 입력 X -> inputMember.getMemberAddress() -> ",," 이런 모양으로 돼있다
		if(inputMember.getMemberAddress().equals(",,")) {
			//주소 입력이 안됐을 경우
			inputMember.setMemberAddress(null); //주소에 null대입
		}else {
			//주소가 입력된 것이 있다 -> memberAddress를 A^^^B^^^C 형태로 가공
			//String.join ==문자열이나 배열이 넘어오면 사이사이에 앞의 기호를 구분자로서 넣는다 
			String address = String.join("^^^", memberAddress);
			
			//주소에 가공된 데이터 대입
			inputMember.setMemberAddress(address);
			//이제 inputMember에는 번호, 주소, 닉네임, 전화번호가 들어있다
		}
		//SQL 수행 후 결과 반환
		return mapper.updateInfo(inputMember);
	}
	//비밀번호 변경
	@Override
	public int changePw(int memberNo, Map<String, Object> paramMap) {
		
		int result=0;
		//해당 회원의 ,저장돼있는 암호화된 비밀번호 조회
		String beforePw = mapper.selectPw(memberNo);
		
		if(bcrypt.matches((String)paramMap.get("currentPw"), beforePw)) {
			//(String)paramMap.get("currenPw")는 Object타입이니까 String 타입으로 강제형변환
			//matches 는 String타입을 비교
			//일치하면
			//MemberService의 회원 가입 서비스 참고하기!!!(암호화해서 저장해야 함)
			String encPw = bcrypt.encode((String)paramMap.get("newPw")); //새 비밀번호 암호화
			//얘도 Object 타입이어서 String으로 형변환 해야 한다
			
			//새 비밀번호로 변경(MAPPER)하는 Mapper 호출
			//Mapper에 전달 가능한 파라미터는 1개 뿐!!!
			//매퍼에는 하나만 보낼 수 있어서 두개를 보내야 하면 묶어서 보내기!!!!
			paramMap.put("encPw", encPw);
			paramMap.put("memberNo", memberNo);
			
			result = mapper.changePw(paramMap);
		}else {
			//일치하지 않으면
			result=0;
		}
		
		return result;
	}
	
	//회원 탈퇴
	@Override
	public int secession(String inputPw, int memberNo) {
		//기존의 저장돼있던 비밀번호 조회
		String beforePw = mapper.selectPw(memberNo); //암호화된 비밀번호
		int result=0;
		if(bcrypt.matches(inputPw, beforePw)) {
			//일치하는 경우
			return mapper.secession(memberNo);
		}
		return 0;
	}
	//파일 업로드 테스트 1
	@Override
	public String fileUpload1(MultipartFile uploadFile) throws IllegalStateException, IOException {
		//MultipartFile이 제공하는 여러 메서드 중 많이 쓰는 것!
		// - getSize() : 파일 크기 알 수 있다
		// - isEmpty() : 업로드한 파일이 없을 경우 true, 있을 경우 false
		// - getOriginalFileName() : 원본 파일명 얻어올 수 있다
		// - transferTo(경로) : 메모리 또는 임시 저장 경로에 업로드 된 파일을
		//						원하는 경로에 전송(서버 어떤 폴더에 저장할 지 지정)
		//					(원래는 임시 경로에 저장돼있었는데 실제 폴더 어디에다가 저장할 지 지정)
		
		//업로드한 파일이 있는지 없는지 검사하기
		if(uploadFile.isEmpty()) {
			//업로드한 파일이 없을 경우
			return null; //파일 없으면 밑의 코드들 실행할 필요 없음
		}
		//업로드 된 파일이 있을 경우
		//transferTo 이용! 그 파일을 서버 컴퓨터 어디에 저장할 지 
		uploadFile.transferTo(
				new File("C:\\uploadFiles\\test\\"+uploadFile.getOriginalFilename())); 
		//C:\\uploadFiles\\test\\파일명 으로 서버에 저장 
		//파일명에 확장자도 같이 저장돼있다
		//예외 던지기
		//path객체 말고 file 객체 쓰기
		//근데 여기까지만 하면 클라이언트가 서버에 파일 업로드(저장)하는 것만 되고 
		//서버에 저장된 파일 다운로드 받아가는 건 안돼서
		//FileConfig에서 addResourceHandlers를 오버라이딩 해서 특정 요청 주소와 서버 내 폴더를 연결해둠 
		//-> 클라이언트가 그 요청 보내면 서버컴퓨터의 자원을 다운로드 받아갈 수 있다
		
		//웹에서 해당 파일에 접근할 수 있는 경로를 반환
		//서버 : C:\\uploadFiles\\test\\a.jsp
		//웹 접근 주소 : /myPage/file/a.jpg 
		//해당 주소 요청 보내면 저 위치의 파일을 보이게 할거다???
		//저기다 저장할 건데 거기에 접근할 수 있는 주소를 이렇게 만든다
		return "/myPage/file/"+uploadFile.getOriginalFilename(); //이 경로를 돌려보내주겠다
	}
	//파일 업로드 테스트 2 + DB
	@Override
	public int fileUpload2(MultipartFile uploadFile, int memberNo) throws IllegalStateException, IOException {
		//업로드 된 파일이 있는지 없는지 검사하기
		if(uploadFile.isEmpty()) {
			//업로드 된 파일이 없다면 == 파일 아무것도 선택 안하고 제출 눌렀을 때
			return 0; //여기서 메서드 끝내기
		}
		//업로드 된 파일이 있는 경우
		//서버에 업로드 + DB에 저장
		//DB에 파일을 통째로 저장하지 않고 
		//DB에 FILE을 
		//DB에 (B)LOB이라는 자료형 존재(2진코드 저장)
		//파일도 2진으로 저장돼서 DB에 파일을 저장할 수 있기는 한데 잘 안한다!!->권장 X
		//->DB가 아닌, 서버 컴퓨터의 특정 폴더에 저장하는 게 좋다!!
		/*DB에 파일 저장이 가능하긴 하지만
		 * DB 부하를 줄이기 위해서 
		 * 1) DB에는 서버에 저장할 경로를 저장(찾아가는 주소 -> 그거 보고 서버에 찾아가서 보는거다)
		 * 		(여기다가 저장할 예정~~~)
		 * 2) DB 삽입/수정 성공 후 서버의 해당 위치에 파일을 따로 저장
		 * 3) 만약에 파일 저장 실패 시 
		 * 		->예외 발생 시 -> @Transactional을 이용해서 rollback수행!
		 * 	DB에 정보 삽입/수정을 미리 해 놨는데 파일 저장 실패 시 DB에 INSERT 했던 데이터를 rollback을 해야한다
		 * 예외가 발생해야지 @Transactional가 rollback을 하고, 발생 안하면 자동으로 커밋된다
		 * @Transactional는 안써도 자동으로 커밋 되고, @Transactional는 롤백을 하도록 지정하기 위해 사용!!!
		 * */
		
		// 1. 서버에 저장할 파일 경로 만들기
		//파일이 저장될 서버 폴더 경로
		String folderPath = "C:\\uploadFiles\\test\\"; //이 폴더에 접근할 수 있는
		
		//클라이언트가 파일이 저장된 서버컴퓨터의 폴더에 접근할 수 있는 요청 주소!!
		String webPath="/myPage/file/"; //요청 주소
		
		// 2. DB에 전달할 데이터를 Map으로 묶어서 INSERT 호출하기
		//4개를 묶어야 한다
		//webPath, memberNo, 원본파일명, 변경된 파일명
		//Utility 클래스 이용!
		String fileRename  //변경된 파일명
		= Utility.fileRename(uploadFile.getOriginalFilename()); //static 메서드여서 클래스명으로 호출 가능
		//원본 파일명을 보내면 변경된 파일명이 돌아올것이다
		
		//DTO에 담기
		//@Builder 각각의 필드 초기화해줌, 이어쓰면 된다
		//Builder 패턴을 이용해서 UploadFile 객체를 생성 & 메서드 이어서 씀(method chaining)
		// 장점 1) 반복되는 참조변수명, set 구문을 생략할 수 있다.
		// 장점 2) method chaining을 이용해서 한 줄로 작성 가능하다
		UploadFile uf = UploadFile.builder().memberNo(memberNo)
											.filePath(webPath)
											.fileOriginalName(uploadFile.getOriginalFilename())
											.fileRename(fileRename)
											.build(); 
		
		//매퍼로 uf 보내기
		int result = mapper.insertUploadFile(uf);
		
		if(result==0) {
			//삽입 실패 시
			return 0;
		}
		// 3. 삽입 INSERT 성공 시 파일을 지정된 서버 폴더에 저장하기
		uploadFile.transferTo(new File(folderPath+fileRename)); //임시 저장소에 있었는데 경로에 저장할거야
		// -> 여기서 파일 올리다가 예외 발생하면 에러 페이지로 넘어가게 만들었다
		
		//  C:\\uploadFiles\\test\\변경된파일명 으로 파일을 서버에 저장하겠다
		//근데 이거 쓰면 예외가 발생한다
		//빨간줄 뜨면 checked Exception이다 (IOException 발생한것!)
		//CheckedException 발생 -> 예외 처리 필수!
		//@Transactional은 Unchecked Exception만 처리하게 돼있다 -> 롤백 못함
		//	->rollbackFor 속성을 이용해서 롤백의 대상이 되는 예외의 범위를 수정해야한다 
		
		return result; //성공 시 1이 리턴됨
	}
	//파일 목록 조회
	@Override
	public List<UploadFile> fileList() {
		return mapper.fileList();
	}
	//한 번에 여러 파일 업로드
	@Override
	public int fileUpload3(List<MultipartFile> aaaList, List<MultipartFile> bbbList, int memberNo)
			throws IllegalStateException, IOException {

		
		// 1. aaaList 처리
		int result1=0; //1번 결과 저장용
		//aaaList는 최대 파일 2개까지만 있을 수 있다(input이 두개여서)
		//업로드 된 파일이 없을 경우 부터 확인 -> 그걸 제외하고 업로드하겠다
		for(MultipartFile file : aaaList) {
			//하나씩 꺼내서
			if(file.isEmpty()) {
				//하나라도 파일이 비었을 경우
				continue; //다음거 검사해~
			}
			//fileUpload2() 메서드 호출
			//	-> 파일 하나 업로드 + DB INSERT까지 다 하고 int값 반환해줌
			result1+=fileUpload2(file, memberNo); //서비스가 다른 서비스 호출
			//두 개 보내주면 얘가 insert까지 다 해서 갖다줌
			
		}
		
		// 2. bbbList 처리
		int result2=0; //2번 결과 저장용
		
		for(MultipartFile file : bbbList) {
			//하나씩 꺼내서
			if(file.isEmpty()) {
				//하나라도 파일이 비었을 경우
				continue; //다음거 검사해~
			}
			//fileUpload2() 메서드 호출
			//	-> 파일 하나 업로드 + DB INSERT까지 다 하고 int값 반환해줌
			result2+=fileUpload2(file, memberNo); //서비스가 다른 서비스 호출
			//두 개 보내주면 얘가 insert까지 다 해서 갖다줌
			
		}
		return result1+result2; //합한 개수만큼 삽입되었습니다.
	}
	//프로필 이미지 변경
	@Override
	public int profile(MultipartFile profileImg, Member loginMember) throws IllegalStateException, IOException {
		//수정할 경로
		String updatePath=null;
		
		//변경명 저장
		String rename = null;
		//업로드한 이미지가 있을 경우
		//파일을 저장하고 있는 객체 : profileImg
		if(!profileImg.isEmpty()) {
			//updatePath를 조합하기 -> 없을 경우에는 조합이 안돼서 null이 들어감
			//myPage/profile/변경된파일명 형태의 문자열
			//Config에 따로 써서 얻어오는 식으로 할거다(매번 바뀔 수 있어서)
			
			//파일명 변경
			rename=Utility.fileRename(profileImg.getOriginalFilename());
			
			updatePath=profileWebPath+rename;
			// /myPage/profile/변경된파일명.jpg 이런 모양 됐다!
		}
		
		
		
		
		//수정된 프로필 이미지 경로, 회원 번호를 한 번에 저장할 DTO 객체
		Member mem = Member.builder()
							.memberNo(loginMember.getMemberNo())
							.profileImg(updatePath)
							.build(); //객체 만들어짐
		
		//UPDATE하는 SQL 수행
		int result = mapper.profile(mem);
		
		if(result>0) {
			//수정 성공 시
			//파일을 서버의 지정된 폴더에 저장하기
//			profileImg.transferTo(new File(폴더경로+변경명));
			
			//profileIMG를 null로 바꾸려면
//			profileImg에 내용이 없어 ->그럼 updatePath가 null이 되고
			//그걸로 수정하면 result가 1이 나옴
			//rename은 업로드한 사진이 없는 경우 null임
			//폴더에 null 파일을 넣을 거야 말이 안되니까( profileImg.transferTo(new File(profileFolderPath+rename)); )
//			if문으로 감싸자
			if(!profileImg.isEmpty()) { //프로필 이미지를 없앤 경우(NULL로 수정한 경우)를 제외
				//->업로드한 이미지가 있을 경우에만 서버에 저장하겠다는 코드 쓸거다
				profileImg.transferTo(new File(profileFolderPath+rename));
			}
			

			//loginMember에는 세션에 저장돼있는 객체의 주소만 얕은복사돼있어서
			//여기서 값 변경 시 원본의 값도 변한다
			// 세션 회원 정보에서 프로필 이미지 경로를
			//업데이트한 경로로 변경 -> 세션에 있는 원본의 값도 이걸로 바뀐다
			loginMember.setProfileImg(updatePath); //바꿨으면 세션에 바꾼 값 세팅
			// null로 업데이트 했으면(사진 선택 X) null로 세팅
			//null이면 기본 이미지 출력됨
		}
		
		
		return result;
	}
}
