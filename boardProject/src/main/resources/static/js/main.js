/* 쿠키를 이용한 JS 코드  */

/* 쿠키에서 key가 일치하는 value 얻어오기 함수 */
//쿠키가 여러 개이면 'saveId=member01@kh.or.kr; saveId=member01@kh.or.kr; saveId=member01@kh.or.kr;' 형식의 문자열로 되어있다
//쿠키는 "K=V; K=V;" 형식 ->이걸 쪼개서 객체로 만들기
const getCookie = key=>{ 
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
























