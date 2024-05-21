package edu.kh.api.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class ApiController {
	/**Ajax로 Open API 요청하는 페이지로 포워드
	 * @return
	 */
	@GetMapping("ex1") 
	public String ex1() {
		return "ex1"; //templates폴더의 ex1.html로 포워드
	}
	
	/**Java에서 Open API 요청 후 포워드
	 * @return
	 * @throws IOException 
	 * @throws JSONException 
	 */
	@GetMapping("ex2")
	public String ex2(
			@RequestParam(value="regionName", required=false, defaultValue="서울") String regionName,
			Model model //Spring에서 값 전달용 객체
			//요청 시 전달된 파라미터를 꺼내서 쓰는 것
			//요청 시 없었다면 기본값을 서울로 하겠다
			) throws IOException, JSONException {
		String serviceKey = "2Kd4kpeZc9Ej66agEoP%2F4%2Bs4nKOzfqtZA94rcXJ1IYkKAEIrWi6favIhgwJvZMFMh8YAXSLrGA3u3v%2FARI7s3g%3D%3D";
		//					"2Kd4kpeZc9Ej66agEoP/4+s4nKOzfqtZA94rcXJ1IYkKAEIrWi6favIhgwJvZMFMh8YAXSLrGA3u3v/ARI7s3g=="
		int numOfRows = 1000; //조회할 데이터 개수 
		int pageNo = 1; //조회할 페이지 (기상청은 대부분 한 페이지에 들어간다)
		String dataType = "JSON"; //응답 데이터 타입 (JSON / XML)
		
		

		// 지역 좌표가 저장된 map
		final Map<String, String> coordinateList = new HashMap<>();
		
		coordinateList.put("서울", "60/127");
		coordinateList.put("경기", "60/120");
		coordinateList.put("인천", "55/38");
		coordinateList.put("제주", "52/38");
		coordinateList.put("세종", "66/103");
		coordinateList.put("광주", "58/74");
		coordinateList.put("대구", "89/90");
		coordinateList.put("대전", "67/100");
		coordinateList.put("부산", "98/76");
		coordinateList.put("울산", "102/84");
		coordinateList.put("강원", "73/134");
		coordinateList.put("경북", "89/91");
		coordinateList.put("경남", "91/77");
		coordinateList.put("전북", "63/89");
		coordinateList.put("전남", "51/67");
		coordinateList.put("충북", "69/107");
		coordinateList.put("충남", "68/100");
		
		
		
		// 현재 시간 얻어와 baseDate, baseTime가공
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd/HH/mm"); //시간 형태로 만들어주는 클래스
		String[] arr = (sdf.format(new Date())).split("/");
		
		String baseDate = arr[0];
		
		int hour = Integer.parseInt(arr[1]);
		int minute  = Integer.parseInt(arr[2]);
		
		// 매시간 30분에 생성되고 10분마다 최신 정보로 업데이트(기온, 습도, 바람)
		String baseTime = "";
		if(minute <= 45) { 
			if(hour == 0) hour = 24;
			
			baseTime = String.format("%02d30", hour-1);
		}else {
			
			baseTime = String.format("%02d30", hour );
		}
		
		String[] coordinate = (coordinateList.get(regionName)).split("/");
		String nx = coordinate[0];
		String ny = coordinate[1];
		
		
		
		
		
		
		
		//공공데이터 포털 샘플 코드 활용해서 데이터 요청하기
		 StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtFcst"); /*URL*/
		 // StringBuilder : String의 단점(불변성->String의 값이 변할 때마다 메모리를 잡아먹는 문제)을 해결한 객체
		 // 요청 주소 뒤에 쿼리스트링을 엄청 붙여야 하는데 String으로 붙이면 메모리 소모가 커서 StringBUilder로 붙임!
		 
		 // URLEncoder : 우리가 html 요청 보내면
	        urlBuilder.append("?"  //urlBuilder에 요청 주소 들어있으니까 ?부터 쿼리스트링!
		 + URLEncoder.encode("serviceKey","UTF-8" //UTF-8로 인코딩을 해서 서비스키에 넣을거다
	        		) + "="+serviceKey); /*Service Key*/
	        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode(String.valueOf(pageNo)
	        		// String.valueOf() == 소괄호 안의 것을 String으로 변환
	        		, "UTF-8")); /*페이지번호*/
	        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode(String.valueOf(numOfRows), "UTF-8")); /*한 페이지 결과 수*/
	        urlBuilder.append("&" + URLEncoder.encode("dataType","UTF-8") + "=" + URLEncoder.encode(dataType, "UTF-8")); /*요청자료형식(XML/JSON) Default: XML*/
	        urlBuilder.append("&" + URLEncoder.encode("base_date","UTF-8") + "=" + URLEncoder.encode(baseDate, "UTF-8")); /*‘21년 6월 28일 발표*/
	        urlBuilder.append("&" + URLEncoder.encode("base_time","UTF-8") + "=" + URLEncoder.encode(baseTime, "UTF-8")); /*06시 발표(정시단위) */
	        urlBuilder.append("&" + URLEncoder.encode("nx","UTF-8") + "=" + URLEncoder.encode(nx, "UTF-8")); /*예보지점의 X 좌표값*/
	        urlBuilder.append("&" + URLEncoder.encode("ny","UTF-8") + "=" + URLEncoder.encode(ny, "UTF-8")); /*예보지점의 Y 좌표값*/
	        // urlBuilder에 주소랑 쿼리스트링이 쌓여있다
	        
	        //위에서 조합한 요청주소 + 쿼리스트링 문자열로
	        //요청을 보낼 수 있는 형태의 객체로 변환
	        URL url = new URL(urlBuilder.toString());
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection(); // 인터넷으로 연결하겠다
	        conn.setRequestMethod("GET"); //요청 메서드를 GET으로 지정
	        conn.setRequestProperty("Content-type", "application/json"); 
	        System.out.println("Response code: " //응답 상태 코드 (200==응답 성공, 404==페이지 찾을 수 없음)
	        + conn.getResponseCode()); //요청을 했을 때 응답 상태 코드를 받을 수 있다
	        BufferedReader rd; //입력(read) 보조스트림 / 출력 == write
	        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
	        	//응답 상태 코드가 200~300인 경우(요청 성공해서 응답을 잘 받은 경우)
	            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	            // InputStreamReader : 바이트 기반 입력 스트림을 문자 기반으로 변경해주는 애
	            // 원래 InputStream은 바이트 기반인데 그걸 Reader(문자 기반)으로 바꾸겠다
	            // 문자 기반으로 바꿔서 버퍼드 리더에 넣는다
	            
	            // -> 문자 형태로 빨리 입력 받기 위한 준비를 함(응답 데이터 빠르게 받기)
	        } else {
	            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
	        }
	        
	        //StringBuilder로 읽어들일거다
	        StringBuilder sb = new StringBuilder();
	        String line;
	        //요청 받은 긴 데이터를 한 줄 씩 계속 읽어들여서 sb에 추가
	        // 더이상 읽을 내용이 없으면 반복문을 종료하라는 구문
	        while ((line = rd.readLine())  //한 줄을 읽어와서 line에 저장했는데 null이 아니면
	        		!= null) {
	            sb.append(line); //추가하겠다
	        }
	        rd.close(); //다 읽었으면 다 쓴 스트림 자원 반환하기
	        conn.disconnect(); // 외부 요청 연결도 반환해서 연결 끊기
	        System.out.println(sb.toString()); //전체 결과 출력
	        //여기까지만 내일 시험 범위이다!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	        //내일은 요청 주소가 다를거다!!!!!!!!!!!!!!!!!!!!!!(기상청 아니고 다른 것)
	        //------------------------------------------------------------------------------------------
	        //JSONObject : Spring에서 제공하는 JSON 다루기 객체
	        //Jackson : DTO로 변환할 때 사용하는 것
	        JSONObject j1 = new JSONObject(sb.toString()); 
	        //response에는 headr와 body가 있다
	        
	        System.out.println(j1.getString("response")); //response꺼내오기(String타입으로 꺼내와짐)
	        
	        // 한 겹씩 벗겨내기
	        
	        //j1에서 얻어온 response 데이터를 JSONObject로 변환 (j1에서 response만 꺼내서 JSONObject로 바꾸기)
	        JSONObject j2 = new JSONObject(j1.getString("response")); 
	        
	        //j2에 header, body가 있는데 그 중 body만 꺼내겠다
	        JSONObject j3 = new JSONObject(j2.getString("body"));
	        
	        //body에는 dataType, items가 있는데 item만 꺼내기
	        JSONObject j4 = new JSONObject(j3.getString("items"));
	        
	        String item = j4.getString("item"); //JSON(문자열)으로 얻어오기
	        //이제 item에 [] 배열에 60개의 데이터가 담겨있게 된다
	        
	        //이 JSON을 Map으로 변경해서 원하는 데이터 하나씩 꺼낼 수 있게 하기(Jackson 라이브러리 이용)
	        // (웹소켓 때 이용했던 객체)
	        // Jackson : JSON <-> DTO / Map 변환해주는 라이브러리
	        ObjectMapper objectMapper = new ObjectMapper(); //객체 생성
	        
	        //item(JSON)을 List<Map>으로 변경
	        List<Map<String, String>> list = objectMapper.readValue(item, List.class); //Map만 모여있는 List
	        /*	{
						"baseDate": "20240521",
						"baseTime": "0830",
						"category": "LGT",
						"fcstDate": "20240521",
						"fcstTime": "0900",
						"fcstValue": "0",
						"nx": 60,
						"ny": 127
					}
					이 하나가 Map 하나가 된다
	         * */

	        // list에서 필요한 데이터만 꺼내는 작업
	        /*원리는 ajax 했던 것과 같다
	         * 조회되는 데이터의 특징은 낙뢰 등 카테고리 별로 나온다
	         * 아이템의 가장 먼저 나오는 데이터가 가장 가까운 시간의 정보를 보여주는 거다
	         * ->그래서 0번쨰거 가져와서 비어있던 weaterMap에 세팅*/
	        Map<String, String> weatherMap = new HashMap<>();
	        
	        //현재 시간으로부터 가장 가까운 시간대 얻어오기(0번째가 가장 가까운 시간이다)
	        weatherMap.put("fcstDate", list.get(0).get("fcstDate")); // 20240521
	        weatherMap.put("fcstTime", list.get(0).get("fcstTime")); // 0900
	        
	        //조회된 많은 데이터 중
	        //현재 시간으로부터 가장 가까운 시간대의 데이터만 모아두기
	        for(Map<String, String> map : list){
	            if(map.get("fcstDate") //조회된 데이터 중
	            		.equals(weatherMap.get("fcstDate") // 20240521을 찾는다
	            				) &&
	        		map.get("fcstTime").equals(weatherMap.get("fcstTime"))){

	            	weatherMap.put(map.get("category"), map.get("fcstValue"));
	            }
	        }
	        
	        String sky = null;
	        switch(weatherMap.get("SKY")){
	        case "1": sky = "맑음";    	break;
	        case "3": sky = "구름많음"; break;
	        case "4": sky = "흐림"; 	break;
	        }
	        
	        
	        String pty = null;
	        switch(weatherMap.get("PTY")){
	        case "0": pty = "없음";    	break;
	        case "1": pty = "비";    	break;
	        case "2": pty = "비/눈";    	break;
	        case "3": pty = "눈";    	break;
	        case "5": pty = "빗방울"; break;
	        case "6": pty = "빗방울눈날림"; 	break;
	        case "7": pty = "눈날림"; 	break;
	        }
	        
	        weatherMap.put("SKY", sky);
	        weatherMap.put("PTY", pty);
	        
		    
		    model.addAttribute("weatherMap", weatherMap);
	        
		return "ex2"; //ex2.html로 포워드
	}
	
	
	
}
