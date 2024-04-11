package edu.kh.project.board.model.dto;
/*Pagination 뜻 : 목록 조회 시 목록을 일정 페이지 씩 분할해서 원하는 페이지를 볼 수 있게 만드는 것
 * 					== 페이징 처리(페이지네이션)
 *  Pagination 객체 : 페이징 처리에 필요한 값을 모두 모아두고, 계산하는 객체
 * 
 * 이걸 서비스 임플에서 호출할거다
 * 
 * */
public class Pagination {
	   private int currentPage;      // 현재 페이지 번호(cp)(파라미터로 전달받음)
	   private int listCount;         // 전체 게시글 수
	   
	   private int limit = 10;         // 한 페이지 목록에 보여지는 게시글 수
	   private int pageSize = 10;      // 밑에 보여질 페이지 번호의 개수
	   
	   //이 밑의 다섯 개는 계산할거다
	   private int maxPage;         // 마지막 페이지 번호
	   private int startPage;         // 보여지는 맨 앞 페이지 번호 
	   private int endPage;         // 보여지는 맨 뒤 페이지 번호
	   
	   private int prevPage;         // 이전 페이지 모음의 마지막 번호 
	   private int nextPage;         // 다음 페이지 모음의 시작 번호 
	   
	   //기본생성자는 안쓸거다
	   
	   //생성자
	public Pagination(int currentPage, int listCount) {
		super();
		this.currentPage = currentPage;
		this.listCount = listCount;
		calculate(); //새 값 들어올 때마다 필드 계산 메서드 호출
	}
		//생성자
	public Pagination(int currentPage, int listCount, int limit, int pageSize) {
		super();
		this.currentPage = currentPage;
		this.listCount = listCount;
		this.limit = limit;
		this.pageSize = pageSize;
		calculate(); //새 값 들어올 때마다 필드 계산 메서드 호출
	}
	//----------------------------------------------------------------------
	//getter
	public int getCurrentPage() {
		return currentPage;
	}
	public int getListCount() {
		return listCount;
	}
	public int getLimit() {
		return limit;
	}
	public int getPageSize() {
		return pageSize;
	}
	public int getMaxPage() {
		return maxPage;
	}
	public int getStartPage() {
		return startPage;
	}
	public int getEndPage() {
		return endPage;
	}
	public int getPrevPage() {
		return prevPage;
	}
	public int getNextPage() {
		return nextPage;
	}
	//----------------------------------------------------------------------
	//setters (나머지 다섯 개는 계산해서 자동으로 넣도록 만들거다)
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
		calculate(); //새 값 들어올 때마다 필드 계산 메서드 호출
		//현재 페이지가 바꼈으면 다시 계산해야함
	}
	public void setListCount(int listCount) {
		this.listCount = listCount;
		calculate(); //새 값 들어올 때마다 필드 계산 메서드 호출
		//이게 바뀌면 계산 다시해야함
	}
	public void setLimit(int limit) {
		this.limit = limit;
		calculate(); //새 값 들어올 때마다 필드 계산 메서드 호출
		//이게 바뀌면 계산 다시해야함
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
		calculate(); //새 값 들어올 때마다 필드 계산 메서드 호출
		//이게 바뀌면 계산 다시해야함
	}
	//----------------------------------------------------------------------
	@Override
	public String toString() {
		return "Pagination [currentPage=" + currentPage + ", listCount=" + listCount + ", limit=" + limit
				+ ", pageSize=" + pageSize + ", maxPage=" + maxPage + ", startPage=" + startPage + ", endPage="
				+ endPage + ", prevPage=" + prevPage + ", nextPage=" + nextPage + "]";
	}
	
	//----------------------------------------------------------------------
	/**
	 * 페이징 처리에 필요한 값을 계산해서
	 * 필드에 대입하는 메서드 (private이니까 여기에서만 호출 가능)
	 * (maxPage, startPage, endPage,  prevPage, nextPage 계산함)
	 */
	private void calculate() {
		/*maxPage : 총 페이지 수 == 최대 페이지*/
		//한 페이지에 게시글이 10개 씩 보여질 것이고
		//총 게시글이 95개  -> 10페이지
		//총 게시글이 100개 -> 10페이지
		//총 게시글이 101개 -> 11페이지
		maxPage = (int)Math.ceil((double)listCount/limit);
		//limit == 10
		//(double) 없으면 listCount/limit -> 95/10 == 9 (자바의 값 처리 원칙, int끼리 계산하면 결과도 int)
		//(double) 있으면 listCount/limit -> 95.0/10 == 9.5
		//올림하면 10.0
		//int로 하면 10
		
		//startPage : 페이지 번호 목록의 시작 번호
		//페이지 번호 목록이 10(pageSize)개 씩 보여질 경우
		//현재 페이지가 1~10 사이일 경우 startPage ==1
		//현재 페이지가 11~20 사이일 경우 startPage ==11
		startPage = (currentPage-1)/pageSize*pageSize+1;
		//5페이지 보고있으면 
		//4/10*10+1
		// 4/10 하면 0임!
		
		//10 ->1페이지
		
		//11 -> 10/10==1
		
		//endPage : 페이지 번호 목록의 끝 번호
		//현재 페이지가 1~10 사이일 경우 endPage ==10
		//현재 페이지가 11~20 사이일 경우 endPage ==20
		endPage=startPage+pageSize-1; //pageSize가 나중에 바뀔 수 있으므로
		if(endPage>maxPage) endPage=maxPage; //페이지 끝 번호가 최대 페이지를 초과하는 경우, 최대 페이지에서 끝나도록 대입
		//최대 48페이지까지만 존재하면
		//50페이지가 끝이 아니라 48페이지가 끝이 돼야함
		
		//prevPage : "<" 클릭 시 이동할 페이지 번호
		//			(이전 페이지 목록 번호 중 끝 번호)
		// 43페이지 보다가 < 클릭 시 40페이지로 이동하도록 만들기
		if(currentPage < pageSize) {
			//1~10페이지 보고 있을 경우
			// 더 이상 뒤로 갈 페이지가 없을 경우
			prevPage=1;
		}else {
			//앞으로 갈 페이지가 있다는 뜻이넹
			prevPage = startPage-1;
		}
		
		//nextPage : ">" 클릭 시 이동할 페이지 번호
				//			(다음 페이지 목록 번호 중 시작 번호)
		if(endPage==maxPage) { //더 이상 뒤로 넘어갈 페이지가 없을 경우
			nextPage = maxPage;
		}else {
			nextPage = endPage+1;
		}
		
		
	}
}
