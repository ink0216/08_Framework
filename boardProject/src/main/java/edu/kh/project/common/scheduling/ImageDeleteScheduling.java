package edu.kh.project.common.scheduling;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import edu.kh.project.common.scheduling.service.SchedulingService;
import lombok.extern.slf4j.Slf4j;

@Component //스프링이 알아서 활용 해서 일정 시간마다 수행하도록
@Slf4j //어떤 파일이 지워졌는 지 로그로 남기려고
@PropertySource("classpath:config.properties") //여기의 값을 가져다 쓰겠다
public class ImageDeleteScheduling {
	//회원이 현재 사용하고 있는 프로필 이미지 제외한 파일들은 지우기
	
	@Autowired //서비스 bean 의존성 주입 (Dependency Injection)
	private SchedulingService service;
	
	@Value("${my.profile.folder-path}")
	private String profileLocation; //프로필 이미지 저장 경로
	
	@Value("${my.board.folder-path}")
	private String boardLocation; //게시글 이미지 저장 경로
	
//	@Scheduled(cron="0/10 * * * * *") //0초 기준으로 10초 마다 (테스트 용도)
	@Scheduled(cron="0 0 * * * *") //매 정시마다(0분 0초 마다) (실제 운영 시)
	public void imageDelete() {
		// 1. 서버 폴더 지정/연결(java.io.File 이용)
		File profileFolder = new File(profileLocation);
		File boardFolder = new File(boardLocation);
		
		// 2. 서버에 저장된 파일 목록 조회
		File[] profileArr = profileFolder.listFiles();//파일 배열
		File[] boardArr = boardFolder.listFiles();//파일 배열
		
		// 3. 배열 -> List로 변환하기 (다루기 편하게)
		List<File> profileList = Arrays.asList(profileArr);
		//매개변수에 있는 배열을 List로 변환해주는 메서드
		List<File> boardList = Arrays.asList(boardArr);
		
		// 4. 비어있는 리스트를 만들어서 
		//		변환된 두 개의 리스트를 한 곳에 저장시키기
		List<File> serverImageList = new ArrayList<>();
		
		serverImageList.addAll(profileList);
		serverImageList.addAll(boardList); //다 합쳐졌다
		
		log.info("------ 서버 이미지 목록 조회 성공 ------");
		
		// 5. DB에 저장된 회원 프로필, 게시글 이미지 목록을 조회하기
		List<String> dbImageList = service.selectImageList();
		// serverImageList, dbImageList 두 개가 준비된다
		// 둘을 비교해서 동일하게 있는 애들은 놔두고, 둘 중 하나에만 있는 요소만 모은 리스트를 따로 만든다 -> 얘네들이 삭제할 목록이 될 것이다
		
		// 6. DB 이미지가 존재하지 않을 경우
		if(dbImageList.isEmpty()) {
			log.info("---- DB이미지가 없어서 이미지 삭제 스케줄러 종료했다 ----");
			return;
		}
		
		// 7. 서버 이미지 목록 순차 접근
		for(File server : serverImageList) {
			
			// 8. 서버 이미지가 DB 이미지 목록에 존재하지 않는 경우
			if(!dbImageList.contains(server.getName())) {
				// File.getName() : 파일명만 얻어옴
				//파일의 이름을 가져와서 그게 db에 없는지 검사
				
				// 9. DB에 없는 서버 이미지를 삭제하겠다
				log.info("{} 삭제", server.getName());
				//중괄호 자리에 뒤에 작성된 변수의 값이 하나씩 대입되는 작성법 -> 어떤 파일이 삭제되는지 눈에 보이고
				server.delete(); //실제로 삭제된다
			}
		}
		log.info("----- 서버 이미지 파일 삭제 스캐줄러 동작 종료 -----");
		
		
		
	}
	
}
