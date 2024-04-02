/* 다음 주소 API 활용 */
function execDaumPostcode() {
    new daum.Postcode({ //다음 객체를 하나 만들어서 Postcode 메서드 호출
        oncomplete: function(data) { //매개변수로 JS 객체를 전달
            //완료했을 때 라는 콜백함수

            // 팝업에서 검색결과 항목을 클릭했을때 실행할 코드를 작성하는 부분.

            // 각 주소의 노출 규칙에 따라 주소를 조합한다.
            // 내려오는 변수가 값이 없는 경우엔 공백('')값을 가지므로, 이를 참고하여 분기 한다.
            var addr = ''; // 주소 변수

            //사용자가 선택한 주소 타입에 따라 해당 주소 값을 가져온다.
            if (data.userSelectedType === 'R') { // 사용자가 도로명 주소를 선택했을 경우
                addr = data.roadAddress;
            } else { // 사용자가 지번 주소를 선택했을 경우(J)
                addr = data.jibunAddress;
            }

            

            // 우편번호와 주소 정보를 해당 필드에 넣는다.
            document.getElementById('postcode').value = data.zonecode;
            // 아이디가 인 애를 찾아서 
            //data == 매개변수로 전달받은 것 -> 카카오에서 보내주는 데이터
            //호출 완료 시 카카오에서 data 보내줌

            document.getElementById("address").value = addr;
            // 커서를 상세주소 필드로 이동한다.
            document.getElementById("detailAddress").focus(); //입력해야하니 포커스 옮기기
        }
    }).open();

}
/* 주소 검색 버튼 클릭 시 */
document.querySelector("#searchAddress").addEventListener("click", execDaumPostcode
/* js에서는 매개변수로 함수가 왔다갔다 할 수 있다
함수 호출 -> 함수명() 작성
함수 코드를 그대로 가져오고싶다 -> 함수명 만 작성 */
//클릭 시 execDaumPostcode함수가 실행된다(execDaumPostcode 함수 내부의 코드가 저 자리로 들어온다)
);

//------------------------------------------------------------------------------------------------------
/*  *************************************** 회원가입 유효성 검사(Validation) ******************************************************** */

//객체 생성
const checkObj = {
    "memberEmail" : false,
    "memberPw"    : false,
    "memberPwConfirm" : false,
    "memberNickname" : false,
    "memberTel" : false,
    "authKey" : false //인증 다 했을 경우에 true로 바꿀거다
    //필수 입력 항목의 유효성 검사 여부를 체크하기 위한 객체(체크리스트 같은 객체)
    //입력해야 하는 input태그들에서 필수로 입력해야 하는 5개에 대한 id들이다
    // - true == 해당 항목은 유효한 형식으로 작성됐다는 뜻
    // - false == 해당 항목은 유효하지 않은 형식으로 작성됐다는 뜻 ->예를 들어서 이메일 형식에 안맞게 작성하면 false
    //모든 필수항목을 다 제대로 작성했으면 모두 true가 됨 -> 모두 true인 경우에만 회원가입이 시도되도록
    //하나라도 false이면 회원가입 시도조차 안되도록 하기
};
//---------------------------------
/* 이메일 유효성 검사 */
// 1) 이메일 유효성 검사에 사용될 요소 얻어오기
const memberEmail = document.querySelector("#memberEmail");
//메시지 나오는 span태그
const emailMessage = document.querySelector("#emailMessage");

// 2) 이메일이 입력(마우스로 입력, 복붙, keyup keydown, keypress, copy paste 등 통틀면 input이벤트!!)
//될 때 마다 유효성 검사를 수행할 거다!
memberEmail.addEventListener("input", e=>{
    //이메일 인증번호 인증 후에 이메일이 변경되는 경우
    checkObj.authKey = false; //readOnly 상태로 바꿔도 된다!!(인증 후에는 수정 못하도록 막는 것)
    document.querySelector("#authKeyMessage").innerText="";


    const inputEmail = e.target.value; //작성된 이메일 값 얻어오기(이벤트가 발생한 요소의 값)


    // 3) 지우는 버튼(backspace)도 입력으로 인식돼서
    //      입력된 이메일이 없을 경우
    if(inputEmail.trim().length===0){
        //좌우 띄어쓰기도 제거했을 때 길이가 0일 때
        emailMessage.innerText = "메일을 받을 수 있는 이메일을 입력해주세요."; //입력된 이메일이 없으면 이 말을 띄우겠다
        //textContent : 내용에 띄어쓰기 있으면 다 넣음

        //메시지 색 바뀌는 것은 클래스를 넣었다 뻈다 하면서 색을 줬다 말다 함!
        //메시지에 색상을 추가하는 클래스 모두 제거
        emailMessage.classList.remove('confirm', 'error'); //둘 다 지우면 검정색으로 나옴!
        //classList로 클래스 추가/제거할 때 위와 같이 2개 이상의 클래스를 한 번에 할 수 있다

        //이메일 유효성 검사 여부를 false로 변경
        checkObj.memberEmail = false;

        //처음의 띄어쓰기 안되도록 하기
        //잘못 입력한 띄어쓰기가 있을 경우 없애기
        //띄어쓰기 해도 빈칸 입력돼서 띄어쓰기 안되게 된다
        memberEmail.value=""; //빈칸을 대입
        return; //여기서 검사 끝내기
        
    }
    //입력이 됐을 경우
    // 4) 입력된 이메일이 있을 경우 정규식(정규 표현식, 알맞은 형태로 작성했는지 검사) 검사
    //regexr.com : 정규표현식 검사하는 사이트
    const regExp = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

    //입력 받은 이메일이 정규식과 일치하지 않는 경우(알맞은 이메일 형태가 아닌 경우)
    if( !regExp.test(inputEmail)){
        //입력받은 것이 형식에 맞는 지 검사 ->맞으면 true나옴
        emailMessage.innerText = "알맞은 이메일 형식으로 작성해 주세요.";

        //글자 빨갛게 만들기
        emailMessage.classList.add('error');  //빨간색으로 변경
        emailMessage.classList.remove('confirm'); //초록색 제거 

        checkObj.memberEmail = false; //유효하지 않은 이메일임을 기록하기
        return; //더이상 다른 것 검사할 필요 없다
    }
    //유효한 경우
    //비동기 요청(ajax - fetch API 이용)해서 이메일 중복 검사하기
    // 5) 유효한 이메일 형식인 경우 DB에 저장돼있는 이메일과 중복 검사 수행
    fetch("/member/checkEmail?memberEmail="+inputEmail) //get방식으로 요청 ->파라미터를 주소에 담아서 보냄(쿼리스트링으로)
    //컨트롤러 만들기
    .then(response =>response.text())
    .then(count => {
        //count : 1이면 중복, 0이면 중복 아님
        if(count == 1){
            //중복인 경우
            emailMessage.innerText="이미 사용중인 이메일 입니다.";
            emailMessage.classList.add('error');
            emailMessage.classList.remove('confirm');
            checkObj.memberEmail = false; //중복은 유효하지 않은 것!
            return;
        }
        //중복이 아닌 경우
        emailMessage.innerText="사용 가능한 이메일 입니다.";
        emailMessage.classList.add('confirm');
        emailMessage.classList.remove('error');
        checkObj.memberEmail = true; //유효한 것!
    })
    .catch(e=>{
    //fetch 비동기 통신하다가 예외 발생하면 잡는것!
    console.log(e); //발생한 예외 객체(e)
    })
    
});
//---------------------------------
/* 비밀번호 / 비밀번호 확인 유효성 검사 */
//영어,숫자,특수문자(!,@,#,-,_) 6~20글자 사이로 입력해주세요.
// 1) 비밀번호 관련 요소 얻어오기
const memberPw = document.querySelector("#memberPw");
const memberPwConfirm = document.querySelector("#memberPwConfirm");
const pwMessage = document.querySelector("#pwMessage");

// 5) 비밀번호, 비밀번호 확인이 같은지 검사하는 함수 만들기
const checkPw = ()=>{
    //요즘 이런식으로 코드 많이 쓴다!
    if(memberPw.value == memberPwConfirm.value){
        //같을 경우
        pwMessage.innerText="비밀번호가 일치합니다.";
        pwMessage.classList.add('confirm');
        pwMessage.classList.remove('error');
        checkObj.memberPwConfirm = true; //비밀번호 확인 true
        return;
    }
    //다른 경우
    pwMessage.innerText="비밀번호가 일치하지 않습니다.";
    pwMessage.classList.add('error');
    pwMessage.classList.remove('confirm');
    checkObj.memberPwConfirm = false;
};



// 2) 비밀번호 유효성 검사
memberPw.addEventListener("input", e=>{
    const inputPw=e.target.value //입력 받은 비밀번호
    //e.target.value == memberPw.value

    
    if(inputPw.trim().length===0){
        // 3) 아무것도 입력되지 않은 경우
        pwMessage.innerText="영어,숫자,특수문자(!,@,#,-,_) 6~20글자 사이로 입력해주세요.";
        pwMessage.classList.remove('error', 'confirm'); //처음에는 검정색으로 보여야 한다
        checkObj.memberPw=false; //유효하지 않다고 해놔야된다
        memberPw.value=""; //맨 앞의 띄어쓰기 입력 못하게 만들기
        return; //여기서 메서드를 끝내고 호출한 곳으로 돌아가는데 호출한 곳이 없으면 그냥 끝난다!
    }

    //뭔가 입력을 한 경우
    // 4) 입력 받은 비밀번호 정규식 검사
    //영어,숫자,특수문자(!,@,#,-,_) 6~20글자 사이로 입력해주세요.
    const regExp = /^[a-zA-Z0-9!@#_-]{6,20}$/;
    if(!regExp.test(inputPw)){
        //유효하지 않으면
        pwMessage.innerText="비밀번호가 유효한 형식이 아닙니다.";
        pwMessage.classList.add('error'); 
        pwMessage.classList.remove('confirm');
        checkObj.memberPw=false;
        return; 
    }
    //유효한 비밀번호인 경우
    pwMessage.innerText="사용 가능한 비밀번호 입니다.";
    pwMessage.classList.add('confirm'); 
    pwMessage.classList.remove('error');
    checkObj.memberPw=true;

    if(memberPwConfirm.value.length!==0){
        //비밀번호 확인에 무언가 값이 작성돼있을 때
        // 7) 비밀번호 입력 시에도 비밀번호 확인이랑 비교하는 코드 추가
        checkPw(); //아래쪽에 값 써져 있을 때에만 비교하고 비밀번호 확인에 아무것도 안쓰면 같은 지 검사하지마
    }
    
});
// 6) 비밀번호 확인에 대한 유효성 검사
//  단, 비밀번호 (memberPw)가 유효할 때에만 검사 수행할 것이다! ->비밀번호 확인은 비밀번호랑 같은 지만 확인하면 된다
memberPwConfirm.addEventListener("input", ()=>{
    //익명함수
    if(checkObj.memberPw){
        //checkObj.memberPw가 true인 경우(유효한 경우)
        checkPw(); //비교 함수 호출해서 수행
        return;
    }
    //memberPw가 유효하지 않은 경우
    //memberPwConfirm 검사 수행 X
    checkObj.memberPwConfirm=false; //얘도 유효하지 않다

    //비밀번호 확인 입력할 떄에만 동일 검사 하면 안되고 비밀번호 입력 할 때에도 동일 검사 해야한다
});
//---------------------------------
/* 닉네임 유효성(중복 검사도 해야함!) 검사 */
//입력 안됨 // 정규식 검사 // 중복 검사
const memberNickname = document.querySelector("#memberNickname");
const nickMessage = document.querySelector("#nickMessage");
memberNickname.addEventListener("input", e=>{
    const inputNickname = e.target.value;
    

    if(inputNickname.trim().length===0){
        //입력 안 한 경우
        memberNickname.value="";
        nickMessage.innerText="한글,영어,숫자로만 2~10글자";
        nickMessage.classList.remove('error','confirm');
        checkObj.memberNickname=false;
        return;
    }
    //뭔가 입력 했음 -> 정규식 검사
    // const regExp=/^[가-힣a-zA-Z0-9]{2,10}$/; ==이걸로 해도 된다
    const regExp=/^[가-힣\w\d]{2,10}$/;
    if(!regExp.test(inputNickname)){
        //유효하지 않은 경우
        nickMessage.innerText="닉네임이 유효한 형식이 아닙니다."
        nickMessage.classList.add('error');
        nickMessage.classList.remove('confirm');
        checkObj.memberNickname=false;
        return;
    }
    //유효한 경우
    //중복검사하기
    fetch("/member/checkNickname?memberNickname="+inputNickname)
    .then(response => response.text())
    .then(count=>{
        if(count >0){
            //중복되는 게 있다
            nickMessage.innerText="이미 존재하는 닉네임 입니다."
            nickMessage.classList.add('error');
            nickMessage.classList.remove('confirm');
            checkObj.memberNickname=false;
            return;
        }
            nickMessage.innerText="사용 가능한 닉네임 입니다.";
            nickMessage.classList.add('confirm');
            nickMessage.classList.remove('error');
            checkObj.memberNickname=true;
    })
    .catch(e=>console.log(e));
});
//---------------------------------
/* 전화번호 유효성(중복 검사도 해야함!) 검사 */

const memberTel = document.querySelector("#memberTel");
const telMessage = document.querySelector("#telMessage");
memberTel.addEventListener("input", e=>{
    const inputTel = e.target.value;
    if(inputTel.trim().length===0){
        //아무것도 작성 안한 경우
        telMessage.innerText="전화번호를 입력해주세요.(- 제외)";
        telMessage.classList.remove('error', 'confirm');
        memberTel.value="";
        checkObj.memberTel=false;
        return;
    }
    //뭔가 작성 한 경우->정규식 검사
    const regExp=/^01[0-9]{1}[0-9]{3,4}[0-9]{4}$/;
    if(!regExp.test(inputTel)){
        //정규식 만족하지 않으면
        telMessage.innerText = "휴대폰 번호 형식에 맞지 않습니다.";
        telMessage.classList.add("error");
        telMessage.classList.remove("confirm");
        checkObj.memberTel=false;
        return;
    }
    //정규식도 만족하는 경우 ->중복검사하기
    fetch("/member/checkTel?memberTel="+inputTel)
    .then(response => response.text())
    .then(count=>{
        if(count>0){
            //중복
            telMessage.innerText = "중복되어 사용할 수 없는 번호입니다.";
            telMessage.classList.add("error");
            telMessage.classList.remove("confirm");
            checkObj.memberTel=false;
            return;
        }
        //중복 아닌 경우
        telMessage.innerText = "사용 가능한 전화번호 입니다.";
        telMessage.classList.add("confirm");
        telMessage.classList.remove("error");
        checkObj.memberTel=true;
    })
    .catch(e=>{
    console.log(e);
    });
});








//----------------------------------------------------------------------------------------------------
/* 이메일 인증 관련 */
const sendAuthKeyBtn = document.querySelector("#sendAuthKeyBtn");
//인증번호 보내기 버튼

const authKey = document.querySelector("#authKey");
//인증번호 입력 input태그

const checkAuthKeyBtn = document.querySelector("#checkAuthKeyBtn");
//인증번호 입력 후 확인하는 버튼

const authKeyMessage = document.querySelector("#authKeyMessage");
//인증번호 관련 메시지 출력 span태그

let authTimer; //인증번호 보내면 얼마 안에 인증해라 하는 타이머 역할을 할 
//setInterval(일정 간격마다 실행하라는 JS 인터벅 객체)을 저장할 변수

//시작 값 저장
const initMin = 4;  //타이머 초기값(분)
const initSec = 59; //타이머 초기값(초)
const initTime = "05:00"; //맨 처음에 화면에 보여질 숫자 5분
//5분은 4분 59초부터 셈

let min = initMin; //실제 줄어드는 시간을 저장할 변수
let sec = initSec; //실제 줄어드는 시간을 저장할 변수
//타이머가 인증번호 받기 버튼 눌렀을 때부터 타이머 동작하도록!
sendAuthKeyBtn.addEventListener("click", ()=>{
    //클릭 되자마자
    checkObj.authKey=false;
    document.querySelector("#authKeyMessage").innerText="";

    //중복되지 않은, 유효한 이메일을 입력한 경우가 아니면
    if(!checkObj.memberEmail){
        //이메일이 유효하지 않은 경우
        alert("유효한 이메일 작성 후 클릭해 주세요.");
        return;
        //유효한 이메일이 작성되지 않았으면 이메일 받기 버튼 못누르도록 막기
    }
    //클릭 시 타이머 숫자 초기화
    min = initMin;
    sec = initSec;

    //이전 동작중인 인터벌 클리어하기
    clearInterval(authTimer); //인증번호 받기 버튼 클릭 할 때마다 인터벌이 시작되는데 그러면 점점 빨라져서 이거 써야
    //이전에 있던 인터벌 지우고 새로 만드는 것이 됨

    //인증번호 받기 클릭
    checkObj.authKey=false; //인증 유효성 검사 여부 false

    //******************************************************************* */
    //비동기로 서버에서 메일 보내기(이거 하는 동안 아래의 코드들이 실행안되는 것 아님!)(기다리지 않고 실행됨)
    //심부름 보낼 때부터 타이머 시작됨
    //get방식으로 하면 주소에 쓰면 돼서 get방식으로 하면 안된다!->다른 사람이 막 이메일 많이 보낼 수 있음
    //->Post방식으로 해보기
    fetch("/email/signup", { ///비동기는 이 패치 코드 수행되는 동안 아래 코드들 수행됨
        method : "POST",
        headers : {"Content-Type" : "application/json"}, //객체 타입
        body : memberEmail.value //인풋태그에 작성된 value값을 넘기겠다
    }) //1이나 0이 돌아옴
    .then(response=>response.text())
    .then(result =>{
        if(result ==1){
            //인증번호 발송 성공
            console.log("인증번호 발송 성공!!!");
        }else{
            console.log("인증번호 발송 실패...");
        }
    });

    //******************************************************************* */
    //메일은 비동기로 서버에서 보내라고 하고
    //화면에서는 타이머 시작시키기
    authKeyMessage.innerText=initTime; //처음에는 05:00 세팅해둠
    authKeyMessage.classList.remove('confirm', 'error'); //검정 글씨로 바꾸기

    // setInterval(함수, 지연시간(ms단위)) 
    //  - 지연시간 만큼 시간이 지날 때 마다 함수를 한 번씩 수행
    //지연시간에 10초가 적혀 있고, 함수가 1을 출력하는 함수인 경우
    //setInterval 해석과 동시에 1 나오지 않고, 해석 후 10초 후에 최초의 1이 출력된다

    //근데 언젠가는 멈추게 해야한다
    //clearInterval(Interval이 저장된 변수)
    // - 매개변수로 전달 받은 Interval을 지워버린다

    alert("인증번호가 발송되었습니다.");

    //인증 시간 출력(1초마다 동작하며 5분에서 시간 줄여나감)
    authTimer = setInterval( ()=>{ //지정된 시간만큼 지날 때마다 수행
        authKeyMessage.innerText=`${addZero(min)}:${addZero(sec)}`;

        //0분 0초인 경우(화면에 00:00 출력한 후)
        if(min==0 && sec==0){
            //그 동안 이메일 인증 못함
            checkObj.authKey=false;
            clearInterval(authTimer); //안멈추면 -1초, -2초 됨
            //interval 멈춤

            // 빨간색으로 바꾸기
            authKeyMessage.classList.add("error");
            authKeyMessage.classList.remove("confirm");
            return;
        }


        if(sec ==0){
            //0초를 출력한 후
            sec=60; //그래야 다음 코드에서 59로 된다
            min--; //1분 줄이기
        }
        sec--; //1초 감소시키기
    } , 1000); //ms = 1/1000초
    //Interval을 저장할 변수







});

//----------------------------------------------------------------------------------------------------
function addZero(number){
    //전달받은 숫자가 1자리 숫자인 경우
    //분,초에 0이라는 숫자 붙여서 반환해주는 함수
    if(number<10) return "0"+number;
    else          return number;
}
//----------------------------------------------------------------------------------------------------
/* 회원 가입 버튼이 클릭되었을 때 전체 필수 입력값들의 유효성 검사 여부 확인 ->모두 true여야 가입 되도록 */
//가입버튼이 클릭되었을 때 ->이걸로 하면 안되고
//form태그가 signUpForm이 제출할 때
const signUpForm = document.querySelector("#signUpForm");
signUpForm.addEventListener("submit", e=>{
    //회원 가입 form 제출 시
    //버튼을 누르든, 엔터를 누르든 하면 제출이 된다

    //checkObj 객체에 저장된 값(value) 중 하나라도 false가 있으면 제출 막기
    
    //checkObj를 하나씩 꺼내서 보기
    //for  ~ in : 객체 전용 향상된 for문
    for(let key in checkObj){
        //checkObj요소의 key값을 순서대로 꺼내옴
        if(!checkObj[key]){
            //꺼내온 key값에 해당되는 value값을 얻어오면 true나 false임
            //하나라도 false인 경우 실행됨

            //뭐를 잘못 썼는지 알려주기
            let str; //출력할 메시지를 저장할 변수
            switch(key){
                //하나씩 꺼낸 key값에 따라서
                /* 뭐가 유효하지 않는 지 key값을 찾은것임 */
                case "memberEmail" :  str="이메일이 유효하지 않습니다."; break;
                case "authKey" :  str="이메일이 인증되지 않았습니다."; break;
                case "memberPw" :  str="비밀번호가 유효하지 않습니다."; break;
                case "memberPwConfirm" :  str="비밀번호가 일치하지 않습니다."; break;
                case "memberNickname" :  str="닉네임이 유효하지 않습니다."; break;
                case "memberTel" :  str="전화번호가 유효하지 않습니다."; break;
                
            }
            alert(str); //경고창 출력
            document.getElementById(key).focus(); //key값을 아이디로 가지는 요소에 작성 가능하도록 초점 이동시키기
            e.preventDefault(); //form태그인 기본 이벤트인 제출 막기
            return; //하나 false이면 더 검사할 필요 없다
        }
    }
});
//---------------------------------------------------------
//인증하기 버튼 클릭 시 
//입력된 인증번호를 비동기로 서버에 제출(전달)
//서버에서, 입력된 인증번호와 발급된 인증번호가 같은 지 비교
//->같으면 1, 아니면 0 반환하도록 만들어보기
//단, 타이머가 00:00초가 아닐 경우에만 수행해야 한다(제한 시간 후에 입력하면 안됨) 
checkAuthKeyBtn.addEventListener("click", ()=>{
    //제한시간 이내인지부터 확인하기
    if(min===0 && sec ===0){
        //타이머가 0분 0초인 경우(시간 초과)
        alert("인증번호 입력 제한 시간을 초과하였습니다.");
        return; //이 함수(이벤트 핸들러 함수)가 종료된다
    }
    //타이머가 아직 시간 남은 경우
    //비동기로 인증하기

    if(authKey.value.length<6){
        //인증번호가 6자리여서, 인증번호가 제대로 입력되지 않은 경우
        alert("인증번호를 정확히 입력해 주세요.");
        return;
    }
    const obj={
        "email" : memberEmail.value, //입력 받은 이메일
        "authKey" : authKey.value //입력 받은 이메일과 입력 받은 인증번호를 객체로 만들어두기
}; //객체 만들기
    fetch("/email/checkAuthKey", {
        method : "POST",
        headers : {"Content-Type" : "application/json"},
        body : JSON.stringify(obj) //JS의 객체(obj)를 Java에서는 인식을 못해서 문자열화 해서 전달
        //인증번호만 보내면 안되고 
        //body에는 전달할 데이터 적음(입력한 인증번호를 전달)
        //method, headers, body 외에도 작성가능한 것 더 있음
    }) //인증하는 것 get방식으로 하면 안좋다 -> POST방식으로!
    .then(resp =>resp.text())
    .then(result =>{
        //result값에 따라서 코드 수행
        if(result ==0){ //result 는 문자열 "0"으로 넘어와서 ===쓰면 안됨
            // == : 값만 비교
            // === : 값 + 타입 비교
            //엥근데 두개 쓰면 안되고 세개 써야 됨!

            //실패
            alert("인증번호가 일치하지 않습니다.");
            checkObj.authKey=false;
            return;
        }
        clearInterval(authTimer); //이걸 안하면 안됨(타이머 멈추기!!!(타이머 더이상 돌지 않도록))
        authKeyMessage.innerText="인증 되었습니다.";
        authKeyMessage.classList.remove("error");
        authKeyMessage.classList.add("confirm"); //초록글씨
        checkObj.authKey=true; //인증번호 검사 여부를 true로 바꾼다(인증 완료)
    })
});



















