/* signup.js 참고하면 비슷하다 */
/* 회원 정보 수정 페이지 */
const updateInfo = document.querySelector("#updateInfo"); //querySelector는 css 선택자를 작성 //form 태그

//#updateInfo 요소가 존재할 때에만 수행
if(updateInfo !=null){ //회원 정보 수정 페이지에만 있다
    //form태그가 존재할 때에만 수행

    //회원가입할 때에는 입력할 때마다 동시에 유효성 검사 했는데
    //지금은 닉네임, 전화번호, 주소가 제출 되는 시점에 유효성 검사를 다 해볼거다

    updateInfo.addEventListener("submit", e=>{
        //form 제출 시

        const memberNickname = document.querySelector("#memberNickname");
        const memberTel = document.querySelector("#memberTel");
        const memberAddress = document.querySelectorAll("[name='memberAddress']"); //3개를 배열 형태로 반환
        
        //닉네임 유효성 검사
        if(memberNickname.value.trim().length==0){
            //닉네임 입력 안한 경우
            alert("닉네임을 입력해 주세요");
            e.preventDefault(); //제출 막기
            return;
        }

        /* 정규식 검사 */
        let regExp = /^[가-힣\w\d]{2,10}$/; //정규표현식
        if( !regExp.test(memberNickname.value)){
            //정규식에 맞지 않은 경우
            alert("닉네임이 유효하지 않습니다.");
            e.preventDefault(); //제출 막기
            return;
        }
        //**************************************************************************************************** */
        //닉네임 중복 검사는 나중에 추가 예정
        //비동기를 동기식으로 돌려서 하는 방법!
        //  (테스트 시 닉네임 중복 안되기 조심하기!!!!)
        //**************************************************************************************************** */

        //전화번호 유효성 검사
        if(memberTel.value.trim().length==0){
            //닉네임 입력 안한 경우
            alert("전화번호를 입력해 주세요");
            e.preventDefault(); //제출 막기
            return;
        }
        /* 정규식 검사 */
        regExp = /^01[0-9]{1}[0-9]{3,4}[0-9]{4}$/; //정규표현식
        if( !regExp.test(memberTel.value)){
            //정규식에 맞지 않은 경우
            alert("전화번호가 유효하지 않습니다.");
            e.preventDefault(); //제출 막기
            return;
        }
        
        
        //주소 유효성 검사
        //입력을 안하면 전부 안해야 하고
        //입력을 하면 전부 입력해야 한다

        //input요소에 써진 값이 빈칸일 때 해야하고, 요소가 빈칸일 때는 할 수 없으므로 .value 써야함!
        const addr0 = memberAddress[0].value.trim().length==0;  //배열 //true , false인 boolean이 저장됨
        const addr1 = memberAddress[1].value.trim().length==0;  //배열 //true , false인 boolean이 저장됨
        const addr2 = memberAddress[2].value.trim().length==0;  //배열 //true , false인 boolean이 저장됨
        //true라면 해당 칸에 아무것도 안썼다는 뜻

        //세 개가 모두 true이거나 모두 false일 때에만 제출되도록 하기

        //result1 : 모두 true인 경우만 true 저장 == 하나라도 false이면 false저장
        const result1 = addr0 && addr1 && addr2; //&&연산 : 모두 true여야 true가 나옴!!

        //result2 : 모두 false인 경우에만 true 저장 == 하나라도 true라면 false 저장
        const result2 = !(addr0 || addr1 || addr2);  
        //addr0 || addr1 || addr2 가 false인 경우 true 저장 

        if(!(result1 || result2)){ //모두 입력 또는 모두 미입력이 아니면
            //result1 || result2가 false인 경우 시행
            //둘 다 false일 때 수행

            //둘 다 true인 경우
            //주소 입력 세 칸 중 아무것도 입력 안했거나 모두 입력했을 경우
            alert("주소를 모두 작성 또는 미작성 해주세요.");
            e.preventDefault(); //기본 이벤트를 막는다 제출 막아야 할 때마다 이 코드 추가하기
        }

        
    });
}

























