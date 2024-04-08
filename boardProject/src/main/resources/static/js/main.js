/* 쿠키를 이용한 JS 코드  */

/* 쿠키에서 key가 일치하는 value 얻어오기 함수 */
//쿠키가 여러 개이면 'saveId=member01@kh.or.kr; saveId=member01@kh.or.kr; saveId=member01@kh.or.kr;' 형식의 문자열로 되어있다
//쿠키는 "K=V; K=V;" 형식 ->이걸 쪼개서 객체로 만들기
const getCookie = key=>{ //아이디 저장 checkbox -> 아이디 저장은 쿠키 이용!!
    //체크박스에 value가 없을 때
		// - 체크가 된 경우   -> 체크 되면 on 나옴(null아님)
		// - 체크가 안 된 경우 -> null이 나옴  (MemberController)
        //아이디 저장은 보통 로그인 성공 시 저장함! -> MemberController의 로그인 성공 시에 쿠키 저장 코드 있다
    /* 매개변수가 하나면 소괄호 생략 가능 */
    const cookies = document.cookie; //"K=V; K=V;" ==문자열

    //cookies 문자열을 배열 형태로 변환하기
    const cookieList = cookies.split("; ") // 쿠키가 여러 개 있으면 ;를 구분자로 해서 쪼개겠다
    // 배열 돼서 [ "K=V", "K=V"]로 된다
    //method chaining ->함수 이어쓰기
    //배열.map(함수) : 배열의 각 요소를 이용해 함수 수행 후 결과 값으로 새로운 배열을 만들어서 반환
                                // .map(el=>{return el.split("=")}) 
                                .map(el=>el.split("=")) //el에는 "K=V"가 들어있는데 또 쪼갠다
                                //[K,V]로 나눠져서 배열로 저장된다
                                //향상된 for문 ->요소 하나씩 들어감
                                //const arr = [1,2,3];
                                // arr.map(item =>{return item*2})
                                //[2,4,6]
                                //요소를 이용해서 새 배열 만듦
                                console.log(cookieList); //2차원 배열이 된다
    
    //배열 -> 객체(Object타입)로 변환(그래야 다루기 쉽다)
    const obj = {}; //비어있는 객체 선언
    for(let i=0;i<cookieList.length ; i++){
        const k = cookieList[i][0]; //key값
        const v = cookieList[i][1]; //value값
        obj[k] = v; //객체에 추가
    }
    // console.log("obj", obj); //객체가 되었다
    return obj[key]; //매개변수로 전달 받은 key와 
    //                  obj 객체에 전달된 key가 일치하는 요소의 value를 반환


}
console.log(1234); /* 이렇게 밖에 쓰면 페이지 로딩 되자마자 실행됨 */

const loginEmail = document.querySelector("#loginForm input[name='memberEmail']"); //로그인에 작성하는 이메일
//아이디가 loginForm의 후손 중에서 name속성값이 해당되는 input태그 찾아라!
if(loginEmail !=null){ 
    //만약 반대로 null인 경우로 하면 페이지 로딩됐을 때 수행돼서 오류남
    //로그인 창의 이메일 입력 부분이 존재할 때(로그인이 안 된 상태인 경우에만 수행) 코드 동작하겠다

    //쿠키 중 key값이 "saveId"인 요소의 value 얻어오기
    const saveId = getCookie("saveId"); //얻어온 value가 저장되는데, value는 undefined 또는 이메일이 있을거다

    //saveId 값이 있을 경우
    if(saveId !=undefined){
        //이메일이 존재하는 경우
        //아이디 저장 체크 후 로그인 한 후 다시 들어왔을 때 이메일 입력 창에 이전에 입력했던 이메일 있어야 한다
        loginEmail.value=saveId; //쿠키에서 얻어온 값을 input의 value로 세팅해놨다 ->화면에 이미 작성돼있을거다

        document.querySelector("input[name='saveId']").checked = true; //아이디 저장 체크박스에 체크 해두기

        //아이디 저장 미체크 후 로그인 후 다시 돌아오면 쿠키가 다 삭제되므로 아이디 부분에 이메일 작성되지 않아있다
    }
} 
// 목표 : 로그인 시 이메일 또는 비밀번호가 하나라도 작성되지 않은 경우, 로그인 시도조차 되지 않게 하겠다
//          로그인 시도를 못하게 하겠다 == 컨트롤러로의 form태그 제출을 못하게 하겠다
//                                      == e.preventDefault();
//form 태그 제출을 막는 방법
//click, keyup 등등 외에도
//브라우저 내에서 일어나는 모든 것이 이벤트!!!
//submit도 이벤트임!!
//  form 요소.addEventListener("submit", e=>{
//  e.preventDefault(); //form태그의 기본 이벤트(내부에 작성된 input태그 값을 제출하는 이벤트)
//}) 
/*이메일, 비밀번호 하나라도 미작성 시 로그인 막기*/
const loginForm = document.querySelector("#loginForm");
const loginPw
= document.querySelector("#loginForm input[name='memberPw']");
//form태그는 로그인 성공 시 사라지므로 form태그 있을 때에만 동작하도록 if문 쓰기
//#loginForm이 화면에 존재할 때 == 로그인 상태가 아닐 때
if(loginForm !=null){
    //있을 때에만
    loginForm.addEventListener("submit", e=>{
        //submit이라는 이벤트 감지하는 이벤트 리스너 추가
        //그 때 실행되는 함수 == 이벤트핸들러
        //제출 이벤트 발생 시
        //e.preventDefault(); //기본 이벤트(제출) 막음

        //loginEmail : 이메일 input요소
        //loginPw : 비밀번호 input요소

        //이메일 미작성 시
        if(loginEmail.value.trim().length ===0){
            alert("이메일을 작성해 주세요.");
            e.preventDefault();
            loginEmail.focus(); //작성하도록 초점 이동시켜놓기
            return; //이거 하나 작성 안됐으면 그 밑의 코드 실행할 필요 없음
        }

        //비밀번호 미작성 시
        if(loginPw.value.trim().length ===0){
            alert("비밀번호를 작성해 주세요.");
            e.preventDefault();
            loginPw.focus(); //작성하도록 초점 이동시켜놓기
            return; //이거 하나 작성 안됐으면 그 밑의 코드 실행할 필요 없음
        }

        //둘 다 정상적으로 작성한 경우
        

        //내가쓴코드
        // const memberEmail = document.querySelector("input[name='memberEmail']").value
        // const memberPw = document.querySelector("input[name='memberPw']").value
        // if(memberEmail.length == 0 || memberPw.length==0) e.preventDefault();
    });
}
/* 빠른 로그인 */
const quickLoginBtns = document.querySelectorAll(".quick-login"); // NodeList(배열, forEach를 제공)로 얻어와짐
quickLoginBtns.forEach((item, index)=>{ //향상된 for문
    //item : 현재 반복에서 꺼내온 객체가 들어있음
    //index : 현재 반복 중인 인덱스
    item.addEventListener("click", e=>{
        //배열에 이벤트 추가 못한다!
        //요소 하나하나에만 이벤트 추가할 수 있다
        const email = item.innerText; //버튼에 작성된 이메일 얻어오기
        location.href = "/member/quickLogin?memberEmail="+email; //get방식 요청 email값 쿼리스트링으로 전달
    });
});
//--------------------------------------------------------------------------------------------
/* 비동기로 회원 목록 조회하기 */
//조회 버튼
const selectMemberList = document.querySelector("#selectMemberList");

//tbody
const memberList = document.querySelector("#memberList");

//td태그 만드는 함수
const createTd = (text)=>{
    const td = document.createElement("td");
    td.innerText=text;
    return td;
    //이 구문이 반복될 것 같아서 함수로 만듦
    //td 요소를 만들고 text 추가 후 반환하는 함수
}

//조회 버튼 클릭 시
selectMemberList.addEventListener("click", ()=>{
    // 1) 비동기로 회원 목록 조회
    //      (포함될 회원 정보 : 회원 번호, 이메일, 닉네임, 탈퇴 여부)
    //      가져오면 [{}, {}, {}, ...] 형태인 JSON Array로 반환되는데 JS객체 배열로 변환된다
    //      fetch하면 첫 번째 then에서 response =>response.json()으로 변환
    //      그게 JS 객체로 변환해준다
    //  2) 두 번째 then
    //      tbody에 이미 작성되어 있던 내용(이전에 조회한 목록) 다 삭제하기
    //  3) 두 번째 then
    //      조회된 JS 객체 배열을 이용해 
    //      tbody에 들어갈 요소를 만들고 값 세팅 후 추가
    fetch("/member/selectMemberList") //조회니까 get방식
    //그러면 결과가 int나 String이면 response.text()로 하는데
    //지금은 비동기 요청 결과가 객체이므로 json으로 변환
    .then(resp => resp.json()) //리스트로 받아올 거니가 json으로 인식해서 js 객체로 파싱
    .then(list => {
        console.log(list);
        //부르면 실행되는 함수 == 콜백함수

        //이전 내용 삭제
        memberList.innerHTML="";

        //tbody에 들어갈 요소를 만들고 값 세팅 후 추가
        list.forEach( (member,index)=>{ //array, nodeList만 forEach 사용 가능
            //분홍 소괄호 내부의 함수 ==콜백함수
            //member : 반복 접근한 요소(순서대로 하나씩 꺼낸 요소)
            //index : 현재 접근 중인 index(사용 안할 거긴 한데)
            //tr만들어서 그 안에 td 만들어 append후 tr을 tbody에 append
            const keyList = ['memberNo', 'memberEmail', 'memberNickname', 'memberDelFl'];
            const tr = document.createElement("tr");

            tr.append(createTd(index) );

            keyList.forEach(key =>{ 
                //keyList에서 key값 하나씩 꺼내온 후 해당 key에 맞는 member객체 값을 얻어와
                //생성되는 td 요소에 innerText로 추가 후 tr요소의 자식으로 추가
                
                const td = createTd(member[key]); 
                //0번 인덱스의 memberNo나오고 그 다음 바퀴에서는 1번 인덱스의 memberNo나오고,...
                tr.append(td);

                // 여기까지만 줄이면 == keyList.forEach(key => tr.append(createTd(member[key])));
                
                //tbody 자식으로 tr 추가하기
                memberList.append(tr);
                
            });
            

            const btnTd = document.createElement("td");
            const btn = document.createElement("button");
            btn.innerText = "삭제";

            btn.addEventListener("click", () => {
                if(!confirm("정말 삭제하시겠습니까?")){
                return;
                }

                console.log(member.memberNo);
                 //확인 클릭 시
                fetch("/member/delete", {
                method : "DELETE",
                headers : {"Content-Type" : "application/json"},
                body : member.memberNo})
                .then(resp=>resp.text())
                .then(result => {
                    if(result>0) alert("삭제 성공!!!");
                    else{
                        
                        alert("삭제 실패...");
                        /* 만약에 alert하고 다른 페이지 가고싶으면 */
                        /* location.href 쓰면 된다(get방식) */
                        /* 테이블 UPLOAD_FILE에서 회원 번호 참조하고 있는데
                        부모 테이블의 데이터 삭제하고싶은 경우
                        기본값이 ON DELETE RESTRICTED여서
                        UPLOAD_FILE의 FK제약조건을 삭제하고 
                        ON DELETE SET NULL이런 것으로 설정하면 된다 */
                    }    
                });
            });

            btnTd.append(btn);
            tr.append(btnTd);
            
        })
        //mdn 사이트 : 여기서 여러가지 검색해서 공부 가능
    });
});
//----------------------------------------------------------------------------
//특정 회원 비밀번호 초기화하기
const resetPw = document.querySelector("#resetPw");
const resetMemberNo = document.querySelector("#resetMemberNo");
resetPw.addEventListener("click", ()=>{
    /*클릭되었을 때
    비밀번호 바꾸는 업데이트 -> PUT(많이 수정할 때 사용)이나 FETCH(일부 수정할 때 사용)
    putmapping으로 요청 보내보기 비동기로
    */


   //입력 받은 회원 번호 얻어오기
    const inputNo = resetMemberNo.value; 
    if(inputNo.trim().length==0){
        //입력 안하면 시행도 안되도록 막기
        alert("회원 번호를 입력해 주세요");
        return;
    }
    fetch("/resetPw", { //MainController 이용!
        method : "PUT", //수정 요청 방식
        headers : {"Content-Type" : "application/json"}, 
        //이게 무슨 형식이다 정의, 내용 많으면 다 써주면 된다
        body : inputNo //inputNo만 담아서 요청 보낼거다
    }) //수정 시 결과 int로 나옴
    .then(resp => resp.text()) //컨트롤러에서 resp로 응답이 오는데 text로 바꾼 것이 result에 담긴다
    //첫 번째 then의 resp.text() == 두 번째 then의 result 
    .then(result =>{
        //result == 컨트롤러로부터 반환받아 text로 파싱한 값(근데 String형태가 된다)
        //결과 result값에 따라서 코드 수행할거야
        if(result>0) alert("비밀번호 초기화 성공!!");
        else        alert("해당 번호의 회원이 존재하지 않습니다.");
        
    });
});
/* 특정 회원 탈퇴 복구(비동기, ajax) */


const outResetBtn = document.querySelector("#outResetBtn");
outResetBtn.addEventListener("click", ()=>{

    const outResetMemberNo = document.querySelector("#outResetMemberNo").value;

    if(outResetMemberNo.trim().length==0){
        alert("회원 번호를 입력해 주세요.");
        return;
    }
    fetch("/outReset", {
        method : "PUT",
        headers : {"Content-Type" : "application/json"},
        body : outResetMemberNo
    })
    .then(resp => resp.text())
    .then(result => {
        if(result==3){
            alert("등록된 회원 번호가 아닙니다.");
            return;
        }    
            
        if(result==1) {
            alert("탈퇴 복구 성공!!");
            return;
        }
        else       alert("탈퇴한 회원이 아니어서 탈퇴 복구를 할 수 없습니다. ");
    
    });
});
/* Filter : 걸러냄 + 추가함 
Filter : 서블릿에서 제공하던 기술이고, 클라이언트랑 dispatcher servlet 사이에 필터가 걸림
필터가 요청이 들어올 때에도 걸리고, 응답 나갈 때에도 필터가 걸림(요청 들어오자마자 or 응답 가기 직전에)
이 지점에 필터를 넣어서 요청 올/나갈 때 요청이 무슨 주소*파라미터를 가지고 있는지 확인 가능

필터가 dispatcher servlet이랑 controller 사이에 들어가면 필터가 아닌 interceptor(가로채기)라고 부름
요청/응답을 가로챔
컨트롤러 들어가고 나올 때 응답이 Spring Interceptor

Java를 인터넷에서 쓰고 싶다(web) == Servlet(Filter 제공)
                                +설정 자동화, 관리, 기능    == Spring(Spring에서도 Filter 제공!)
*/























