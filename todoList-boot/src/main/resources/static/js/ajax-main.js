
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

//할 일 목록 조회 관련 요소
const tbody=document.querySelector("#tbody");

//js로 화면 만들기 쉽게 하는 것 : 리액트, 뷰

//할 일 상세 조회 관련 요소
const popupLayer = document.querySelector("#popupLayer");
const popupTodoNo = document.querySelector("#popupTodoNo");
const popupTodoTitle = document.querySelector("#popupTodoTitle");
const popupComplete = document.querySelector("#popupComplete");
const popupRegDate = document.querySelector("#popupRegDate");
const popupTodoContent = document.querySelector("#popupTodoContent");

const popupClose = document.querySelector("#popupClose");

//삭제 버튼
const deleteBtn = document.querySelector("#deleteBtn");

//완료 여부 버튼
const changeComplete = document.querySelector("#changeComplete");
const updateView = document.querySelector("#updateView");

// 수정 레이어 버튼
const updateLayer = document.querySelector("#updateLayer");
const updateTitle = document.querySelector("#updateTitle");
const updateContent = document.querySelector("#updateContent");
const updateBtn = document.querySelector("#updateBtn");
const updateCancel = document.querySelector("#updateCancel");
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
    .then(response=> {return response.text()})
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
    /* js객체 양 끝에 쌍따옴표를 붙이면 문자열 모양으로 만들어지는데, 그 문자열이 js 작성법으로 k:v, k:v로 쓰여진 것 == json */
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
        //param을 JSON으로 만든다
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

            //할 일 목록 다시 비동기로 조회
            selectTodoList();
        } else{
            //실패한 경우
            alert("추가 실패...");
        }
    });
    //js에서 {K:V, K:V, ...} ==객체
});

//------------------------------------------------------------------------------------------------------------
// 클릭하면 비동기(ajax)로 할 일 상세 조회하는 함수
const selectTodo = (url)=>{
    //매개변수로 url을 전달받음

    //매개변수 url== "/ajax/detail?todoNo=10" 모양의 문자열

    //.then(response =>response.json()) : 응답 데이터가 JSON인 경우 이를 자동으로 Object 형태로 변환하는 메서드
    //                              ==JSON.parse(JSON데이터)
    fetch(url) //비동기로 요청 ->이 주소 처리할 컨트롤러 만들기

    .then(response =>response.json()) //JSON으로 바꿨다 ->이게 두 번째 then으로 넘어감
    .then(todo =>{
        //매개변수 todo : 서버 응답(JSON)이 Object로 변환된 값(첫 번째 then 반환 결과)->todo 객체에 넣었다
        console.log(todo);

        /* popup layer에 조회된 값을 출력 */
        popupTodoNo.innerText=todo.todoNo;
        popupTodoTitle.innerText=todo.todoTitle;
        popupComplete.innerText=todo.complete;
        popupRegDate.innerText=todo.regDate;
        popupTodoContent.innerText=todo.todoContent;

        //popup layer 보이게 하기
        popupLayer.classList.remove("popup-hidden");

        //update layer가 혹시라도 열려 있으면 숨기기
        updateLayer.classList.add("popup-hidden"); //팝업 숨겨!
        /* 요소.classList.toggle("클래스명")
            - 요소에 해당 클래스가 있으면 제거
                요소에 해당 클래스가 없으면 추가해라(스위치)

            요소.classList.add("클래스명")
            - 요소에 해당 클래스가 없으면 추가

            요소.classList.remove("클래스명")
            - 요소에 해당 클래스가 있으면 제거
        */
    });
};
//위에서 선언한 함수를 밑에서는 호출 가능 
//------------------------------------------------------------------------------------------------------------
//비동기(ajax)로 할 일 목록을 조회하는 함수
//function 으로 선언 요즘은 안하고 변수 식으로 다음과같이 작성(변수에 함수를 대입하는 형식)
const selectTodoList = ()=>{//js는 변수에 함수도 저장할 수 있다
    //ajax코드 쓰기
    fetch("/ajax/selectList") //get방식 요청을 할거면 소괄호 안에 "요청주소"를 적는다
    //비동기 요청해서 이 위치로 가져온다 -> Promise(결과 여기로 가지고 올거라고 약속 하는 객체) 객체가 반환된다

    //응답받은 Promise 객체 뜯어내는 첫 번째 then문
    //응답 결과를 텍스트 형태로 파싱(변환)!
    .then(response =>response.text())

    .then(result =>{
        console.log(result); //result == 첫 번째 then에서 반환된 결과값==조회 결과가 담긴 String!!!
        console.log(typeof result); //result의 타입이 뭘까? String(문자열이다!)
        //JSON은 문자열임!!(JS 객체 모양일 뿐)
        //js object(객체 타입)와 js objectNotation(String)은 완전히 다르다!
        //아! JSON은 객체가 아니라 문자열 이구나!!!!!!!! ==>지금은 0번인덱스,1번인덱스로 뜯어서 쓸 수 없음

        //문자열은 어떻게든 가공을 해낼 수는 있는데 힘들다

        //근데 js에서 이걸 편하게 가공할 수 있는 함수를 제공
        //  ->JSON.parse(JSON데이터) 이용
        //      - String형태의 JSON데이터를 JS Object타입으로 변환해줌!
        //  <위아래의 둘은 서로 역방향!!!>
        //어제 했던
        //JSON.stringify(JS Object)
        //  - JS Object타입을 String형태의 JSON 데이터로 변환해줌

        const todoList = JSON.parse(result);
        console.log(todoList); //배열 안에 객체가 들어있는 객체배열 형태가 되었다!! 
        //JSON문자열을 자바와 js가 주고받음
        //자바에서 리스트를 반환한 것 같지만 HttpMessageConverter가 JSON으로 바꿔서 js에 보내줌 ->js에서 함수 이용해서 객체배열로 만듦

        //-------------------------------------
        /* 기존에 출력되어있던 할 일 목록을 모두 삭제 */
        tbody.innerHTML="";  /* 빈칸 대입 */
        /* 요소의 inner == 시작태그와 종료태그의 사이 */
        //-------------------------------------
        //받은 데이터 이용해서 #tbody에 tr/td 요소를 생성해서 내용 추가하는 코드
        for(let todo of todoList){
            //향상된 for문
            //하나씩 꺼내서 반복

            //비동기로 데이터 요청해서 가져왔으면 js로는 동적으로 화면 꾸미기
            //tr태그 생성
            const tr = document.createElement("tr"); //tr요소 생성
            //tr안에 네개의 td가 필요

            //이렇게 해도 되지만 중복이 너무 많아 !
            // const td1 = document.createElement("td");
            // td1.innerText=todo.todoNo; //번호
            // //하나씩 꺼낸 todo에서 todoNo를 꺼내와서 세팅하기 

            // const td2 = document.createElement("td");
            // td2.innerText=todo.todoTitle;//제목

            // const td3 = document.createElement("td");
            // td3.innerText=todo.complete;//완료 여부

            // const td4 = document.createElement("td");
            // td4.innerText=todo.regDate;//등록일

            // tr.append(td1,td2,td3,td4); //tr의 자식으로 추가하기

            // //tbody의 자식으로 tr 한 줄을 추가하기
            // tbody.append(tr);
            //--------------------------------------------------------------------------
            //리팩토링 == 효율 높이려고 코드 수정
            const arr = ['todoNo', 'todoTitle', 'complete', 'regDate'];
            for(let key of arr){
                const td = document.createElement("td");
                if(key==='todoTitle'){
                    //만약 key값이 제목인 경우
                    //그냥 값 만드는 게 아니라 a태그 만들기
                    const a = document.createElement("a");
                    a.innerText=todo[key]; //a태그를 생성해서 a태그의 내용으로 todo의 key값을 대입한다
                    //a.href="주소"하면 속성 추가된다
                    a.href="/ajax/detail?todoNo="+todo.todoNo; //쿼리스트링으로 todoNo전달
                    //주소에는 띄어쓰기 없어야함!

                    


                    //a태그를 td에 넣기
                    td.append(a);
                    tr.append(td);

                    //a태그의 클릭 막기
                    a.addEventListener("click", e=>{
                        e.preventDefault();

                        //이동은 안 할 건데 할 일 상세 조회하는 것을 비동기로 요청할거다
                        //주소를 가져와서
                        //위에 만듦
                        //아래에 변수로 선언한 함수를 위에서 호출하는 것 불가!!!
                        //할 일 상세 조회 비동기 요청

                        //e.target.href : 클릭된 a태그의 href 속성 값
                        selectTodo(e.target.href); //selectTodo를 호출하는데 매개변수 보내줌 ->비동기 요청 보내서 콘솔에 찍음
                    });
                    //a태그 클릭 시 a태그 자체가 가지고 있던 기본 이벤트(클릭 시 주소의 페이지로 이동)
                    continue; //밑으로 안 내력고 위로 가서 다음 반복 수행
                    //Title인 경우에만 if문 실행되고 아니면 밑의 코드가 실행됨
                }
                td.innerText=todo[key]; 
                //js 객체 필드값 꺼내는 방법 두가지 : 1).찍어서 하는 방법 2) 배열처럼 []하는 방법
                tr.append(td);
            }
            //tbody의 자식으로 tr한 줄을 추가하기
            tbody.append(tr);
        }
        
    })


}; 

//------------------------------------------------------------------------------------------------------------
//popup layer 의 X버튼 클릭 시 닫기
popupClose.addEventListener("click", ()=>{
    //다시 안보이게 하는 클래스를 추가하기
    popupLayer.classList.add("popup-hidden");
});
//------------------------------------------------------------------------------------------------------------
//삭제버튼이 클릭되었을 때 ->정말 삭제할 지 한 번 물어보기
deleteBtn.addEventListener("click", ()=>{
    if(!confirm("정말 삭제 하시겠습니까?")){ //취소 클릭 시 ->아무것도 안하고 끝내기
        //confirm()
        //확인버튼, 취소버튼이 나오는데
        //확인버튼 누르면 true
        //취소버튼 누르면 false
        return; //아래쪽이 더이상 해석 안되게 함수 종료하기
    }
    //그게 아니면 비동기 요청을 할건데 삭제할 할 일 번호(PK) 얻어오기
    const todoNo = popupTodoNo.innerText; //#popupTodoNo 요소 태그의 태그 사이 내용을 얻어온다

    //비동기 DELETE 방식 요청
    fetch("/ajax/delete", {
        //객체 만들겠다
        method : "DELETE", //DELETE 방식 요청(이건 비동기 요청시에만 사용가능!) -> @DeleteMapping으로 처리하면 된다!
        headers : {"Content-Type" : "application/json"}, /* 전달할 데이터가 텍스트야->text말고 json으로 하기 */
        //데이터 하나를 전달해도 application/json으로 작성하기
        body : todoNo /* 바디에 todoNo 값만 담아서 전달 -> 이 값은 @RequestBody로 꺼내면 된다 */
        /* //@RequestBody는 post방식으로 body에 담겨있을 때만 사용+delete방식도 이거 사용 */
    })
    .then(response =>response.text())
    /* 삭제 결과에 따라 0,1 둘중 하나 나오니까 요청 결과를, 객체가 아닌,텍스트 형태로 파싱 */
    /* 텍스트 : 문자열,숫자 */
    .then(result=>{
        if(result>0){
            //삭제 성공시
            alert("삭제 되었습니다");

            //상세 조회 창 닫기
            popupLayer.classList.add("popup-hidden");

            //전체 Todo개수, 완료된 Todo 개수, 할 일 목록 조회 함수 다시 호출하기
            getTotalCount();
            getCompleteCount();
            selectTodoList();
        } else{
            alert("삭제 실패...");
        }
    });
});
//------------------------------------------------------------------------------------------------------------
//완료 여부 변경 버튼 클릭 시
//UPDATE할 때에는 PUT방식으로 요청하기(수정)->@PutMapping
changeComplete.addEventListener("click", (e)=>{
    //업데이트 하려면 complete와 todoNo필요
    //변경할 할 일 번호
    const todoNo = popupTodoNo.innerText; //글자 가져오면 번호임

    //완료 여부를 바꿔서 가져오기
    const complete = popupComplete.innerText ==='Y' ?'N':'Y'; //Y이면 N으로 바꿔서 저장하고 아니면 Y로 저장

    //SQL 수행에 필요한 값을 객체로 묶음->한번에 보내게!
    const obj = {"todoNo" : todoNo, "complete" : complete};

    //값 두개 가져왔으니 비동기 통신하기
    //비동기로 완료 여부 변경하기
    fetch("/ajax/changeComplete",{ //delete,post랑 똑같이 쓰면 된다
        method : "PUT", //PUT 방식 요청 -> @PutMapping으로 처리하면 된다!
        headers : {"Content-Type" : "application/json"}, /* 전달할 데이터가 텍스트야->text말고 json으로 하기 */
        //데이터 하나를 전달해도 application/json으로 작성하기
        body : JSON.stringify(obj) /* 값을 두개 보내야 해서 객체로 묶어서 보내기*/
        //obj는 객체인데, headers에서 JSON(String)을 보낸다 했으니까 문자열화하기
    })
    .then(response=>response.text())
    .then(result=>{ //텍스트화된 result를 이용해서 코드 작성
        if(result>0){
            //변경 성공 시
            //업데이트 된 DB데이터를 다시 조회해서 화면에 출력하기
            //selectTodo(); //바뀐 값으로 다시 조회하기
            //->서버 부하가 큼->서버 부하를 줄이기 위해 상세 조회에서 Y/N만 바꾸기

            popupComplete.innerText=complete; //반대로 해서 저장한 값으로 바꾸겠다
            // 서버 부하를 줄이기 위해 완료된 Todo 개수 +-1 하기

            const count = Number(completeCount.innerText)//화면에 써져 있던 값을 얻어와서 String을 넘버로 바꿈
            //innerText : String 타입이다
            if(complete ==='Y'){
                //complete : 반대로 바뀐 값
                completeCount.innerText = count+1;
                
            }else completeCount.innerText = count-1;
            //getCompleteCount();
            selectTodoList(); //바뀐 리스트 다시 조회하기
            //이것도 서버 부하 줄이기 가능 ->코드 약간 복잡
            //9번 할 일의 완료 여부 수정 시 9번의 전체 목록에서도 바뀐 값으로 바꾸면 된다
        }else{
            //변경 실패 시
            alert("완료 여부 변경 실패...")
        }
    });

});
//------------------------------------------------------------------------------------------------------------
//상세 조회에서 수정 버튼 클릭 시
updateView.addEventListener("click", ()=>{
    //기존 팝업 레이어는 숨기고
    popupLayer.classList.add("popup-hidden"); //이 클래스가 추가돼야 숨겨진다
    // 수정 레이어 보이게
    updateLayer.classList.remove("popup-hidden");

    //수정 레이어 보일 때 팝업 레이어에 작성된 제목, 내용 얻어와 세팅
    updateTitle.value=popupTodoTitle.innerText; //span태그의 내용을 얻어와서 input태그 요소에 집어넣겠다
    updateContent.value=popupTodoContent.innerHTML.replaceAll("<br>", "\n"); //HTML 화면에서 줄바꿈이 <br>로 인식됐는데 
    //textarea에서는 \n으로 바꿔야 줄바꿈으로 인식된다 ->br태그를 \n으로 교체하는 replace구문 추가 

    //수정 레이어 -> 수정 버튼에 data-todo-no 속성 추가 ->dataset을 이용해서 저 속성값을 이용할 수 있게 된다
    updateBtn.setAttribute("data-todo-no", popupTodoNo.innerText);
});
//------------------------------------------------------------------------------------------------------------
//수정 레이어에서 취소버튼이 클릭되었을 때
updateCancel.addEventListener("click", ()=>{
    //수정 레이어 숨기기
    updateLayer.classList.add("popup-hidden");

    //팝업레이어 보이기
    popupLayer.classList.remove("popup-hidden");
});
//------------------------------------------------------------------------------------------------------------
/*수정 레이어 -> 수정 버튼 클릭 시*/
updateBtn.addEventListener("click",e=>{
    //e : 이벤트 객체
    //서버로 전달해야되는 값을 객체로 묶어두기
    const obj = {
        "todoNo" : e.target.dataset.todoNo,
        "todoTitle" : updateTitle.value, //updateTitle에 작성된 값
        "todoContent" : updateContent.value 
        //input태그들은 value를 쓴다
        //innerText는 종료태그도 있을 때 사용
    };
    //console.log(obj);
    //fetch로 비동기요청하기
    fetch("/ajax/update", {
        method : "PUT", /* 수정은 PUT, restAPI */
        headers : {"Content-Type" : "application/json"},
        body : JSON.stringify(obj)
    })
    .then(response =>response.text())
    .then(result =>{
        //결과 가지고 코드 쓰기
        if(result>0){
            //성공
            alert("수정 성공!!!");

            //수정 화면 숨기기
            updateLayer.classList.add("popup-hidden");
            

            //selectTodo(); //다시 상세 조회
            //->성능 개선
            //기존 내용을 바꾼 내용으로 업데이트하기
            popupTodoTitle.innerText=updateTitle.value; //쓴 값을 넣겠다
            popupTodoContent.innerHTML=updateContent.value.replaceAll("\n", "<br>")
            //updateContent는 textarea여서 엔터를 \n으로 나타냄
            //html에서는 br태그로 ->innerText가 아닌 innerHTML로
            popupLayer.classList.remove("popup-hidden");

            selectTodoList(); //목록 다시 조회
            //내용이 바뀌었으니까 바뀐 내용으로 다시 만들기

            updateTitle.value="";
            updateContent.value=""; 
            updateBtn.removeAttribute("data-todo-no"); //속성을 제거
            //흔적 남지 않도록 남은 흔적 제거
        }else alert("수정 실패...");
    });
});
//------------------------------------------------------------------------------------------------------------
//js파일에 함수 호출 코드를 작성하면 -> 페이지 로딩 시 바로 실행된다 ->페이지 로딩 시에 두 함수 호출해서 DB에서 조회해서 화면에 뿌려진다
//로딩과 동시에 될 것들은 맨 위나 맨 아래에 작성해야 됨!
getTotalCount();
getCompleteCount();
selectTodoList(); //목록까지 바로 나와있다