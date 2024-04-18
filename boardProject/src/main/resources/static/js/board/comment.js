/* *************댓글 목록 비동기 조회(ajax) 
    비동기로 데이터 요청해서 
    동기식 : 순차적으로 진행 -> 장점 : 동시에 진행하지 않아서 충돌하지 않음
                                ->단점 : 페이지 깜박이고, 느림(forward,redirect)
    비동기식 : 동시에 수행 -> 장점 : 상대적으로 빠르고 사용자 경험이 풍부해짐
                            ->단점 : 비동기가 많을 수록 페이지가 복잡해짐
    */
   //함수
const selectCommentList = ()=>{
    //바로 비동기 요청
    //fetch API : JS 내장 비동기 실행 가능 코드

    //[GET]
    //fetch(주소?쿼리스트링) - 데이터를 주소에 담아서 보냄

    //[POST,PUT,DELETE,PATCH] - 데이터를 body에 담아서 보냄
    //fetch(주소, {
    // method : "",
    // headers : "",
    // body : "",
    //})

    /*REST(REpresentational State Transfer) API 
    - 자원(응답,데이터,파일)을 이름(주소)으로 
        구분(REpresentational)하여 
        자원의 상태(State)를 
        주고 받는(Transfer) 것
        ->자원의 이름(주소)을 명시하고
            HTTP Method(GET,POST,PUT,DELETE,PATCH)를 이용해
            지정된 자원에 대한 CRUD 진행

        자원의 이름(주소)는 하나만 지정 (ex. /comment )
        method
        그 전에는 /selectBoardList, /selectBoard, /member/search
        이렇게 많이 했는데
        멤버 조회하고싶으면 /member라고만 쓰고
        게시글 조회하고싶으면 /board라고만 쓰자

        메서드 별로 동작 다르게 하게 해서
        이름을 간단하게 단순화하는 것
            [하나의 주소]
            삽입(생성)==POST    (Create)
            조회==GET           (Read)
            수정==PUT           (Update)
            삭제==DELETE        (Delete)

            /comment주소로 주고받을건데
            조회하고싶으면 comment를 GET방식 요청할거다
    
    */
   //REST API 사용
   //몇 번 게시글의 댓글을 볼 지 전달

   //GET방식 요청
    fetch("/comment?boardNo="+boardNo) //boardDetail.html 밑 script에 전역변수
    //자바의 리스트는 js에서 
    .then(resp=>resp.json())
    // resp.json()
    //컨트롤러에서 JSON으로 바꿔서 보내줬는데
    //  - 응답 받은 JSON 데이터를 JS 객체로 변환
    
    .then(commentList =>{
        console.log(commentList);

        //화면에 존재하는 기존 댓글 목록을 삭제 후 
        //조회된 commentList를 이용해서 새로운 댓글 목록 출력

        //li태그를 감싸고 있는 #commentList의 안의 내용을 다 지우겠다

        //ul태그(댓글 목록을 감싸는 요소)
        const ul = document.querySelector("#commentList");
        ul.innerText=""; //기존 댓글 목록 다 삭제

        //비동기로 조회한 데이터로 화면 만들어 넣기
        /******** 조회된 commentList를 이용해 반복하면서 댓글 출력 *******  */
        for(let comment of commentList){
            //조회된 댓글 하나씩 꺼낼건데 댓글 하나도 없으면 이 for문이 한 번도 반복 안할거다
            //li태그부터 만들기(행 생성)+클래스 추가
            const commentRow = document.createElement("li");
            commentRow.classList.add("comment-row");

            //대댓글(자식 댓글)인 경우
            //"child-comment" 클래스 추가
            if(comment.parentCommentNo !=0){
                commentRow.classList.add("child-comment");
            }

            
            if(comment.commentDelFl=='Y'){
                //만약 삭제된 댓글이지만 자식 댓글이 존재하는 경우
            //삭제된 부모 댓글의 내용 대신 삭제된 댓글입니다 나오고
            //그 자식댓글은 그대로 보이도록 하기
            commentRow.innerText="삭제된 댓글입니다.";
            }
            else{
                //삭제되지 않은 댓글인 경우->화면 만들어 넣기

                const commentWriter = document.createElement("p");
                commentWriter.classList.add("comment-writer");

                //프로필 이미지
                const profileImg = document.createElement("img");
                if(comment.profileImg==null) profileImg.src=userDefaultImage; //기본이미지
                else  profileImg.src=comment.profileImg; //회원이미지

                //닉네임
                const nickname = document.createElement("span");
                nickname.innerText=comment.memberNickname; //조회 결과값 그대로 넣어주기

                //작성일
                const commentDate = document.createElement("span");
                commentDate.classList.add("comment-date");
                commentDate.innerText=comment.commentWriteDate;

                //작성자 영역(commentWriter)에 프로필 이미지,닉네임, 작성일 append
                commentWriter.append(profileImg, nickname, commentDate);

                //댓글 행에 작성자 영역 추가
                commentRow.append(commentWriter);

                //----------------------------------------------------
                //댓글 내용
                const content = document.createElement("p");
                content.classList.add("comment-content");
                content.innerText=comment.commentContent;
                commentRow.append(content);
                //----------------------------------------------------
                //버튼 영역
                const commentBtnArea = document.createElement("div");
                commentBtnArea.classList.add("comment-btn-area");

                //답글 버튼
                const childCommentBtn = document.createElement("button");
                childCommentBtn.innerText="답글";

                //답글 버튼에 onclick 이벤트 리스너 추가
                childCommentBtn.setAttribute("onclick",
                `showInsertComment(${comment.commentNo},this)`);
                //onclick 속성 추가할건데 값으로 이걸 넣는다

                //버튼 영역에 답글 추가
                commentBtnArea.append(childCommentBtn);

                //로그인한 회원 번호가 댓글 작성자 번호와 같을 때
                //댓글 수정/삭제 버튼 출력되도록
                if(loginMemberNo !=null && loginMemberNo == comment.memberNo){
                    //로그인이 되어있고
                    //로그인 한 사람의 번호랑 댓글 작성인의 번호가 같을 때
                    const updateBtn = document.createElement("button");
                    updateBtn.innerText="수정";

                    //수정 버튼에 onclick 이벤트 리스너 추가
                    updateBtn.setAttribute("onclick",
                    `showUpdateComment(${comment.commentNo},this)`);
                    //--------------------------------------
                    //삭제버튼
                    const deleteBtn = document.createElement("button");
                    deleteBtn.innerText="삭제";

                    //수정 버튼에 onclick 이벤트 리스너 추가
                    deleteBtn.setAttribute("onclick",
                    `deleteComment(${comment.commentNo})`);

                    //--------------
                    //버튼 영역에 수정,삭제 버튼 추가
                    commentBtnArea.append(updateBtn, deleteBtn);
                }


                //행에 버튼 영역 추가
                commentRow.append(commentBtnArea);

            }

            //댓글 목록(ul)에 행(li) 추가
            ul.append(commentRow);

        }

    });

};

//------------------------------------------------------
/*댓글 등록(ajax) */
const addContent = document.querySelector("#addComment"); //button
const commentContent = document.querySelector("#commentContent"); //textarea

addContent.addEventListener("click", e=>{
    //댓글도 줄이 바뀔 수 있으니까
    //엔터로 안함
    //댓글 등록 버튼 클릭 시

    //로그인이 되어있지 않은 경우
    if(loginMemberNo ==null){
        alert("로그인 후 이용해 주세요.");
        return; //early return (함수 끝까지 안가고 일찍 리턴하는 것)
    }

    //로그인 했는데 댓글 내용을 아무것도 안 쓰고 버튼 클릭한 경우
    if(commentContent.value.trim().length==0){
        alert("내용 작성 후 등록 버튼을 클릭해 주세요.");

        //다시 쓸 수 있게 포커스 맞춰주기
        commentContent.focus();
        return; //early return
    }

    //이제 비동기로 데이터 보내기
    //댓글 내용, 게시글번호, 작성자 번호 담아서 넘겨주기
    //ajax 를 이용해 댓글 등록 비동기 요청하기
    const data = {
        "commentContent" : commentContent.value,
        "boardNo"       : boardNo,
        "memberNo"      : loginMemberNo
        //근데 회원번호는 여기서 안 담아서 보내고
        //컨트롤러에서 세션에 있는 것을 꺼내서 이용해도 된다
    }
    fetch("/comment",{
        method : "POST", 
        headers : {"Content-Type" : "application/json"},
        //우리가 전달하려는 값이 JSON이다
        //data를 그냥 보내면 JSON이 아닌 JS 객체를 보내는거여서 안됨
        //JSON은 JS 객체 양쪽에 쌍따옴표까지 붙은 꼴의 문자열이다
        body    : JSON.stringify(data)

    }) //REST API 
    .then(resp=>resp.text()) //숫자 result가 들어오니까 문자 그대로 해석
    .then(result=>{
        if(result>0){
            //댓글 등록 성공
            alert("댓글이 등록 되었습니다.");
            commentContent.value=""; //작성한 댓글 내용 지우기
            //새 댓글 내용이 DB에는 등록됐는데 아직 화면에는 추가 안됐다

            selectCommentList(); //댓글 목록을 다시 조회해서 화면에 출력하기 -> 새로 등록된 댓글도 화면에 보이게 된다
        }else{
            alert("댓글 등록 실패..");
        }
    })
    .catch(err=>console.log(err)); //에러 발생 시 콘솔로 에러 출력
});
/**답글 작성 화면 추가
 * 
 * @param {*} parentCommentNo 
 * @param {*} btn 
 */
const showInsertComment = (parentCommentNo, btn)=>{ //답글 적기 위한 화면 만들어주는 함수

  // ** 답글 작성 textarea가 한 개만 열릴 수 있도록 만들기 **
const temp = document.getElementsByClassName("commentInsertContent");

  if(temp.length > 0){ // 답글 작성 textara가 이미 화면에 존재하는 경우
    //몇 개 있는지 세서 1개이상 있으면
    if(confirm("다른 답글을 작성 중입니다. 현재 댓글에 답글을 작성 하시겠습니까?")){
      temp[0].nextElementSibling.remove(); // 버튼 영역부터 삭제 //1개니까 0번 인덱스에 있을거다 //다음요소인 등록/취소버튼
      temp[0].remove(); // textara 삭제 (기준점은 마지막에 삭제해야 된다!)
    
    } else{
      return; // 함수를 종료시켜 답글이 생성되지 않게함.
    }
}

  // 답글을 작성할 textarea 요소 생성
const textarea = document.createElement("textarea");
textarea.classList.add("commentInsertContent");

  // 답글 버튼의 부모의 뒤쪽에 textarea 추가
  // after(요소) : 뒤쪽에 추가
btn.parentElement.after(textarea); 


  // 답글 버튼 영역 + 등록/취소 버튼 생성 및 추가
const commentBtnArea = document.createElement("div");
commentBtnArea.classList.add("comment-btn-area");


const insertBtn = document.createElement("button");
insertBtn.innerText = "등록";
insertBtn.setAttribute("onclick", "insertChildComment("+parentCommentNo+", this)"); //onclick으로 구현해둠


const cancelBtn = document.createElement("button");
cancelBtn.innerText = "취소";
cancelBtn.setAttribute("onclick", "insertCancel(this)"); //onclick으로 구현해둠

  // 답글 버튼 영역의 자식으로 등록/취소 버튼 추가
commentBtnArea.append(insertBtn, cancelBtn);

  // 답글 버튼 영역을 화면에 추가된 textarea 뒤쪽에 추가
textarea.after(commentBtnArea);
};
//------------------------------------------------------------------------------------------------------
/******* 답글(자식 댓글) 작성 취소 시  */
//취소 버튼을 눌렀을 때 그 버튼을 감싸고 있는 것의 이전 요소를
//먼저 지우고 
//기준을 this라고 지었는데
//기준을 먼저 지워버리면 그 앞의 요소를 탐색할 수 없어서
//기준을 마지막에 지워야 함

/**답글 (자식 댓글) 작성 취소
 * 
 * @param {*} cancelBtn : 취소버튼
 */
const insertCancel = (cancelBtn)=>{
    //취소 버튼 부모의 이전 요소(textarea)를 삭제
    cancelBtn.parentElement.previousElementSibling.remove();
    
    //취소 버튼이 존재하는 버튼 영역 삭제
    cancelBtn.parentElement.remove();
};
/**답글(자식 댓글) 등록
 * 
 * @param {*} parentCommentNo : 부모 댓글 번호
 * @param {*} btn : 클릭된 등록 버튼
 */
const insertChildComment = (parentCommentNo, btn)=>{

    //답글 내용이 작성된 textarea 요소
    const textarea = btn.parentElement.previousElementSibling;

    //간단히 유효성 검사
    if(textarea.value.trim().length==0){
        //안썼다
        alert("내용 작성 후 등록 버튼을 클릭해 주세요.");
        textarea.focus();
        return;
    }

    //유효하면 INSERT 하는 비동기 코드
    //답글 == 자식 댓글
    // -> 내용, 게시글번호, 회원번호, 부모 댓글 번호를 담아서 보내야함
     //ajax 를 이용해 댓글 등록 비동기 요청하기
    const data = {
        "commentContent" : textarea.value,
        "boardNo"       : boardNo,
        "memberNo"      : loginMemberNo,
        "parentCommentNo" : parentCommentNo //답글이니까 부모 댓글 번호 알아야함
        //근데 회원번호는 여기서 안 담아서 보내고
        //컨트롤러에서 세션에 있는 것을 꺼내서 이용해도 된다
    };
    fetch("/comment",{
        method : "POST", 
        headers : {"Content-Type" : "application/json"},
        //우리가 전달하려는 값이 JSON이다
        //data를 그냥 보내면 JSON이 아닌 JS 객체를 보내는거여서 안됨
        //JSON은 JS 객체 양쪽에 쌍따옴표까지 붙은 꼴의 문자열이다
        body    : JSON.stringify(data)

    }) //REST API 
    .then(resp=>resp.text()) //숫자 result가 들어오니까 문자 그대로 해석
    .then(result=>{
        if(result>0){
            //댓글 등록 성공
            alert("답글이 등록 되었습니다.");
            //새 댓글 내용이 DB에는 등록됐는데 아직 화면에는 추가 안됐다

            selectCommentList(); //댓글 목록을 다시 조회해서 화면에 출력하기 -> 새로 등록된 댓글도 화면에 보이게 된다
        }else{
            alert("답글 등록 실패..");
        }
    })
    .catch(err=>console.log(err)); //에러 발생 시 콘솔로 에러 출력
};
//-------------------------------------------------------------------------
/**댓글 삭제
 * 
 * @param {*} commentNo 
 */
const deleteComment = commentNo=>{
    if(!confirm("삭제 하시겠습니까?")){
        //취소 클릭 시
        return; //early return
    }
    fetch("/comment",{
        method : "DELETE",
        headers : {"Content-Type" : "application/json"},
        body : commentNo
    })
    .then(resp=>resp.text())
    .then(result=>{
        if(result>0){
            //삭제 성공 시
            alert("삭제되었습니다.");
            //댓글이 DB에서만 사라지고 화면에는 그대로 있을거니까
            //다시 조회해서 화면 다시 만들기
            selectCommentList();
        }else{
            //삭제 실패 시
            alert("삭제 실패...");
        }
    })
    .catch(err=>console.log(err));
};
//-------------------------------------------------------------------------------------------------------------------
let beforeCommentRow; //수정 취소 시 원래 댓글 모습대로 돌아가기 위한 백업본(수정 누르기 전의 모습을 백업해둔 변수)!
/**댓글 수정 화면으로 전환하기
 * 
 * @param {*} commentNo 
 * @param {*} btn 
 */
const showUpdateComment = (commentNo, btn)=>{
    /*댓글 수정 화면이 딱 1개만 열릴 수 있도록 하기 */
    const temp = document.querySelector(".update-textarea"); //이게 존재한다면 열려있는 게 있는거다
    if(temp !=null){
        // .update-textarea가 존재 == 열려있는 댓글 수정창이 존재
        if(confirm("수정 중인 댓글이 있습니다. 현재 댓글을 수정하시겠습니까?")){
            //수정하겠다고 한 경우
            //수정 버튼 누르기 전의 모습을 beforeCommentRow에 백업해둠
            const commentRow = temp.parentElement;
            commentRow.after(beforeCommentRow); //commentRow의 다음 요소로 beforeCommentRow를 넣을거야
            commentRow.remove(); //그다음 이거 지우면 commentRow가 beforeCommentRow로 바뀐 것처럼 됨
            //백업이 기존 것의 위치로 당겨져 대체된다
        }else{
            //취소 누른 경우
            return; //수정 안하겠다
        }
    }
    //-------------------------------------------------------
    // 1. 댓글 수정이 클릭된 행(.comment-row) 선택
    const commentRow = btn.closest("li"); //부모 중에서 가장 가까운 li태그 찾기
    //console.log(commentRow);

    // 2. 행 전체를 백업하기
    beforeCommentRow=commentRow.cloneNode(true); //복제함
    // 요소.cloneNode(true) : 요소 복제
    // 매개변수 true : 하위 요소도 다 복제
    //console.log(beforeCommentRow);

    // 3. 기존 댓글에 작성되어있던 내용만 얻어오기
    let beforeContent = commentRow.children[1].innerText;
    //console.log(commentRow.children[1].innerText);
    //console.log(commentRow.children[1].innerHTML);
    //innerText로 
    //document.querySelector("#commentContent").value=commentRow.children[1].innerText;

    // 4. 댓글 행 내부를 모두 삭제하기
    commentRow.innerHTML="";

    // 5. textarea 생성 + 클래스 추가 + 내용 추가
    const textarea = document.createElement("textarea");
    textarea.classList.add("update-textarea");
    textarea.value = beforeContent; //화면에 써져있던 글이었는데 textarea의 value로 넣는다

    // 6. 댓글 행에 textarea 추가하기
    commentRow.append(textarea);

    // 7. 수정/취소버튼 감싸는 버튼 영역 만들기
    const commentBtnArea = document.createElement("div");
    commentBtnArea.classList.add("comment-btn-area");

    // 8. 수정 버튼 만들기
    const updateBtn = document.createElement("button");
    updateBtn.innerText="수정";
    updateBtn.setAttribute("onclick", `updateComment(${commentNo},this)`); //이 함수가 fetch API 호출
    //onclick 속성 추가

    // 9. 취소 버튼 생성
    const cancelBtn = document.createElement("button");
    cancelBtn.innerText="취소";
    cancelBtn.setAttribute("onclick", "updateCancel(this)"); //이 함수가 fetch API 호출

    // 10. 버튼 영역에 수정/취소 버튼 추가 후
    //      댓글 행에 버튼 영역 추가
    commentBtnArea.append(updateBtn, cancelBtn);
    commentRow.append(commentBtnArea);
};
//------------------------------------------------------------------------
/**댓글 수정 취소
 * 
 * @param {*} btn : 취소 버튼
 */
const updateCancel = (btn)=>{
    if(confirm("취소 하시겠습니까?")){
        const commentRow = btn.closest("li"); 
        commentRow.after(beforeCommentRow); //commentRow의 다음 요소로 beforeCommentRow를 넣을거야
        commentRow.remove(); //그다음 이거 지우면 commentRow가 beforeCommentRow로 바뀐 것처럼 됨
        //백업이 기존 것의 위치로 당겨져 대체된다
    }
};
//-----------------------------------------------------------------------------
/**댓글 수정
 * 
 * @param {*} commentNo : 수정할 댓글 번호
 * @param {*} btn : 클릭된 수정 버튼
 */
const updateComment = (commentNo, btn)=>{
    //수정된 내용이 작성된 textarea 얻어오기
    const textarea = btn.parentElement.previousElementSibling;

    //작성됐는지 유효성 검사
    if(textarea.value.trim().length==0){
        //수정한다더니 다 지운 경우
        alert("댓글 작성 후 수정 버튼을 클릭해 주세요.");
        textarea.focus();
        return;
    }

    //유효한 경우 update하기
    //댓글 수정 ajax 위해서 필요한 것
    //몇 번 내용이 뭘로 수정됐는지
    const data = {
        "commentNo" : commentNo, //매개변수로 전달받아온 것
        "commentContent" : textarea.value
    };
    fetch("/comment", {
        method : "PUT",
        headers : {"Content-Type" : "application/json"},
        body : JSON.stringify(data)
    })
    .then(resp=>resp.text())
    .then(result=>{
        if(result>0) {
            alert("댓글이 수정 되었습니다.");
            selectCommentList(); //화면 다 지우고 다시 만들래 ->근데 좋은 방법은 아님
            //리액트, 뷰(virtual dom 이용한 프레임워크) -> 부분부분만 바뀌도록 만듦
        }else{
            alert("댓글 수정 실패");
        }
    })
    .catch(err=>console.log(err));

};





















