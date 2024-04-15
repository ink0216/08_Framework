package edu.kh.project.board.model.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Board {
	//첫 번째 select
	//BOARD 테이블 컬럼
	private int boardNo; 
	private String boardTitle;
	private String boardContent;
	private String boardWriteDate;
	private String boardUpdateDate;
	private int readCount;
	private String boardDelFl;
	private int memberNo;
	private int boardCode;
	
	//MEMBER 테이블 조인
	private String memberNickname;
	
	//목록 조회 시 상관 서브쿼리 결과
	private int commentCount;
	private int likeCount;
	
	//게시글 목록에서 상세조회 -> 게시글 작성자의 프로필 이미지도 같이 볼거다
	private String profileImg; //MEMBER 테이블에 들어가있다
	
	//게시글 목록에 썸네일 보이게 하기
	//썸네일 이미지 저장할 것
	private String thumbnail;
	
	//특정 게시글 이미지 목록
	private List<BoardImg> imageList; //두 번째 select
	
	//특정 게시글에 작성된 댓글 목록 조회해서 여기 다 담아놓기
	private List<Comment> commentList; //세 번째 select 담김
	
	//좋아요 눌렀는 지 여부 확인하는 필드
	private int likeCheck;
}
