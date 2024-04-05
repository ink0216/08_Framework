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
//--------------------------------------------------------------------------------------------------------
//changePw form태그가 있을 때에만 
/* 비밀번호 수정 */
//비밀번호 변경 form태그
const changePw = document.querySelector("#changePw");
if(changePw !=null){
    changePw.addEventListener("submit", e=>{
        //제출 되었을 때 검사해보겠다
        
        
        
        //currentPw, newPw, 
        //form태그 안의 input태그들 다 얻어와서 배열로도 할 수 있다!
        const currentPw = document.querySelector("#currentPw");
        const newPw = document.querySelector("#newPw");
        const newPwConfirm = document.querySelector("#newPwConfirm");

        //- 값을 모두 입력 했는가
        let str; //변수 선언했는데 비어있음 -> 이 상태로 출력하면 undefined 상태이다!!!
        //js의 변수는 처음에 자료형 정해져있지 않고, 값이 대입될 때 자료형이 정의된다!!

        if(currentPw.value.trim().length ==0){
            str="현재 비밀번호를 입력해 주세요";
        }else if(newPw.value.trim().length ==0){
            str="새 비밀번호를 입력해 주세요";
        }else if(newPwConfirm.value.trim().length ==0){
            //여기는 else 하면 안되고 else if만 해야한다
            str="새 비밀번호 확인을 입력해 주세요";
        }
        if(str !=undefined){
            //str에 뭔가 값이 대입된 경우
            //위의 세 if문 중 하나가 실행됐다는 뜻
            alert(str);
            e.preventDefault();
            return;
        }

        //- 새 비밀번호 정규식
        const regExp = /^[a-zA-Z0-9!@#_-]{6,20}$/;
        if(!regExp.test(newPw.value)){
            //새 비밀번호가 정규식 통과 못한 경우
            alert("새 비밀번호가 유효한 형식이 아닙니다.");
            e.preventDefault();
            return;
        }

        //- 새 비밀번호 == 새 비밀번호 확인
        if(newPw.value != newPwConfirm.value){
            alert("새 비밀번호가 일치하지 않습니다.");
            e.preventDefault();
            return;
        }
    });
}
//-------------------------------------------------------------------------------
/* 탈퇴 유효성 검사 */
//탈퇴 form 태그
const secession = document.querySelector("#secession");
if(secession !=null){
    secession.addEventListener("submit", e=>{
        const memberPw = document.querySelector("#memberPw");
        const agree = document.querySelector("#agree");
         // - 비밀번호 입력 되었는지 확인
        if(memberPw.value.trim().length==0){
            alert("비밀번호를 입력해 주세요.");
            e.preventDefault();
            return; //더이상 검사할 필요 없어
        }
    // - 약관 동의 체크 확인
    //checkbox, radio의 .checked 속성
    // - checked -> 체크 시 true, 미체크시 false가 반환된다
    // 이걸 이용해서 값을 대입할 수도 있다 - checked = true  == 체크하기
    //                                       checked = false == 체크 해제하기
    if(!agree.checked){
        //체크 안 한 경우
        // == if(agree.checked == false)
        alert("탈퇴 약관에 동의해 주세요.");
        e.preventDefault();
        return;
    }
    // - 정말 탈퇴? 물어보기
    if(!confirm("정말 탈퇴 하시겠습니까?")){
        //취소 누른 경우
        alert("취소 되었습니다.");
        e.preventDefault();
        return;
    }
    });
}
//------------------------------------------------------------------------------------------------------------------------
/* 프로필 이미지 추가/변경/삭제 관련 */

//이 파일을 여러 군데에서 사용해서 form태그가 있을 때에만 이 코드 실행하겠다고 써야 함
//프로필 이미지 페이지의 form태그
const profile = document.querySelector("#profile"); 

//두 개의 변수 선언(선생님이 만든 로직)
let statusCheckCheck = -1; 
//프로필 이미지가 새로 업로드 되거나 삭제 되었음을 기록하는 상태변수(어떤 상태인지 기록해둠)
// -1 : 초기상태(변화 없음)
//  0 : 프로필 이미지 삭제됨
//  1 : 새 이미지가 선택됨

let backupInput; //input type="file"태그의 값이 변경되었을 때 변경된 상태를 백업해서 저장할 변수
//백업을 어떻게 할거냐!
//->파일이 선택/취소된 input을 복제해서 저장해둘거다 (요소를 복제하는 함수 존재!!)
//요소.cloneNode(true/false 지정가능) == 요소를 복제하는 함수 ->true 작성 시 내부의 하위 요소들도 다 복제함
//                                                            ->false 작성 시

if(profile !=null){ //화면에 있을 때에만 요소 얻어오기
    /* html요소 세 가지 얻어오기  */
    const profileImg = document.querySelector("#profileImg"); //프로필 이미지 보여지는 img태그 요소

    let imageInput = document.querySelector("#imageInput");
    //input type="file"태그(실제 업로드할 프로필 이미지를 선택하는 요소)
    //프로필 이미지를 바꿀 때 사용하는 imageInput input태그

    const deleteImage = document.querySelector("#deleteImage"); //프로필 이미지를 제거하고 기본 이미지로 변경하는 요소
    //deleteImage x버튼

    


    //imageInput의 값이 변했을 때 라는 이벤트 만들기
    //input type="file"의 값이 변했을 때 동작할 함수(이벤트 핸들러) 따로 만들어놓기
    const changeImageFn = e=>{ //총 세 군데에서 쓸 거여서 함수로 뺴놓기
        const maxSize=  1024*1024*5;  //5MB == 1024KB *5 ==1024Byte*1024*5
        //업로드 가능한 파일 최대 크기 지정하여 걸러내는 필터링할 용도(바이트 단위로 작성)

        console.log("e.target", e.target); //input이 나옴
        console.log("e.target.value", e.target.value); //변경된 값이 나옴(파일명)
        console.log("e.target.files", e.target.files); //변경된 값이 나옴(파일명) //input type="files"에만 존재하는 속성!!
        //선택된 파일에 대한 정보가 담긴 배열을 반환해 준다
        //  -> 왜 배열 ? multiple옵션에 대한 대비(여러 파일 선택할 수도 있어서!)
        //      하나밖에 선택 안해도 항상 0번 인덱스에 저장돼서 배열로 나온다

        //업로드된 파일이 1개 있으면 files[0]에 저장됨
        //업로드된 파일이 없으면 files[0] == undefined가 나온다!!(프로필 변경하려다 취소 누른 경우)
        console.log("e.target.files[0]", e.target.files[0]); //프로필이미지는 원래 한장씩만 선택 가능하니까 이거 이용!

        const file = e.target.files[0];
        //-----------------------------------
        //업로드 된 파일이 없다면 (프로필 사진 변경하려다 취소 누른 경우)
        if(file ==undefined){
            console.log("파일 선택 후 취소함");

            //파일 선택 후 취소 -> value=''(빈칸) 돼서 
            //  -> 선택한 파일 없음으로 기록됨
            //  -> backupInput으로 교체시켜서
            //     이전 이미지가 남아있는 것처럼 보이게 하겠다(기존거 지우고 백업했던 것을 넣겠다)

            //혹시 모르니 백업의 백업본 만들기
            const temp=backupInput.cloneNode(true); //false하면 그 하위 요소들은 백업되지 않음

            //imageInput 다음에 backupInput를 추가
            imageInput.after(backupInput);
            
            //화면에 존재하는 기존 input(imageInput)제거
            imageInput.remove();

            //imageInput 변수에 백업을 대입해서 대신하도록 함
            imageInput=backupInput;

            //밑의 imageInput에는 change 이벤트 리스너가 있는데
            // 화면에 추가된 백업본에는
            //이벤트 리스너가 존재하지 않기 때문에 추가해주기
            imageInput.addEventListener("change", changeImageFn);

            //한 번 화면에 추가된 요소(backupInput)는 재사용이 불가능하다
            // -> 그래서 backupInput의 백업본인 temp를 backupInput으로 변경
            backupInput=temp;
            return;
        }
        //파일 업로드가 됐을 경우(선택된 파일이 있을 경우)
        //-----------------------------------
        //선택된 파일이 최대 크기를 초과한 경우
        if(file.size>maxSize){
            alert("5MB 이하의 이미지 파일을 선택해 주세요.");
            if(statusCheck == -1){
                //초과해서 넣었을 때
                imageInput.value='';
                //선택한 이미지가 없는데 5MB를 초과하는 이미지를 선택한 경우
                //선택한 거 지워버려.
            }else{ //위의 것과 동작 원리 똑같다
                //선택한 이미지가 있는데 다음 선택한 이미지가 최대 크기 초과한 경우
                
            //혹시 모르니 백업의 백업본 만들기
            const temp=backupInput.cloneNode(true); //false하면 그 하위 요소들은 백업되지 않음

            //imageInput 다음에 backupInput를 추가
            imageInput.after(backupInput);
            
            //화면에 존재하는 기존 input(imageInput)제거
            imageInput.remove();

            //imageInput 변수에 백업을 대입해서 대신하도록 함
            imageInput=backupInput;

            //밑의 imageInput에는 change 이벤트 리스너가 있는데
            // 화면에 추가된 백업본에는
            //이벤트 리스너가 존재하지 않기 때문에 추가해주기
            imageInput.addEventListener("change", changeImageFn);

            //한 번 화면에 추가된 요소(backupInput)는 재사용이 불가능하다
            // -> 그래서 backupInput의 백업본인 temp를 backupInput으로 변경
            backupInput=temp;
            }
            return;
        }
        //-----------------------------------
        //최대 크기 안넘으므로 선택된 이미지 미리보기
        //선택 시 바로 스폰지밥 보이도록

        const reader = new FileReader() ; //JS에서 파일을 읽을 때 사용하는 객체
        //파일을 읽고 클라이언트 컴퓨터에 저장할 수 있음

        //선택한 파일(file)을 읽어와 BASE64 인코딩 형태로 읽어와
        //result 변수에 저장
        reader.readAsDataURL(file); // -> 이것도 읽어오기 이벤트(load 이벤트)
        //FileReader.readAsDataURL() : 지정된 파일을 읽어와서 읽어온 데이터는 이미지를 2진수로 바꾸고 
        // 그걸 또 압축한 형태로 result 속성에 담아서 가져옴
        //해석하면 다시 이미지로 바꿀 수 있다

        reader.addEventListener("load", e=>{
            //읽어오기가 끝났을 때 동작
            const url = e.target.result; //e.target == reader
            //reader.result에 읽어온 이미지파일이 BASE64 형태로 반환된 것이 들어있다

            //이 값을 img태그의 src 속성에 집어넣으면 이미지가 보인다!!!
            profileImg.setAttribute("src", url); //프로필 이미지 img태그 src 속성에 url을 추가하겠다

            //새 이미지가 선택된 상태를 statusCheck에 기록해둠
            statusCheck = 1;

            backupInput=imageInput.cloneNode(true); //파일이 선택된 input을 복제해서 백업을 해놓는다

        });





    }
    //change 이벤트 : 안에 작성된 값이 변했을 때
    //input type="file"인 경우 -> 선택된 파일 없음 -> 파일명.jpg 처럼 새로운 값이 기존 값과 다를 경우 발생*이벤트 인지
    imageInput.addEventListener("change", changeImageFn);
    
    //--------------------------------------
    // x버튼 클릭 시 기본 이미지로 변경
    deleteImage.addEventListener("click", ()=>{
        profileImg.src="/images/user.png"; //폴더에 있는 파일로 바꾸겠다
        //프로필 이미지 img태그를 기본 이미지로 변경
        // -> 미리보기에만 새 사진으로 바뀌고 변경 버튼 눌러도 원래 사진이다

        //input에 저장된 값(value)를 ''(빈칸)으로 변경
        // -> input에 저장된 파일 정보가 모두 사라짐(input type="file"의 특징!)
        imageInput.value=''; //데이터 삭제

        //삭제하면 백업본도 필요 없다
        backupInput.value=undefined; //백업본도 삭제

        //삭제 상태임을 기록
        statusCheck=0;
        //변수명 바꾸고 싶으면 : ctrl f -> 변수명 쓰고 바꿀 값 쓰고 오른쪽 버튼 누르면 다 바뀐다
    });
    //---------------------------
    // #profile (form태그) 제출 시 
    profile.addEventListener("submit", e=>{
        //언제는 제출 못하게 막겠다

        let flag=true; 
        if(loginMemberProfileImg ==null &&statusCheck ==1){
            //기존의 프로필 이미지가 없다가
            //새 이미지가 선택된 경우
            //->파일 업로드 해야된다
            flag=false; //false로 바뀌면 밑의 if문이 실행 안돼서 제출이 된다
        }
        if(loginMemberProfileImg !=null &&statusCheck==0){
            //기존의 프로필 이미지가 존재하다가 
            //삭제한 경우(기본 이미지로 돌리는 경우)
            flag=false;
        }
        if(loginMemberProfileImg !=null &&statusCheck==1){
            //기존의 프로필 이미지가 존재하다가 
            //새 이미지가 선택된 경우(새 이미지로 바뀌는 경우)
            flag=false;
        }
        if(flag){
            //flag 값이 true일 때 실행
            e.preventDefault();
            alert("이미지 변경 후 클릭하세요.");
            //이미지 변경 못하게 막기
        }
    
    });




}
/* input type="file" 사용 시 유의 사항 
    1. 파일 선택 후 취소를 누르면 선택한 파일이 사라진다(value == '')
    2. value로 대입할 수 있는 값은 사진을 선택하든지, 아니면 ''(빈칸)만 가능하다
    3. 선택된 파일 정보를 저장하는 속성은 
        value가 아니라 files이다
        input태그들은 다 값을 value 속성에 저장하지만
        input type="file"은 files 속성에 저장한다
*/


























