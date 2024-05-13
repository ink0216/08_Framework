/* 1대1 채팅하려면 뭐가 필요할까?
    - 실시간으로 채팅(WebSocket: 전이중통신(전화 생각하면 된다!!))
        (새로고침 안 해도 타인이 채팅 보내면 자동으로 알림이 뜸)
        A한테 통화 하고싶어 하면 기지국이 유저 목록에서 A 찾아서 A와 연결해줌
        웹소켓의 경우, 기지국 역할을 서버가 한다
        http://는 html을 이용하는 것이고
        웹소켓은 websocket:// (웹소켓 프로토콜)을 사용한다!!!(SockJS가 자동으로 만들어줌)
    - 나랑 상대를 구분하는 방법은?
        (Session에서 loginMember 얻어와서 회원 번호를 이용하면 된다!!!->그걸로 구분)
*/
//전역변수 선언
const addTarget = document.querySelector("#addTarget"); // 추가 버튼
const addTargetPopupLayer = document.querySelector("#addTargetPopupLayer"); // 팝업 레이어
const closeBtn = document.querySelector("#closeBtn"); // 닫기 버튼
const targetInput = document.querySelector("#targetInput"); // 사용자 검색
const resultArea = document.querySelector("#resultArea"); // 검색 결과


let selectChattingNo; // 선택한 채팅방 번호
let selectTargetNo; // 현재 채팅 대상
let selectTargetName; // 대상의 이름
let selectTargetProfile; // 대상의 프로필


//------------------------------------------------------------------------------------------------------------------------------
let chattingSock; //채팅에 사용될 SockJS 객체를 저장할 변수
if(notificationLoginCheck){ //로그인 되어있는 경우임
    //로그인 되어있으면 true, 안되어있으면 false가 됨
    chattingSock = new SockJS("/chattingSock"); // /chattingSock 주소를 처리하는 WebSocketHandler에 연결하는 구문
    //로그인이 돼있을 떄에만 해당 주소를 처리하는 웹소켓 핸들러와 연결해줄거다
    //로그인이 돼있을 떄만 SockJS 객체로 chattingSock 변수가 초기화가 된다
    
    //해당 주소로 연결 요청을 보낸다
}
/* 채팅 메시지를 보내는 함수 */
const sendMessage = ()=>{ 
    //채팅 입력하는 textarea요소 얻어와서 그 요소의 내용을 이용해서 메시지 보내기
    const inputChatting = document.querySelector("#inputChatting");

    const msg = inputChatting.value.trim(); //입력된 채팅 메시지
    
    if(!notificationLoginCheck){
        //로그인이 되어있지 않으면 함수 종료시키기
        return;
    }
    if(msg.length ===0){
        //채팅 미입력 시
        alert("채팅을 입력해 주세요.");
        return;
    }
    //채팅 입력하면 메시지 보내는 코드

    //접속한 전체 회원이 아닌 특정 회원에게만 메시지 보내는 코드
    const chattingObj = {
        //웹소켓 핸들러로 전달할 채팅 관련 데이터를 담은 JS 객체 생성
        "targetNo" : selectTargetNo, //누구한테 보낼 채팅인지 메시지를 받을 대상의 회원 번호(웹소켓 할 때 쓸거다(누구한테 메시지 전달할거다))
        "messageContent" : msg, // 전달할 메시지 내용
        "chattingNo" : selectChattingNo //채팅방 번호(DB에 저장할 용도) -> DB에 채팅방 번호 컬럼 있다
    };
    //웹소켓 핸들러는 자바여서
    //JSON으로 변환해서 웹소켓 핸들러로 전달해줘야 한다
    // ChattingWebsocketHandler의 handleTextMessage로 전달돼서 온다
    chattingSock.send(JSON.stringify(chattingObj)); //매개변수로 받은 메세지를 그대로 전달하겠다
    inputChatting.value=""; //보냈으면 보낸 채팅 내용 다 삭제(채팅 입력창 빈칸으로 만들기)
};
//------------------------------------------------------------------------------------------------------------------------------
/* 연결된 웹소켓 객체를 통해 서버로부터 메시지를 전달 받은 경우 */
if(chattingSock !=undefined){
    //로그인 되면 chattingSock 객체가 만들어진다
    chattingSock.addEventListener("message", e=>{
        console.log(e.data); //전달된 데이터 잘 들어오는지 확인


    // 메소드를 통해 전달받은 JSON을 JS Object로 변환해서 msg 변수에 저장.
	const msg = JSON.parse(e.data);
	console.log(msg);


	// 현재 채팅방을 보고있는 경우
	if(selectChattingNo == msg.chattingNo){

        //화면 만드는 코드
		const ul = document.querySelector(".display-chatting");
	
		// 메세지 만들어서 출력하기
		//<li>,  <li class="my-chat">
		const li = document.createElement("li");
	
		// 보낸 시간
		const span = document.createElement("span");
		span.classList.add("chatDate");
		span.innerText = msg.sendTime;
	
		// 메세지 내용
		const p = document.createElement("p");
		p.classList.add("chat");
		p.innerHTML = msg.messageContent; // br태그 해석을 위해 innerHTML
	
		// 내가 작성한 메세지인 경우
		if(loginMemberNo == msg.senderNo){ 
			li.classList.add("my-chat");
			
			li.append(span, p);
			
		}else{ // 상대가 작성한 메세지인 경우
			li.classList.add("target-chat");
	
			// 상대 프로필
			const img = document.createElement("img");
			img.setAttribute("src", selectTargetProfile);
			
			const div = document.createElement("div");
	
			// 상대 이름
			const b = document.createElement("b");
			b.innerText = selectTargetName; // 전역변수
	
			const br = document.createElement("br");
	
			div.append(b, br, p, span);
			li.append(img,div);
	
		}
	
		ul.append(li)
		display.scrollTop = display.scrollHeight; // 스크롤 제일 밑으로
	}
	selectRoomList(); //비동기로 채팅방 목록을 조회해서 화면 만드는 함수
    });
}
//------------------------------------------------------------------------------------------------------------------------
function selectRoomList(){

	fetch("/chatting/roomList")
	.then(resp => resp.json())
	.then(roomList => {
		console.log(roomList);

		// 채팅방 목록 출력 영역 선택
		const chattingList = document.querySelector(".chatting-list");

		// 채팅방 목록 지우기
		chattingList.innerHTML = "";

		// 조회한 채팅방 목록을 화면에 추가
		for(let room of roomList){
			const li = document.createElement("li");
			li.classList.add("chatting-item");
			li.setAttribute("chat-no", room.chattingNo);
			li.setAttribute("target-no", room.targetNo);

			if(room.chattingNo == selectChattingNo){
				li.classList.add("select");
			}

			// item-header 부분
			const itemHeader = document.createElement("div");
			itemHeader.classList.add("item-header");

			const listProfile = document.createElement("img");
			listProfile.classList.add("list-profile");

			if(room.targetProfile == undefined)	
				listProfile.setAttribute("src", userDefaultImage);
			else								
				listProfile.setAttribute("src", room.targetProfile);

			itemHeader.append(listProfile);

			// item-body 부분
			const itemBody = document.createElement("div");
			itemBody.classList.add("item-body");

			const p = document.createElement("p");

			const targetName = document.createElement("span");
			targetName.classList.add("target-name");
			targetName.innerText = room.targetNickname;
			
			const recentSendTime = document.createElement("span");
			recentSendTime.classList.add("recent-send-time");
			recentSendTime.innerText = room.sendTime;
			
			
			p.append(targetName, recentSendTime);
			
			
			const div = document.createElement("div");
			
			const recentMessage = document.createElement("p");
			recentMessage.classList.add("recent-message");

			if(room.lastMessage != undefined){
				recentMessage.innerHTML = room.lastMessage;
			}
			
			div.append(recentMessage);

			itemBody.append(p,div);

			// 현재 채팅방을 보고있는게 아니고 읽지 않은 개수가 0개 이상인 경우 -> 읽지 않은 메세지 개수 출력
			if(room.notReadCount > 0 && room.chattingNo != selectChattingNo ){
				const notReadCount = document.createElement("p");
				notReadCount.classList.add("not-read-count");
				notReadCount.innerText = room.notReadCount;
				div.append(notReadCount);
			}else{

				// 현재 채팅방을 보고있는 경우
				// 비동기로 해당 채팅방 글을 읽음으로 표시
				fetch("/chatting/updateReadFlag",{
					method : "PUT",
					headers : {"Content-Type": "application/json"},
					body : JSON.stringify({"chattingNo" : selectChattingNo})
				})
				.then(resp => resp.text())
				.then(result => console.log(result))
				.catch(err => console.log(err));

			}
			

			li.append(itemHeader, itemBody);
			chattingList.append(li);
		}

		roomListAddEvent(); //새로 만들어진 요소들은 이벤트가 없어서 이벤트 추가하는 함수
	})
	.catch(err => console.log(err));
}
//----------------------------------------------------------------------------------------------------------------------

// 채팅 메세지 영역
const display = document.getElementsByClassName("display-chatting")[0];

// 채팅방 목록에 이벤트를 추가하는 함수 
function roomListAddEvent(){
	const chattingItemList = document.getElementsByClassName("chatting-item");
	
	for(let item of chattingItemList){
		item.addEventListener("click", e => {
	
			// 전역변수에 채팅방 번호, 상대 번호, 상태 프로필, 상대 이름 저장
			selectChattingNo = item.getAttribute("chat-no");
			selectTargetNo = item.getAttribute("target-no");

			selectTargetProfile = item.children[0].children[0].getAttribute("src");
			selectTargetName = item.children[1].children[0].children[0].innerText;

			if(item.children[1].children[1].children[1] != undefined){
				item.children[1].children[1].children[1].remove();
			}
	
			// 모든 채팅방에서 select 클래스를 제거
			for(let it of chattingItemList) it.classList.remove("select")
	
			// 현재 클릭한 채팅방에 select 클래스 추가
			item.classList.add("select");
	
			// 비동기로 특정 채팅방 내의 메세지 목록을 조회하는 함수 호출
			selectChattingFn();
		});
	}
}
//------------------------------------------------------------------------------------------------------------------------------
// 비동기로 메세지 목록을 조회하는 함수
function selectChattingFn() {

	fetch(`/chatting/selectMessage?chattingNo=${selectChattingNo}`)
	.then(resp => resp.json())
	.then(messageList => {
		console.log(messageList);

		// <ul class="display-chatting">
		const ul = document.querySelector(".display-chatting");

		ul.innerHTML = ""; // 이전 내용 지우기

		// 메세지 만들어서 출력하기
		for(let msg of messageList){
			//<li>,  <li class="my-chat">
			const li = document.createElement("li");

			// 보낸 시간
			const span = document.createElement("span");
			span.classList.add("chatDate");
			span.innerText = msg.sendTime;

			// 메세지 내용
			const p = document.createElement("p");
			p.classList.add("chat");
			p.innerHTML = msg.messageContent; // br태그 해석을 위해 innerHTML

			// 내가 작성한 메세지인 경우
			if(loginMemberNo == msg.senderNo){ 
				li.classList.add("my-chat");
				
				li.append(span, p);
				
			}else{ // 상대가 작성한 메세지인 경우
				li.classList.add("target-chat");

				// 상대 프로필
				const img = document.createElement("img");
				img.setAttribute("src", selectTargetProfile);
				
				const div = document.createElement("div");

				// 상대 이름
				const b = document.createElement("b");
				b.innerText = selectTargetName; // 전역변수

				const br = document.createElement("br");

				div.append(b, br, p, span);
				li.append(img,div);

			}

			ul.append(li);
			display.scrollTop = display.scrollHeight; // 스크롤 제일 밑으로
		}

	})
	.catch(err => console.log(err));
}
//-----------------------------------------------------------------------------------------------------------------------------
/*문서 로딩 완료 후 수행할 것 */
document.addEventListener("DOMContentLoaded", //화면에 DOM 내용이 다 로드되었을 때
        ()=>{
            //채팅방 목록에 클릭 이벤트 추가하는 함수 호출
            roomListAddEvent();

            //보내기 버튼 클릭 시 메시지 보내기
            document.querySelector("#send").addEventListener("click", sendMessage);
            //클릭하면 sendMessage 함수 실행된다

            //채팅 입력 후 엔터 입력 시 메시지 보내기
            document.querySelector("#inputChatting").addEventListener("keyup", //key가 올라올 때
                e=>{
                    if(e.key == "Enter"){ //입력한 키가 Enter인 경우
                        //입력한 키를 인식 가능
                        //어떤 키가 올라왔는 지
                        if(!e.shiftKey){
                            //shift 키가 눌러지지 않은 경우
                            //채팅을 보통 여러 줄로 작성 가능
                            //enter 누르면 메시지가 보내져버려서
                            //shift+enter하면 줄바꿈 되도록 함
                            sendMessage(); //엔터만 눌렀을 경우에만 제출시키겠다
                        }
                    }
                })
        })

// -----------------------------------------------------------


// 검색 팝업 레이어 열기
addTarget.addEventListener("click", e => {
	addTargetPopupLayer.classList.toggle("popup-layer-close");
	targetInput.focus();
});

// 검색 팝업 레이어  닫기
closeBtn.addEventListener("click", e => {
	addTargetPopupLayer.classList.toggle("popup-layer-close");
	resultArea.innerHTML = ""; //다시 열었을 때 기존에 있던 내용 안보이게 하려고
});


// 사용자 검색(ajax)
targetInput.addEventListener("input", e => { //입력 방법에 제한 두지 않고 뭔가 입력됐을 때(복붙,키보드,마우스 등등 가리지 않음)

	const query = e.target.value.trim();

	// 입력된게 없을 때
	if(query.length == 0){
		resultArea.innerHTML = ""; // 이전 검색 결과 비우기
		return;
	}


	// 입력된게 있을 때
	if(query.length > 0){
		fetch("/chatting/selectTarget?query="+query)
		.then(resp => resp.json())
		.then(list => {
			//console.log(list);

			resultArea.innerHTML = ""; // 이전 검색 결과 비우기

			if(list.length == 0){
				const li = document.createElement("li");
				li.classList.add("result-row");
				li.innerText = "일치하는 회원이 없습니다";
				resultArea.append(li);
			}

			for(let member of list){
				// li요소 생성(한 행을 감싸는 요소)
				const li = document.createElement("li");
				li.classList.add("result-row");
				li.setAttribute("data-id", member.memberNo);

				// 프로필 이미지 요소
				const img = document.createElement("img");
				img.classList.add("result-row-img");
				
				// 프로필 이미지 여부에 따른 src 속성 선택
				if(member.profileImage == null) img.setAttribute("src", userDefaultImage);
				else	img.setAttribute("src", member.profileImage);

				let nickname = member.memberNickname;
				let email = member.memberEmail;

				const span = document.createElement("span");
				span.innerHTML = `${nickname} ${email}`.replace(query, `<mark>${query}</mark>`);
				//노란 색 형광펜 == mark 태그로 감싼거다
				// query를 mark태그로 감싼 것으로 대체

				// 요소 조립(화면에 추가)
				li.append(img, span);
				resultArea.append(li);

				// li요소에 클릭 시 채팅방에 입장하는 이벤트 추가
				li.addEventListener('click', chattingEnter); //클릭하면 채팅방에 입장하게 만들기
			}

		})
		.catch(err => console.log(err) );
	}
});
//--------------------------------------------------------------------------------------------------------------------
// 채팅방 입장 또는 선택 함수
function chattingEnter(e){ //채팅방에 들어가서 비동기로 채팅방 번호 얻어와서 그 안의 채팅 조회하는 함수
	console.log(e.target); // 실제 클릭된 요소
	console.log(e.currentTarget); // 이벤트 리스트가 설정된 요소

	const targetNo = e.currentTarget.getAttribute("data-id");

	fetch("/chatting/enter?targetNo="+targetNo)
	.then(resp => resp.text())
	.then(chattingNo => {
		console.log(chattingNo);
		
		selectRoomList(); // 채팅방 목록 조회
		
		setTimeout(()=>{  //setTimeout : 200ms만큼 기다리고 나서 실행해라(채팅방 목록 만드는 데에 시간 걸려서)
			// 만약 채팅방 목록 중 이미 존재하는 채팅방이 있으면 클릭해서 입장
			const itemList = document.querySelectorAll(".chatting-item")
			for(let item of itemList) {		
				if(item.getAttribute("chat-no") == chattingNo){
					item.focus();
					item.click();
					addTargetPopupLayer.classList.toggle("popup-layer-close");
					targetInput.value = "";
					resultArea.innerHTML = "";
					return;
				}
			}

		}, 200);

	})
	.catch(err => console.log(err));
}



