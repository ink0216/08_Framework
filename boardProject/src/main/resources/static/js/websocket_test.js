//메인페이지 켜지자마자 웹소켓 실행되게 하기
/*웹소켓 테스트 */

//네 가지를 작성할거다
// 1. SockJS 라이브러리 추가
//      ->common.html에 작성할거다(알림 기능을 모든 페이지 어디서든지 사용 가능하게 하려고)

// 2. SockJS 객체 생성
const testSock = new SockJS("/testSock"); 
//  이렇게 객체 생성하면 발생하는 효과 : 
//          -객체 생성 시 자동으로 websocket://localhost(또는 ip)/testSock으로 연결 요청이 보내진다
//          아까 서버쪽에 WebsocketConfig에서 addHandler에 똑같은 주소 써놨었다
//              그 요청을 testWebsocketHandler가 반응해서 세션 가로챌거고 SockJS와 호환하게 만들거다 라고
//                  백엔드에 다 돼있다

// 3. 생성된 SockJS 객체를 이용해서 메시지를 전달하기
const sendMessageFn = (name,str)=>{ //문자열 하나가 아닌 다량의 데이터 보내고 싶으면?
    //두 개의 데이터를 한 번에 보내고 싶으면 JSON을 이용해야 한다
    //JSON을 이용해서 다량의 데이터를 TEXT(문자열) 형태로 변경하여 한 번에 전달
    const obj = {
        "name" : name,
        "str" : str
    }; //JS 객체

    testSock.send(JSON.stringify(obj)); 
    //연결된 웹소켓 핸들러에게 js객체를 JSON으로 문자열화 해서 전달
    // -> 콘솔에 JS객체 모양으로 찍히게 된다
};

// 4. 서버로부터 현재 클라이언트에게 웹소켓을 이용한 메시지가 전달된 경우 ->이벤트이다
testSock.addEventListener("message", e=>{
    //message라는 이벤트

    //e.data : 서버로부터 전달 받은 message가 이 안에 들어있다
    //console.log(e.data); //e.data 하면 JSON으로 보냈으니까 코드에서 백엔드에서 문자열 그대로 보내서
    //문자열 형태로 나올거다

    const msg = JSON.parse(e.data); //JSON을 JS Object로 바꿔주는 구문
    console.log(`${msg.name} : ${msg.str}`); //누가 어떤 메시지 보냈는 지 채팅 모양으로 만들 수 있다
});



