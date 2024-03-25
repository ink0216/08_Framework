
/* ajax : js기술 */
/*요소 얻어와서 변수에 저장하기*/ 
const totalCount = document.querySelector("#totalCount");
const completeCount = document.querySelector("#completeCount");

//새로고침 버튼
const reloadBtn = document.querySelector("#reloadBtn");

//할 일 추가 관련 요소
const todoTitle = document.querySelector("#todoTitle");
const todoContent = document.querySelector("#todoContent");
const addBtn = document.querySelector("#addBtn");
//------------------------------------------------------------------------------------------------------------

//전체 Todo 개수 조회 및 출력하는 함수 만들기
function getTotalCount(){ //함수 정의

    //비동기로 서버(DB)에서 전체 Todo 개수를 조회하는
    //fetch() API 코드를 작성해보기
    //(fetch : 가지고 오다)
    //비동기로 서버에서 전체 Todo 개수를 가져옴

    //get방식 요청은 fetch 하고 요청주소 쓰기 ->파라미터 전달하고 싶으면 쿼리스트링 쓰기
    fetch("/ajax/totalCount") //getTotalCount를 호출하면 해당 주소로 요청을 보내겠다
    //비동기 요청 수행 하고오면 -> Promise 객체가 반환된다
    .then(response =>{
        // response : 비동기 요청에 대한 응답이 담긴 객체
        // response.text() : 응답 데이터를 문자열/숫자 형태로 변환한 결과를 가지는 Promise 객체가 반환됨
        console.log(response); //12가 담긴 곳이
        //console.log(response.text()); //Promise 객체가 나오는데 그 안에 12가 들어있음
        return response.text(); //바깥 포장 뜯기 ->이거 뜯으면 또다시 Promise 객체가 나와서 두번째 then에서 뽑아주기
    })

    //두 번째 then의 매개변수(result) 
    // == 첫 번째 then에서 반환된 Promise 객체의 promiseResult 값
    .then(result =>{
        //매개변수 result에는
        //첫 번째 then에서 promise 객체가 반환되는데 딱 promiseResult만 뽑아서 넣음
        console.log("result", result);
        //result 매개변수 == Controller 메서드에서 반환된 값
        //비동기로 요청 보내면 결과가 포장 여러 겹 쌓여서 옴
        //그 안에 또 있던 포장 뜯기

        totalCount.innerText = result; 
    //=>id가 totalCount인 요소의 내용을 result값 으로 변경
    //status: 200 == 잘 됐다는 html 표시
    })
    /* getTotalCount를 호출하면 비동기로 요청을 보냈다 거기서 response가 돌아온다 
    응답데이터를 텍스트로 바꿔 */

    //모든 then 구문에서 매개변수의 이름은 중요하지 않다!!!(then문의 순서만 중요)
}
//getTotalCount(); //함수 호출

//completeCount 값을 비동기 통신으로 얻어와서 화면 출력하기
function getCompleteCount(){
    //이 함수가 호출되면 fetch API 호출
    //fetch() : 비동기로 요청(->화면 깜박이지 않고, 특정 부분만 변함)해서 결과 데이터 가져오기

    //첫 번째 then의 response : 응답 결과, 요청 주소, 응답 데이터 등이 담겨있다
    fetch("/ajax/completeCount") //여기로 돌아오는데 promise 객체로 돌아와서 벗겨줘야함
    .then(response=> response.text())
    //.then(response=>{return response.text()}) 화살표 함수 안에 return구문밖에 없으면 return과 중괄호 생략할 수 있다

    //두 번째 then의 result
    // - 첫 번째 then에서 text로 변환된 응답 데이터(completeCount의 값)가 result에 담겨있다
    .then(result =>{
        // #completeCount 요소의 내용으로 result값을 출력하겠다(->화면에 출력됨)
        completeCount.innerText=result;
    })
}

//새로고침 버튼이 클릭되었을 때
reloadBtn.addEventListener("click", ()=>{
    getTotalCount(); //비동기로 전체 할 일 개수 조회 ->그걸로 화면 다시 만드는 것
    getCompleteCount(); //비동기로 완료된 할 일 개수 조회 ->그걸로 화면 다시 만드는 것
    //비동기로 데이터 가져와서 화면에 뿌리기
})

//할 일 추가 버튼 클릭 시
addBtn.addEventListener("click", ()=>{
    //비동기로 할 일 추가하는 fetch() API 코드 작성하기
    // - 요청 주소 : "/ajax/add"
    // - 데이터 전달 방식(method) : "POST"방식
    //fetch API로 저 주소로 요청보낼건데 POST방식으로 보내겠다 해보기

    //파라미터를 저장한 js 객체(key:value 형태로 저장)
    const param = {
        //      Key : Value
        "todoTitle" : todoTitle.value,
        "todoContent" : todoContent.value}
    /* js객체 양 끝에 쌍따옴표를 붙이면 문자열 모양으로 만드는데, 그 문자열이 js 작성법으로 k:v, k:v로 쓰여진 것 == json */
    //todoTitle : 제목 쓴 input태그
    fetch("/ajax/add", {
        //key   :  value
        method : "POST", //POST방식 요청이다
        headers : {"Content-Type" : "application/json"},//headers : 요청 헤더에 들어갈 내용들
        //내용의 형태(비동기로 보내는 파라미터의 모양을 지정)
        //요청 데이터의 형식을 JSON으로 지정한다
        //우리가 데이터 보내는데 이 데이터는 어떤 거야 라는 것을 머리에 써놓음
        //쌍따옴표 => 문자열
        //application/json == 응용 프로그램에서 쓸 수 있는, 데이터 타입이 json이야

        //택배상자 머리에는 json 보낸다고 써놓음
        body    : JSON.stringify(param)//json 보낸다고 했으니까 body에 json 담기
        //JSON : js에 내장된 객체
        //param객체를 JSON으로 문자열화 시키다 : stringify
        //param 객체를 JSON(자료형 ==String)으로 변환한다
    })
    .then(resp => resp.text()) //반환된 값을 text로 변환하기 //변수의 이름은 아무거나로 해도 된다! 

    //첫 번째 then에서 반환된 값 중 변환된 text를 temp에 저장
    .then(temp =>{
        if(temp>0){
            //성공한 경우
            alert("추가 성공!!!");

            //추가 성공한 제목,내용 지우기
            todoTitle.value=""; //값으로 빈칸 넣겠다 다 지워버리겠다
            todoContent.value=""; //값으로 빈칸 넣겠다 다 지워버리겠다

            //할 일이 추가되었기 때문에 전체 Todo 개수 다시 조회하기
            //삽입하자마자 완료되지 않으므로 전체 개수만 조회하면 된다
            getTotalCount();
        } else{
            //실패한 경우
            alert("추가 실패...");
        }
    });
    //js에서 {K:V, K:V, ...} ==객체
});

//js파일에 함수 호출 코드를 작성하면 -> 페이지 로딩 시 바로 실행된다 ->페이지 로딩 시에 두 함수 호출해서 DB에서 조회해서 화면에 뿌려진다
//로딩과 동시에 될 것들은 맨 위나 맨 아래에 작성해야 됨!
getTotalCount();
getCompleteCount();