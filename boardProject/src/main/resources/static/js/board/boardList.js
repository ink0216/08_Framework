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
        // ``안의 ${}은 el구문 아님!그냥 변수명을 적는 것이다
    });

}
//----------------------------------------------------------------------------------------------------------------------
//검색창 searchKey, searchQuery랑 모든 option태그들 다 얻어오기

/* 검색 관련된 요소 */
const searchKey = document.querySelector("#searchKey");
const searchQuery = document.querySelector("#searchQuery");
const options = document.querySelectorAll("#searchKey > option"); //배열


// 검색창에 이전 검색 기록을 남겨놓기

//즉시 실행 함수
// ( ()=>{} )();
//원래 함수는 정의 따로 호출 따로이다!
//function testFn(){}; 했다고 함수 수행되지 않고 밑에서 testFn()해야 호출되는데
//이거는 함수가 정의 되자마자 바로 실행되는것!!!
// - 장점 1. 변수명 중복을 해결하는 용도 
/*function A(){const temp;}
    function B(){const temp;}
    이 둘은 변수 중복 아님 -> 지역변수여서 밖에 나오면 temp라는 변수가 사라지니까 
     */
// - 장점 2. 조금 더 빠름(속도적 우위) <- 정의 따로 호출 따로 하는 것보다 한 번에 하는 게 더 빠름
(()=>{
    const params = new URL(location.href).searchParams;
    // new URL(location.href).searchParams; == 쿼리스트링 값을 key, value 구분해서 저장하는 객체를 반환해줌

    const key = params.get("key"); // t, c, tc, w 중 하나 // 쿼리스트링 중에서 key라는 것 가져와
    const query = params.get("query"); // 검색어 // 쿼리스트링 중에서 query라는 것 가져와

    if(key != null){ // 검색을 했을 때 //쿼리스트링 중에 key라는 게 존재했을 때
        searchQuery.value = query; // 검색어를 화면에 출력
        //파라미터에서 얻어온 query값 넣어!
        
        // option태그를 하나씩 순차 접근해서 value가 key랑 같으면
        // selected 속성 추가
        for(let op of options){
            if(op.value == key){
                op.selected = true; //똑같은 옵션에 selected라는 속성을 추가하는 것
                //option 태그에 selected라는 속성 추가하면 선택이 돼있는 것처럼 된다
            }
        }
    }
}
)();






















