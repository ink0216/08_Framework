/* ajax코드 쓰면 된다 */
/* 서비스키(인증키) 준비하기 연습이니까 스크립트에 작성하지만 원래는 노출되면 안돼서 db나 외부 파일에 넣어놓고 꺼내오는 게 좋다!!! */
const serviceKey = "2Kd4kpeZc9Ej66agEoP/4+s4nKOzfqtZA94rcXJ1IYkKAEIrWi6favIhgwJvZMFMh8YAXSLrGA3u3v/ARI7s3g==";

const numOfRows = 1000; //조회할 데이터 개수 
const pageNo = 1; //조회할 페이지 (기상청은 대부분 한 페이지에 들어간다)
const dataType = "JSON"; //응답 데이터 타입 (JSON / XML)

// 화면 요소 얻어오기
const region = document.getElementById("region");
const regionName = document.querySelector("#regionName");
const currentWeather = document.querySelector(".current-weather");

/* ******************** 초단기 예보 비동기 요청할 함수 정의하기 ******************** */
const getUltraSrtFcst = (regionValue //지역명 (서울,부산,제주 이런것)

)=>{
    //초단기 예보를 기상청에서 이 용어로 부른다

    const curl = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtFcst"; 
    //데이터를 요청해서 응답 받을 주소 (Callback URL) 

    //예보 지점 X/Y 좌표 (서울 기준 수치)
    //const nx = 60;
    //const ny = 127;
    const nx = coordinateList[regionValue].nx; //각 지역의 x좌표
    const ny = coordinateList[regionValue].ny; //각 지역의 y좌표

    const base = getBase(); //base_time, base_date를 얻어오는 함수

    /*http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtFcst
    ?serviceKey=2Kd4kpeZc9Ej66agEoP%2F4%2Bs4nKOzfqtZA94rcXJ1IYkKAEIrWi6favIhgwJvZMFMh8YAXSLrGA3u3v%2FARI7s3g%3D%3D&numOfRows=1000
    &pageNo=1&dataType=JSON&base_date=20240520&base_time=1430&nx=60&ny=127 
    이거 만들거다*/

    //쿼리스트링을 관리하는 객체 생성
    const searchParams = new URLSearchParams(); //이 객체를 이용하면 쿼리스트링을 마음대로 넣었다 뺐따 할 수 있다

    //쿼리스트링에 key = value 추가
    searchParams.append("serviceKey", serviceKey);
    searchParams.append("numOfRows", numOfRows);
    searchParams.append("pageNo", pageNo);
    searchParams.append("dataType", dataType);
    searchParams.append("base_date", base.baseDate); //과거와 현재 날짜만 가능하다
    searchParams.append("base_time", base.baseTime); //현재 시간에 맞게 계산된 date와 time이 자동으로 들어간다
    searchParams.append("nx", nx);
    searchParams.append("ny", ny);

    /* ajax코드 (fetch api) 작성하기 */
    fetch(`${curl}?${searchParams.toString()}`) //get방식으로 요청 보내기
    //toString()하면 쿼리스트링 모양으로 만들어진다 -> 
    /*http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtFcst
    ?serviceKey=2Kd4kpeZc9Ej66agEoP%2F4%2Bs4nKOzfqtZA94rcXJ1IYkKAEIrWi6favIhgwJvZMFMh8YAXSLrGA3u3v%2FARI7s3g%3D%3D&numOfRows=1000
    &pageNo=1&dataType=JSON&base_date=20240520&base_time=1430&nx=60&ny=127 
    이게 만들어진다!!!*/
    .then(resp => resp.json()) //응답 받은 JSON을 JS Object로 변환하는 첫 번째 then
    .then(result =>{
        //파싱된 데이터가 result로 들어온다
        console.log(result);
        //가공
    
        // [위] : 공공 데이터 Open API를 이용한 데이터 요청 및 응답
    //------------------------------------------------------------------------------------------------
    // [아래] : 개발자가 응답 받은 데이터를 활용

    // 요청 데이처 중 item (객체 배열)
    const list = result.response.body.items.item;

    //
    const weatherObj = {}; //현재 시간으로부터 가장 가까운 예보를 저장할 빈 객체 생성

    //예보 날짜/시간을 weatherObj에 추가
    //조회한 데이터의 0번 인덱스는 항상 가장 가까운 시간을 나타냄
    weatherObj.fcstDate = list[0].fcstDate; 
    weatherObj.fcstTime = list[0].fcstTime;
    
    for(let item of list){
        if(item.fcstDate == weatherObj.fcstDate && 
            item.fcstTime == weatherObj.fcstTime){
                //많은 데이터 중에서 현재 시간의 데이터만 모아두겠다
                // 10개 데이터를 weatherObj안에 모아둔다
                // fcstDate, fcstTime이 모두 일치하는 정보만 weatherObj에 저장하기
                // -> 가장 가까운 시간대의 예보만 저장 (바로 앞의 1시간만 필요한 경우임)
            weatherObj[item.category] = item.fcstValue;
        }
    }

    console.log(weatherObj);

    currentWeather.innerHTML = "";

    const h1 = document.createElement("h1");
    h1.innerText = weatherObj["T1H"] + "℃";

    const p1 = document.createElement("p");
    p1.innerText = getSkyState(weatherObj["SKY"] );

    const p2 = document.createElement("p");
    p2.innerText = "습도(%) :" + weatherObj["REH"];

	  // 강수 형태
    const p3 = document.createElement("p");
    p3.innerText = "강수 형태 : " +getUltraSrtPtyState(weatherObj["PTY"]);

    const p4 = document.createElement("p");
    p4.innerText = "강수 확률(%) : " + weatherObj["RN1"];

    currentWeather.append(h1, p1, p2, p3, p4);





  });
};










// 지역별 좌표(기상청 api excel 파일 참고)
const coordinateList = { //객체이다
    "서울" : {"nx":60, "ny":127},
    "경기" : {"nx":60, "ny":120},
    "인천" : {"nx":55, "ny":124},
    "제주" : {"nx":52, "ny":38},
    "세종" : {"nx":66, "ny":103},
    "광주" : {"nx":58, "ny":74},
    "대구" : {"nx":89, "ny":90},
    "대전" : {"nx":67, "ny":100},
    "부산" : {"nx":98, "ny":76},
    "울산" : {"nx":102, "ny":84},
    "강원" : {"nx":73, "ny":134},
    "경북" : {"nx":89, "ny":91},
    "경남" : {"nx":91, "ny":77},
    "전북" : {"nx":63, "ny":89},
    "전남" : {"nx":51, "ny":67},
    "충북" : {"nx":69, "ny":107},
    "충남" : {"nx":68, "ny":100}
}

//현재 시간을 알아서 계산하게 만들기
//알아서 계산하는 함수

/* base_date, base_time 만드는 함수 */
const getBase = () => {
    
    const base = {};
    const now = new Date();

    // base_date(오늘 날짜 YYYYMMDD 형식) 계산
    const year = now.getFullYear();

    let month = now.getMonth() + 1;
    month = month < 10 ? "0" + month : month;

    let date = now.getDate();
    date = date < 10 ? "0" + date : date;

    base.baseDate = `${year}${month}${date}`;


    // 45분 이하인 경우 1시간 전을 baseTime으로 지정
    const hour = now.getMinutes() <= 45 ? now.getHours()-1 : now.getHours()

    if(hour < 10)    base.baseTime =  "0" + hour + "00";
    else             base.baseTime =  hour + "00";
    return base;
    
    
}



// 하늘 상태(SKY) 코드 변환
const getSkyState = (code) => {
    code = Number(code);
    switch(code){
    case 1: return "맑음";
    case 3: return "구름많음";
    case 4: return "흐림";
    }
}

  // 초단기 강수 형태(PTY) 코드 변환
const getUltraSrtPtyState = (code) => {
    code = Number(code);
    switch(code){
        case 0: return "없음";
        case 1: return "비";
        case 2: return "비/눈";
        case 3: return "눈";
        case 5: return "빗방울";
        case 6: return "빗방울눈날림";
        case 7: return "눈날림";
    }
}

/* select 변경 시 해당 지역 날씨를 조회하기 */
region.addEventListener("change", e=>{ //값이 바뀌는 것을 감지
    regionName.innerText = e.target.value; //화면에 보이는 지역명 변경
    getUltraSrtFcst(e.target.value); //해당 지역 날씨 조회하는 open API 호출

});

/* 화면 로딩이 종료된 후 "서울" 지역 날씨 조회하는 코드 */
document.addEventListener("DOMContentLoaded" , ()=>{
    getUltraSrtFcst("서울");
} //화면에 이벤트 줌 DOM 내용 로드가 끝낫을 떄

)




















