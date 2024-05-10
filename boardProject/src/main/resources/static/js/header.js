let notificationSock;       // 알림 웹소켓 객체
let sendNotificationFn;     // 웹소켓을 이용해 알림을 보내는 함수

let selectnNotificationFn;  // 비동기로 알림을 조회하는 함수
let notReadCheckFn;         // 비동기로 읽지 않은 알림 개수 체크하는 함수

if(notificationLoginCheck){ // 로그인 상태일 경우만 알림 WebSocket 수행
    /* 알림 Websocket */
    
    //**  SockJS **
    //웹 브라우저와 웹 서버 간에 실시간 양방향 통신을 가능하게 하는 자바스크립트 라이브러리입니다.

    // 1. SockJS 라이브러리 추가
    // 2. SockJS를 이용해서 클라이언트용 웹소켓 객체 생성
    notificationSock = new SockJS("/notification/send"); //SockJS 객체 생성 -> 현재 접속한 클라이언트가 요청 자동으로 보냄
    //웹소켓 컨피그
    

    /* 웹소켓을 이용해 알림을 전달하는 함수 */
    sendNotificationFn = (type, url, pkNo) => {

        const notification = { //JS 객체
            "notificationType" : type,
            "notificationUrl": url,
            "pkNo" : pkNo
        }

        notificationSock.send(JSON.stringify(notification)); 
        
        //JSON을 웹소켓으로 보냈다 -> NotificationWebsocketHandler의 message에 JSON이 들어감
    }
    

    /* 웹소켓을 통해 서버에서 전달된 메시지가 있을 경우 */
    notificationSock.addEventListener("message", e => {
        //이 코드를 이용해서 메시지를 받을거다

        // 알람 버튼 활성화 (종 노란색으로 변하게 만드는 구문)
        const notificationBtn = document.querySelector(".notification-btn");
        notificationBtn.classList.remove("fa-regular");
        notificationBtn.classList.add("fa-solid");

        //밑에 있는 함수이다
        //비동기로 알림을 실시간으로 읽어오는 함수
        selectnNotificationFn();
    })

    /* 읽지 않은 알림이 있는지 확인하는 함수 */
    //비동기를 동기로 바꾸는거다
    notReadCheckFn = async () => { //동기식으로 작동(await를 붙였기 때문에)
        //fetch했을 때 Promise객체가 나온다 -> 
        const resp = await fetch("/notification/notReadCheck")
        const notReadCount = await resp.text(); //text로 파싱 다 끝날 때까지 기다려라
        // console.log(notReadCount) // 안읽은 알림 개수
        return notReadCount; // 안읽은 알림 개수가 Promise 객체에 담겨져서 반환
    }

    /* 비동기로 알림을 조회하는 함수  */
    selectnNotificationFn = () => {
        fetch("/notification")
        .then(resp => resp.json())
        .then(selectList => {

            // console.log(notificationList);

            // 이전 알림 목록 삭제
            const notiList = document.querySelector(".notification-list");
            notiList.innerHTML = '';

            for(let data of selectList){
                
                // 알림 전체를 감싸는 요소
                const notiItem = document.createElement("li");
                notiItem.className = 'notification-item';


                // 알림을 읽지 않은 경우 'not-read' 추가
                if(data.notificationCheck == 'N') notiItem.classList.add("not-read");


                // 알림 관련 내용(프로필 이미지 + 시간 + 내용)
                const notiText = document.createElement("div");
                notiText.className = 'notification-text';


                // 알림 클릭 시 동작
                notiText.addEventListener("click", e => {
                    
                    // 만약 읽지 않은 알람인 경우
                    if(data.notificationCheck == 'N') { //읽은 적 없는건데 읽은 경우 읽음으로 바꾸기
                        fetch("/notification", {
                            method: "PUT",
                            headers: { "Content-Type": "application/json" },
                            body : data.notificationNo
                        })

                        // 컨트롤러 메서드 반환값이 없으므로 then 작성 X
                    }

                    // 클릭 시 알림에 기록된 경로로 이동
                    location.href = data.notificationUrl;
                })


                // 알림 보낸 회원 프로필 이미지
                const senderProfile = document.createElement("img");
                if(data.sendMemberProfileImg == null)   senderProfile.src = notificationDefaultImage;  // 기본 이미지
                else                                    senderProfile.src = data.sendMemberProfileImg; // 프로필 이미지
                

                // 알림 내용 영역
                const contentContainer = document.createElement("div");
                contentContainer.className = 'notification-content-container';
                
                // 알림 보내진 시간
                const notiDate = document.createElement("p");
                notiDate.className = 'notification-date';
                notiDate.innerText = data.notificationDate;

                // 알림 내용
                const notiContent = document.createElement("p");
                notiContent.className = 'notification-content';
                notiContent.innerHTML = data.notificationContent; // 태그가 해석 될 수 있도록 innerHTML

                // 삭제 버튼
                const notiDelete = document.createElement("span");
                notiDelete.className = 'notidication-delete';
                notiDelete.innerHTML = '&times;';


                /* 삭제 버튼 클릭 시 비동기로 해당 알림 지움 */
                notiDelete.addEventListener("click", e => {

                    fetch("/notification", {
                        method: "DELETE",
                        headers: { "Content-Type": "application/json" },
                        body : data.notificationNo
                    })
                    .then(resp => resp.text()) 
                    .then(result => {
                        // 클릭된 x버튼이 포함된 알림 삭제
                        notiDelete.parentElement.remove();

                        // 남은 알림 개수를 확인하여
                        notReadCheckFn().then(notReadCount => {//promise await async 비동기
                            //fetch()는 비동기로 요청하는 것 ->Promise 객체가 반환된다
                            //언젠가는 값이 돌아올거라고 약속하는 것임
                            //값이 돌아오면 뒤의 then이 실행됨

                            //안읽은 알림이 있으면 색 칠해두고 
                            //세보고 오는 동안 뒤의 코드 실행 안하고 기다려줬어(동기)
                            const notificationBtn = document.querySelector(".notification-btn");

                            // 있으면 활성화
                            if(notReadCount > 0){
                                notificationBtn.classList.remove("fa-regular");
                                notificationBtn.classList.add("fa-solid");
                            }else{ // 없으면 비활성화
                                notificationBtn.classList.add("fa-regular");
                                notificationBtn.classList.remove("fa-solid");
                            }
                        })
                    })
                })

                // 조립
                notiList.append(notiItem);
                notiItem.append(notiText, notiDelete);
                notiText.append(senderProfile, contentContainer);
                contentContainer.append(notiDate, notiContent);

            }
        })
    }

    /* 
        ajax (fetch() api)는 비동기로 동작하기 때문에
        일반 함수 () => {} 내부에 
        fetch() 성공 시 결과 result 출력,
        fetch() 다음 에 "함수 끝"  출력을 작성하면
        result -> "함수 끝" 순서 로 출력되는 것이 아닌
        "함수 끝" -> result 순서로 
        fetch() 수행 결과를 기다리지 않고 함수 내 다음 코드가 먼저 수행됨
    
        // ------------ 비동기 통신 순서 예시 코드 -------------
        notReadCheckFn = () => { //async, await 적용 안 한 코드
            fetch("/notification/notReadCheck")
            .then(resp => resp.text())
            .then(result => {
                console.log("result",result); // 서버와 비동기 통신이 끝난 후 출력!! 후
            });

            console.log("함수 끝"); // 비동기이면 얘가 먼저 출력! 선
        }

        근데 위에있는 게 먼저 나오길 원하면 동기식으로 하면 된다!!
        하지만 fetch()의 결과를 기다렸다 다음 코드가 수행 할 수 있는 방법이 존재함

        - Promise 객체 
            > fetch() 같은 비동기 요청 후에 값을 가져올거라 약속하는 객체
            > 요청 상태/결과를 then() 메서드를 이용해서 얻어올 수 있음

        - async 함수 (함수 앞에 붙일 수 있고 그럼 이 함수는 더이상 비동기 함수가 아닌 동기식 함수가 된다)
            > 작업결과를 기다려야되는 비동기 코드를 포함한 함수
            > 함수명 앞에 작성
            > 함수 결과로 Promise 객체를 반환

        - await 키워드 (await 안붙이고 함수 호출하면 비동기로 함수가 수행되는데 await를 붙이고 호출하면 동기식으로 함수가 작동한다)
            > 비동기 작업이 완료 될때 까지 코드 수행을 멈추게함
            > async 함수 내부에 비동기 코드(fetch, Promise) 앞에 작성
            > 비동기 작업 결과를 반환함
    */

    /* 페이지 DOM 요소 내용이 모두 로딩된 후(화면 렌더링이 끝난 후) */
    document.addEventListener("DOMContentLoaded", () => {

        // 알람 버튼
        const notificationBtn = document.querySelector(".notification-btn");

        /* 읽지 않은 알림이 있으면 알림 버튼 활성화 하기 */
        notReadCheckFn().then(notReadCount => {
            if(notReadCount > 0){
                notificationBtn.classList.remove("fa-regular");
                notificationBtn.classList.add("fa-solid");
            }
        })

        
        /* 알림 버튼(종) 클릭 시*/
        notificationBtn.addEventListener("click", e => {
            const notiList = document.querySelector(".notification-list");

            // 보이는 상태일 때
            if(notiList.classList.contains("notification-show")){
                notiList.classList.remove("notification-show");
                return;
            }

            /* 로그인 상태인 경우 알림 목록을 바로 비동기로 조회 */
            selectnNotificationFn();
            notiList.classList.add("notification-show");
        })

    })   

}

/* 페이지 DOM 요소 내용이 모두 로딩된 후(화면 렌더링이 끝난 후) */
document.addEventListener("DOMContentLoaded", () => { //문서 전체(브라우저 화면 전체)에 대해서 이벤트 추가
    //클릭이든 change든 모든 이벤트 줄 수 있다
    //화면의 요소 내용들이 load됐을 때 ==html 코드 다 해석됐을 때 그 떄 이걸 실행하겠다

    // 주소에 #아이디속성명 이 작성되어 있으면서
    // 해당 아이디를 가진 요소가 존재하는 경우
    // 해당 요소의 위치로 스크롤 옮기기
    // const targetId = location.href.substring(location.href.indexOf("#") + 1);
    
    // 쿼리스트링 파라미터 중 cn 값을 얻어와 같은 아이디를 가지는 요소로 이동
    const params = new URLSearchParams(location.search) //URLSearchParams
    //location.search == ?cn=100&key=t 이거 중에서
    const targetId = "c"+params.get("cn"); // 100 만 얻어옴
    //comment.html에서 c도 더해서 아이디 주기로 했어서

    let targetElement = document.getElementById(targetId);

    if (targetElement) {
        const scrollPosition = targetElement.offsetTop; 
        //targetElement.offsetTop : 스크롤이 위에서부터 몇 픽셀 내려왔는지 계산해주는 요소
        //만약 위에서부터 900픽셀 떨어져있으면 900이라는 숫자가 들어감
        window.scrollTo({ //그 자리까지 스크롤내리기
            top: scrollPosition - 200, 
            //-200안쓰면 스크롤 하면 요소가 위에 딱 붙어버려서 위에서부터 200픽셀 떨어지도록
            behavior: 'smooth' //스무스하게 천천히 화면이 움직임
        });
    }
})

