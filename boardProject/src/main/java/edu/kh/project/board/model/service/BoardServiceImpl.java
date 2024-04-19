package edu.kh.project.board.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.board.model.dto.Pagination;
import edu.kh.project.board.model.mapper.BoardMapper;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
//매퍼 의존성 주입(생성자 방식 이용) -> final 붙여서 해야한다!
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService{
	private final BoardMapper mapper; //final 붙은 애가 자동으로 bean으로 등록이 된다
//	원래 이거임
//	@Autowired
//	public BoardServiceImpl(BoardMapper mapper) {
//		this.mapper = mapper;
//	}
	
	//게시판 종류 목록 조회
	@Override
	public List<Map<String, Object>> selectBoardTypeList() {
		return mapper.selectBoardTypeList();
	}
	
	//특정 게시판의 지정된 페이지 목록 조회(공지 게시판의 2페이지 볼거다)
	@Override
	public Map<String, Object> selectBoardTypeList(int boardCode, int cp) {
		// 1. 지정된 게시판(boardCode)에서 삭제되지 않은 게시글 수를 조회 -> 그래야 총 몇 페이지 분량의 글이 있는 지 알 수 있어서!
		int listCount = mapper.getListCount(boardCode);
		
		// 2. 1번의 결과 + cp를 이용해서
		//		Pagination 객체를 생성
		//	Pagination 객체: 게시글 목록 구성에 필요한 값을 저장한 객체(별의 별 거를 다 저장할거다)
		Pagination pagination = new Pagination(cp, listCount); 
		//Pagination 이게 만들어지면서 그 객체에 값이 대입되면서 calculate가 실행되면서 모든 필드값들이 초기화된다
		//그렇게 만들어진 객체가 pagination 변수에 저장될거다
		
		// 3. 특정 게시판의 지정된 페이지 목록 조회(공지 게시판의 2페이지 볼거다)
//		Mybatis RowBounds 객체 -> offset(지정한 값, 건너뛸 값) + Limit(몇 개 조회할 지)
//		offset=20을 이용해서 조회 -> 20개 행의 범위를 건너뜀 -> 1~20은 몇 칸 건너뛰고 몇 개를 조회할 지
//
//		(offset, Limit)
//		1페이지 == 0,10
//		2페이지 == 10,10
//		3페이지 == 20,10
//		4페이지 == 30,10
		/*ROWBOUNDS 객체 (Mybatis(JDBC 프레임워크(긴 JDBC를 쉽게 쓰게 해주는 것)) 제공 객체)
		 * - 지정된 크기 (offset)만큼 건너뛰고
		 * 		제한된 크기(limit)만큼의 행을 조회하는 객체
		 * --->이걸 이용하면 페이징 처리가 굉장히 간단해짐!!!!
		 * */
		int limit = pagination.getLimit(); //limit 얻어오기
		int offset = (cp-1)*limit;
		//3페이지 보고싶으면 앞의 20개는 건너뛰고 열 개 조회하면 됨
		
		RowBounds rowBounds = new RowBounds(offset, limit); //몇 행 범위를 offset만큼 건너뛰고 몇 행만큼 볼 지
		//20, 10 -> 20개 건너뛰고 그 다음부터 10개 조회한다 ->이걸 가능하게 하는게 RowBounds이다!!!
		
		/*Mapper 메서드 호출 시
		 * - 첫 번째 매개변수 -> 무조건 SQL 에 전달할 파라미터가 됨
		 * - 두 번째 매개변수 -> RowBounds 객체 전달할 자리
		 * */
		List<Board> boardList = mapper.selectBoardList(boardCode, rowBounds);
		
		// 4. 목록 조회 결과 + Pagination 객체를 Map으로 묶음
		Map<String, Object> map = new HashMap<>();
		map.put("pagination", pagination);
		map.put("boardList", boardList);
		
		// 5. 결과 반환
		return map;
	}
	
	//게시글 상세 조회
	@Override
	public Board selectOne(Map<String, Integer> map) {
		//mapper 태그 하나 당 sql 하나씩만 수행 태그 하나 당 매퍼의 메서드 하나씩 연결돼있다
		//[여러 sql을 실행하는 방법]
		// 1) 하나의 Service 메서드에서
		//		여러 Mapper 메서드를 호출하는 방법(가장 쉬운 방법)
		
		// 2) 수행하려는 SQL이 
		//		-1. 모두 SELECT이면서
		//		-2. 먼저 조회된 결과 중 일부를 이용해 
		//			나중에 수행되는 SQL의 조건으로 삼는 경우
		//	-->Mybatis의 <resultMap>, <collection> 태그를 이용해
		//		Mapper 메서드 1회 호출로 여러 SELECT를 한 번에 수행할 수 있다
		//(특수한 조건일 때만 사용할 수 있지만 코드가 쉬운 방법)
		
		//상세조회하면 컬럼들이 나오는데 컬럼 값들 중에서 두 번째 SELECT문의 BOARD_NO를 이용해서
		//이미지 조회할 떄의 BOARD_NO에 넣어줄 수 있고
		//댓글 조회할 떄의 BOARD_NO에도 넣어줄 수 있다
		//첫 SQL 의 결과 중 일부를 이용해서 뒤의 SQL에 이용하는 경우이므로 이 상황에 해당됨
		return mapper.selectOne(map);
	}
	
//	게시글 좋아요 체크/해제
	@Override
	public int boardLike(Map<String, Integer> map) {
		int result=0; //결과 저장용 변수
		// 1. 이 회원이 이전에 좋아요를 체크한 상태인 경우
		//		likeCheck가 1일 때
		//		->BOARD_LIKE 테이블에 DELETE하기
		if(map.get("likeCheck")==1) {
			//map.get("likeCheck") 하면 Integer 타입이랑 int 타입을 비교하면
			//자동으로 포장을 벗기는 AutoUnboxing이 진행돼서 아무 문제 없이 된다
			//Integer 타입을 int타입으로 자동으로 바꿔서 1과 비교해준다
			result=mapper.deleteBoardLike(map);
		}
		// 2. 이 회원이 이전에 좋아요를 체크하지 않은 상태인 경우
		//		likeCheck가 0일 때
		//		->BOARD_LIKE 테이블에 INSERT하기
		else {
			result=mapper.insertBoardLike(map);
		}
		// 3. 다시 해당 게시글의 좋아요 개수를 조회해서 반환(해당 게시글의 전체 좋아요 개수 변함)
		// 여러 명이 동시에 할 수 있으므로
		if(result>0) {
			//게시글 좋아요 수 다시 조회해서 반환해주기
			return mapper.selectLikeCount(map.get("boardNo"));
		}
		//INSERT, DELETE가 실패한 경우
		return -1;
	}
	
	//조회 수 증가시키기
	@Override
	public int updateReadCount(int boardNo) {
		// 1. 조회 수 1 증가시키기
		int result = mapper.updateReadCount(boardNo);
		
		// 2. 현재 조회수를 다시 조회(여러 명이 동시에 조회할 수 있어서 따로 조회하겠다)
		if(result>0) {
			return mapper.selectReadCount(boardNo);
		}
		
		return -1; //실패한 경우 -1 반환
	}
	
	//검색 서비스 (게시글 목록 조회 참고하면 된다)
	@Override
	public Map<String, Object> searchList(Map<String, Object> paramMap, int cp) {
		// 1. 지정된 게시판(boardCode)에서 
		//		검색 조건에 맞으면서
		//		삭제되지 않은 게시글 수를 조회 -> 그래야 총 몇 페이지 분량의 글이 있는 지 알 수 있어서!
		
		// paramMap에 key, query, boardCode가 다 담겨있다
		int listCount = mapper.getSearchCount(paramMap);
		
		// 2. 1번의 결과 + cp를 이용해서
		//		Pagination 객체를 생성
		//	Pagination 객체: 게시글 목록 구성에 필요한 값을 저장한 객체(별의 별 거를 다 저장할거다)
		Pagination pagination = new Pagination(cp, listCount); 
		//Pagination 이게 만들어지면서 그 객체에 값이 대입되면서 calculate가 실행되면서 모든 필드값들이 초기화된다
		//그렇게 만들어진 객체가 pagination 변수에 저장될거다
		
		// 3. 지정된 페이지의 검색 결과 목록을 조회하기
//				Mybatis RowBounds 객체 -> offset(지정한 값, 건너뛸 값) + Limit(몇 개 조회할 지)
//				offset=20을 이용해서 조회 -> 20개 행의 범위를 건너뜀 -> 1~20은 몇 칸 건너뛰고 몇 개를 조회할 지
		//
//				(offset, Limit)
//				1페이지 == 0,10
//				2페이지 == 10,10
//				3페이지 == 20,10
//				4페이지 == 30,10
				/*ROWBOUNDS 객체 (Mybatis(JDBC 프레임워크(긴 JDBC를 쉽게 쓰게 해주는 것)) 제공 객체)
				 * - 지정된 크기 (offset)만큼 건너뛰고
				 * 		제한된 크기(limit)만큼의 행을 조회하는 객체
				 * --->이걸 이용하면 페이징 처리가 굉장히 간단해짐!!!!
				 * */
				int limit = pagination.getLimit(); //limit 얻어오기
				int offset = (cp-1)*limit;
				//3페이지 보고싶으면 앞의 20개는 건너뛰고 열 개 조회하면 됨
				
				RowBounds rowBounds = new RowBounds(offset, limit); //몇 행 범위를 offset만큼 건너뛰고 몇 행만큼 볼 지
				//20, 10 -> 20개 건너뛰고 그 다음부터 10개 조회한다 ->이걸 가능하게 하는게 RowBounds이다!!!
				
				/*Mapper 메서드 호출 시
				 * - 첫 번째 매개변수 -> 무조건 SQL 에 전달할 파라미터가 됨
				 * - 두 번째 매개변수 -> RowBounds 객체 전달할 자리
				 * */
				List<Board> boardList = mapper.selectSearchList(paramMap, rowBounds);
				
				// 4. 목록 조회 결과 + Pagination 객체를 Map으로 묶음
				Map<String, Object> map = new HashMap<>();
				map.put("pagination", pagination);
				map.put("boardList", boardList);
				
				// 5. 결과 반환
				return map;
	}
	
	
	
	
	
	
	
	
}
