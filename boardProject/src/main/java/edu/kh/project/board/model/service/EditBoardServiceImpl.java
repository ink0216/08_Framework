package edu.kh.project.board.model.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.board.model.dto.BoardImg;
import edu.kh.project.board.model.mapper.EditBoardMapper;
import edu.kh.project.common.util.Utility;
import lombok.RequiredArgsConstructor;

@Service
@Transactional //얘는 RuntimeException이 발생 시 롤백을 해줌
@RequiredArgsConstructor
@PropertySource("classpath:/config.properties") //config.properties의 내용을 여기서 쓸거야
public class EditBoardServiceImpl implements EditBoardService{
	private final EditBoardMapper mapper;
	//config.properties 값을 얻어와 필드에 저장
	@Value("${my.board.web-path}")
	private String webPath; //이 값을 얻어와서 여기에 저장하겠다
	
	@Value("${my.board.folder-path}")
	private String folderPath;//이 값을 얻어와서 여기에 저장하겠다
	
	//게시글 작성
	public int boardInsert(Board inputBoard, List<MultipartFile> images) throws IllegalStateException, IOException {
		//inputBoard에는 제목,내용,작성자번호,boardCode이 담겨있어서 BOARD에 INSERT하고(선)
		//insert된 게시글의 boardNo를 얻어와서 그 번호에 이미지를 insert해야한다(후)
		
		// 1. 게시글 부분(inputBoard)을 먼저 
		//		BOARD 테이블에 INSERT하기
		//		->INSERT결과로 작성된 게시글의 번호(생성된 시퀀스 번호)를 반환 받을거다
		int result = mapper.boardInsert(inputBoard); //객체가 복사돼서 넘어가는 게 아닌, 객체의 주소만 얕은 복사해서 넘겨줌
		//result == INSERT 결과 (0 / 1 행 삽입)
		
		// Controller - Service -  Mapper - mapper.xml
		//Controller에서 만들어진 inputBoard객체!
		//그걸 Controller에서 Mapper까지 넘겨줄 때 모두 inputBoard의 "주소"를 넘겨주는거다!!
		// ->mapper.xml에서 <selectKey>태그를 이용해 생성된 boardNo가 inputBoard에 담겨있음!(얕은 복사)
		// inputBoard 객체의 주소를 여기까지 넘겨줬음
		if(result==0) {
			//실패한 경우
			return 0;
		}
			
		//INSERT 성공 시 boardNo를 꺼내서 쓸거다
		//삽입된 게시글의 번호를 변수로 저장하기
		int boardNo = inputBoard.getBoardNo();
		
		// 2. 업로드된 이미지가 실제로 존재할 경우
		// 		업로드 된 이미지만 별도로 저장하여
		//		"BOARD_IMG" 테이블에 삽입하는 코드 작성
		
		//업로드 된 게 몇개인지 모르니까 그것만 담을 리스트 만들기
		//실제 업로드 된 이미지의 정보만 모아둘 List 생성
		List<BoardImg> uploadList = new ArrayList<>(); //타입추론이어서 <>안에 BoardImg안써도 오류 안난다
		//무조건 5개가 다 차있어서 images.isEmpty() 못 쓴다!(업로드 안된 것도 빈칸으로 돼서 들어있음)
		
		//images 리스트에서 하나씩 꺼내어 실제로 업로드 된 파일이 있는 지 검사
		//향상된 for문 안쓴다
		for(int i=0; i<images.size(); i++) {
			//length는 배열에서 쓰는거고 컬렉션의 길이는 size를 쓴다
			if(!images.get(i).isEmpty()) {//실제 선택된 파일이 존재하는 경우
				//images에서 하나씩 꺼내면 MultipartFile이어서 isEmpty쓸 수 있다
				//IMAGE_PATH는 CONFIG.PROPERTIES에 적을거다 (유지보수를 쉽게 하기 위해서)
				
				//IMG_PATH == webPath
				//folderPath는 서버에 파일 저장할 때 쓸거다
				
				//BOARD_NO == boardNo(시퀀스로 만든 것)
				
				//원본명
				String originalName = images.get(i).getOriginalFilename(); //i번째 하면 MultipartFile임
				
				//변경명
				String rename = Utility.fileRename(originalName); //원본명 넣어주면 변경명 만들어졌다
				
				//IMG_ORDER == i (인덱스 == 순서)
				// 0,1,2,3,4번 인덱스가 무조건 제출되는데
				// 2,3번 인덱스에만 파일을 업로드한 경우
				// 화면 상에서 위치가 고정돼야한다
				
				//이걸 다 담을 DTO 만들기 (Builder 패턴 사용)
				BoardImg img=BoardImg.builder()
							.imgOriginalName(originalName)
							.imgRename(rename)
							.imgPath(webPath)
							.boardNo(boardNo)
							.imgOrder(i)
							.uploadFile(images.get(i))
							.build(); //모든 값을 저장한 DTO 생성
				//업로드한 파일이 있으면 실제 파일도 저장해둠
				uploadList.add(img);
			}
		}
		//이 for문 지났는데 uploadList가 비어있는 경우 == 업로드된 파일이 하나도 없는 경우
		if(uploadList.isEmpty()) {
			//실제 선택한 파일이 아무것도 없는 경우
			return boardNo;
		}
		//선택한 파일이 존재할 경우
		//	-> "BOARD_IMG" 테이블에 INSERT + 서버에 파일 저장하는 코드
		
		//uploadList가 몇 행 인지 모름 
		/*여러 행 삽입 방법
		 * 1) 1행 삽입하는 INSERT SQL을 for문을 이용해서 여러 번 호출하는 방법
		 * 2) 여러 행을 삽입하는 SQL을 1회 호출하는 방법(난이도 높다)(이거 사용!!)
		 * */
		//result 재활용
		result = mapper.insertUploadList(uploadList); //uploadList를 통쨰로 insert할거다
		//result == 삽입된 행의 개수 ( 예를 들어 2,3,5행)
		//		== uploadList.size()랑 같아야 함!!그래야 성공이다
		
		
		if(result == uploadList.size()) {
			//다중 INSERT 성공 확인
			// uploadList에 저장된 값이 모두 담겼는지
			//DB에 저장 완료했으니
			//서버에 파일 저장하는 코드 작성하기
			
			for(BoardImg img : uploadList) {
				//향상된 for문으로 돌리면서 
				//저장된 개수만큼 돌면서 
				//서버에 저장
//				img.getUploadFile() : 실제 업로드 된 파일
				//업로드 된 파일은 메모리나 임시 저장 폴더에 저장돼있는데 그걸 어디다 저장할거야
				
				//새 이름으로 저장할거야
				img.getUploadFile().transferTo(new File(folderPath+img.getImgRename()));
				
			}
		}else {
			//전부 다 삽입되지 않은 경우
			//위에서 게시글 글 삽입했는데 밑에서 이미지 삽입이 5개 중 4개만 된 경우(부분 실패)
			// -> 이 서비스는 실패한 서비스로 봐야 함
			// -> 이미 삽입된 데이터는 롤백해야함
			//부분적으로 삽입 실패한 경우 -> 전체 서비스 실패로 판단
			// -> 이전에 삽입된 내용 모두 rollback 해야한다
			// -> rollback 하는 방법 : @Transactional!!! 이용 -> RuntimeException을 강제 발생시키기!!!
			//throw new RuntimeException(); //근데 이렇게하면 내용이 안나오고 그냥 RuntimeException이라고 콘솔에 나와서 알 수없음
			//그럼 예외 발생하면서 모든 애들이 롤백된다
			
			throw new RuntimeException("이미지가 정상 삽입되지 않음");
		}
		return boardNo;
		
	}
	//게시글 삭제하기
	@Override
	public int delete(int boardCode, int boardNo) {
		Map<String, Integer> map = new HashMap<>();
		map.put("boardCode", boardCode);
		map.put("boardNo", boardNo);
		return mapper.delete(map);
	}
}
