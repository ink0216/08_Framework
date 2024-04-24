package edu.kh.travel.board.model.dto;

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

	//BOARD 테이블 컬럼
	private int boardNo;
	private String boardTitle;
	private String boardContent;
	private String writeDate;
	private int readCount;
	private String boardDelFl;
	private int memberNo;
	private int boardCode;
	
	//MEMBER 테이블 조인
	private String memberNickname;
	
	//목록 조회 시 상관 서브 쿼리 결과
	private int commentCount;
	private int likeCount;
	
	
	//--추가 예정
	//게시글 상세 조회 시 게시글 작성자 프로필 이미지 미리보기
	private String profileImg; //MEMBER테이블에 있다
	
	//게시글 목록 썸네일 이미지만 저장할 것
	private String thumbnail;
	
	//추가 예정
	//특정 게시글에 작성된 댓글 목록
	
}
