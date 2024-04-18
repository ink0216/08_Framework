/*게시글 좋아요 버튼(하트)클릭 시 비동기로 좋아요 INSERT/DELETE */
//회원번호, 게시글 번호 필요하니까 얻어와야 함!
//1) 로그인 한 회원의 번호 얻어오기
//      ->session에서 얻어오기
//      ->근데 session은 서버에서 관리하기 때문에 JS로 바로 얻어올 수 없다(비동기로 얻어올 수 있는데 그렇게 안함)
//      ->타임리프 이용하기!!!!!
//  타임리프, jsp : 템플릿 엔진(화면을 쉽게 만들어주는 도구,기능)
// 자바 웹, Spring == 모두 Servlet 기반
// Servlet(Java) : 요청 받으면 응답 화면 만들어서 출력해주는 역할
// Servlet(Java)
//HTML에서 Java 코드를 쓸 수 있는 것처럼 쓰지만 타임리프는 결국에 java 파일로 변한다
//jsp(단독 실행 불가능한 단점)는 확장자를 .jsp로 했지만
//타임리프는 확장자가 .html이어서 단독 실행 가능하다
//타임리프도 .html이지만 컴파일 되면 java 파일이 된다!!!

// Thymeleaf는 

//html 코드(css,js) 
//th:로 시작하는 타임리프 코드(java코드로 결국 변환된다)
//Spring El(${}, *{})

//로 이루어져 있음
// Thymeleaf에서 코드 해석 순서
// 1. th:코드 + Spring El(변수 선언하듯이 이들이 먼저 해석됨) -> 자바 코드가 먼저 해석돼서
//                                                          ->자바에서 값을 얻어와놔서 그 값을 
//                                                              나중에 해석될 html에서 사용할 수 있다
// 2. html 코드(css,js) 
//2) 현재 게시글 번호 얻어오기

//3) 좋아요 여부 얻어오기
// 이 세개 다 boardDetail.html의 script 태그에서 해놨다

// 1. #boardLike가 클릭되었을 때 
const boardLike = document.querySelector("#boardLike");
boardLike.addEventListener("click", e=>{
    //2. 로그인 여부 검사(좋아요 클릭은 로그인한 회원만 할 수 있게)
    //로그인 상태가 아닌 경우 동작 X
    if(loginMemberNo == null){
        alert("로그인 후 이용해 주세요");
        return;
    }
    //로그인 한 경우
    //3. 준비된 3개의 변수를 객체로 저장(이따 JSON으로 변환할 예정)
    const obj = {
        "memberNo" : loginMemberNo,
        "boardNo" : boardNo,
        "likeCheck" : likeCheck

    };

    //4. 좋아요 INSERT/DELETE를 하나의 요청을 보내기
    //likeCheck 값에 따라서 INSERT를 할 지 DELETE를 할지 정하기
    fetch("/board/like",{
        method : "POST",
        headers : {"Content-Type" : "application/json"},
        body : JSON.stringify(obj)
        /* 객체를 JSON으로 문자열화해서 전달한다! */
    })
    .then(resp=>resp.text()) //INSERT,DELETE의 결과 숫자를 text(글자)로 변환
    .then(count=>{
        //count == 첫 번째 then의 파싱되어 반환된 값인데 문자열 형태로 '-1' 또는 게시글 좋아요 수가 들어있을거다
        console.log("count : ", count);

        if(count== -1){
            console.log("좋아요 처리 실패");
            return;
        }
        //5. likeCheck 값을 0 <->1 바꾸기
        //(왜 ? 그래야지 클릭 될 때마다 INSERT/DELETE 동작을 번갈아가며 할 수 있어서)
        likeCheck = likeCheck==0 ? 1 : 0; //toggle처럼
        //0이었으면 1로 바꿔. 아니면 0으로 바꿔
        //삼항연산자에 따라 대입해

        //6. 화면에 하트 채웠다가 비웠다가 -> toggle이용
        // e.target == 하트버튼
        e.target.classList.toggle("fa-regular");
        e.target.classList.toggle("fa-solid");
        //fa-regular 랑 fa-solid는 공존할 수 없음

        //7. 게시글 전체 좋아요 수 업데이트하기
        e.target.nextElementSibling.innerText=count;


    });
});
/*삭제 버튼이 자기가 쓴 글일 때에만 나오도록 돼있다
게시글 삭제
-1. 삭제 버튼 클릭 시 "정말 삭제하시겠습니까?" -> 확인/취소
	confirm쓰기
-2. 취소 클릭 시 alert("취소됨");
-3. 확인 클릭 시 /editBoard/{boardCode}/{boardNo}/delete로 get방식 요청하기
-4. 삭제는 진짜 삭제가 아니라, {boardCode} 게시판의 {boardNo} 글의 BOARD_DEL_FL 값을 'Y'로 변경
-5. 변경 성공 시 -> 해당 게시판 목록 1페이지로 리다이렉트하기
	변경 실패 시 -> 원래 보고있던 글 상세조회로 리다이렉트 */
/* 삭제(GET) */
const deleteBtn = document.querySelector("#deleteBtn");

if(deleteBtn != null){
    deleteBtn.addEventListener("click", () => {
    if( !confirm("삭제 하시겠습니까?") ) {
        alert("취소됨")
        return;
    }
    // location.pathname : /board/1/2010 이렇게 요청주소만 나온다!
    // 근데 요청 보내야 하는 주소는 /editBoard/1/2010/delete로 만들어야하니까
    //고쳐나가기
    // board를 editBoard로 고치고
    //실패할 때 원래 페이지로 돌아오려고쿼리스트링으로 cp 부여놓음
    const url = location.pathname.replace("board","editBoard") + "/delete"; // /editBoard/1/2000/delete
    const queryString = location.search; // ?cp=1
    location.href = url + queryString;
    });
}


/* 삭제(POST) */
// POST 방식 요청 방법 -1. form태그 이용 *******
//                      -2. ajax 방법 이용 
/*비동기요청은 보통 페이지 이동 없이 화면 일부만 바꾸고 싶을 때
요청을 받았을 때 화면 전환할 때는 동기식 이용!
게시글 삭제 하면 목록으로 가던지 상세조회하던지 페이지 이동하므로 동기식 !

삭제를 해야 할 때 form태그를 만들어서 제출
POST를 동기식으로 해야하는데 화면에 form 태그 만들어놓고 싶지는 않으면
필요할 때 
값 hidden타입으로
form.submit()하면 진짜 제출 된다

 */
const deleteBtn2 = document.querySelector("#deleteBtn2");

if(deleteBtn2 != null){
    deleteBtn2.addEventListener("click", () => {

    if( !confirm("삭제 하시겠습니까?") ) {
        alert("취소됨")
        return;
    }

    const url = location.pathname.replace("board","editBoard")  + "/delete"; 

    // form태그 생성
    const form = document.createElement("form");
    form.action = url;
    form.method = "POST";

    // cp값을 저장할 input 생성
    const input = document.createElement("input");
    input.type = "hidden";
    input.name = "cp";

    // 쿼리스트링에서 원하는 파라미터 얻어오기
    const params = new URLSearchParams(location.search); 
    //주소에 있는 파라미터만 다루는 객체 URLSearchParams
    //location.search 하면 쿼리스트링만 나오게 함
    
    const cp = params.get("cp"); //주소 쿼리스트링에 있는 cp값이 얼마냐 해서 값 꺼내올 수 있다
    input.value = cp;

    form.append(input);

    // 화면에 form태그를 추가한 후 제출하기
    document.querySelector("body").append(form);
    form.submit();
    });
}
//************************************게시글 수정 버튼********************************* */
/*updateBtn 수정 버튼이 클릭 되었을 때 수정 페이지로 전환 */
const updateBtn = document.querySelector("#updateBtn");

if(updateBtn !=null){
    //수정버튼이 존재할 때에만 클릭 이벤트 추가 (로그인이 되어있고 작성자일 경우)
    updateBtn.addEventListener("click", ()=>{
        // 목표 주소 : /editBoard/1/2010/update?cp=1 라는 주소로 요청 보내기(GET)

        //현재 주소 : /board/1/2010?cp=1
        location.href=location.pathname.replace('board', 'editBoard') //get방식
                        +"/update"+ location.search;
        // location.pathname ==  /board/1/2010 의 문자열!
        // 그 문자열의 board를 editBoard로 바꾸겠다

        //쿼리스트링은 location.search == ?cp=1 이렇게 현재 주소의 쿼리스트링 전체가 붙는다 (뒤에 쿼리스트링 더 있으면 다 붙는다)
        //쿼리스트링은 검색할 때 사용하는 거다
    });
}
//------------------------------------------------------------------
//목록으로 버튼 클릭 시
const goToListBtn = document.querySelector("#goToListBtn");
goToListBtn.addEventListener("click", ()=>{
    //상세조회할 때 없을 일 없으니까 if문으로 안감싸도 된다

    //상세 조회 주소를 목록 주소로 바꾸면 된다!!!
    //상세 조회 : /board/1/2011?cp=1
    
    //목록 : /board/1?cp=1
    //location.pathname == '/board/1/2011'
    //location.search == '?cp=1' (쿼리스트링만 나옴)

    // '/board/1/2011'.subString(0,8) : String이면 subString가능!!
    // 마지막 / 인덱스 찾는 것 lastIndexOf 사용!
    let url = location.pathname;
    url= url.substring(0,url.lastIndexOf("/"));
    //url의 0번부터 url의 마지막 /가 있는 인덱스까지 자름
    location.href=url+location.search; //쿼리스트링


});















