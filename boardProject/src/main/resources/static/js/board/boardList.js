/* 글쓰기 버튼(로그인 했을 때에만 insertBtn이 존재하는데, 로그인 안했을 때엔 insertBtn 존재 안하는데
존재하지 않는 insertBtn에 이벤트리스너 추가한다고 쓰면 로그인 안했을 때 오류나서 if문으로 감싸기) 클릭 시 */
const insertBtn = document.querySelector("#insertBtn");
if(insertBtn !=null){
    //글쓰기 버튼이 존재할 때(로그인 상태인 경우)
    insertBtn.addEventListener("click", ()=>{
        // location.href="";
        /* boardCode얻어오는 방법 
        -1. @PathVariable("boardCode") 얻어와 boardList.html 밑 script 에 전역 변수 선언
        -2.location : js 주소 관련 객체
        location.pathname : 요청주소(uri)나옴( /board/2 )

        location.pathname.split("/")[2] */
        //alert(boardCode);
        location.href=`/editBoard/${boardCode}/insert`; //get방식
        // ``안의 ${}은 el구문 아니고, 변수명을 적는 것이다
    });

}