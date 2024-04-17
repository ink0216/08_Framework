/*선택된 이미지 미리보기*/
//const previewList = document.getElementsByClassName("preview"); //img 태그 다섯 개 배열로 얻어옴
//이건 script태그에 있어서 중복돼서 지우기
//파일을 진짜 선택하는 input태그
const inputImageList = document.getElementsByClassName("inputImage"); //input 태그 다섯 개 배열로 얻어옴
const deleteImageList = document.getElementsByClassName("delete-image"); //x버튼 다섯 개 배열로 얻어옴

// x버튼이 눌러져 삭제된 이미지의 순서를 저장 (몇 번이 삭제됐다 몇 번이 삭제됐다)
// 0,1,2,3,4번 이미지 중 2번을 지운 경우 deleteOrder에 2번을 저장해놓을거다
// 이걸 서버에 제출하면 DB까지 가서
// DB에 insert돼있는 0,1,2,3,4에서 2번 행을 지우겠다
// 기존에는 2번이 있었는데 x버튼을 눌러서 이 이미지 필요 없어 해서 지우면 DB에서도 지워야함
// DB에서 지울 게 몇 번인지 기록할 변수를 만들어서 DB까지 보낸다

//x버튼을 여러 번
// Java 의 Set : 안에 1,2,3이 들어있는데 3이 또 들어올 수 없음(정확히는 나중에 들어온 것으로 덮어씌워진다)
// Set은 순서가 없어서 중복데이터가 저장될 수 없음
// 근데 JS에서도 똑같은 게 있다
/* Set : 중복 저장 X, 순서 유지 X */
const deleteOrder = new Set(); 
//x버튼 누를 때마다 Set에 2번이 클릭됐었다고 넣을거다 -> set에 2를 여러 번 넣어도 중복 저장 안되니까 2 하나만 저장될거다

// <input type="file"> 클릭해서 a.jpg 선택했다가 다시 클릭해서 이번에는 취소 눌렀을 때 a.jpg가 남아있지 않고 사라졌었다
//그래서 백업 이용했었다
// 이미지 선택 이후 취소를 누를 경우를 대비한 백업 이미지 만들기(clone 이용)
//(복제품으로 기존 요소를 대체하는 형식의 백업 원리)
const backupInputList = new Array(inputImageList.length); //inputImageList의 길이 만큼 칸 수 만듦
//백업본 저장할 것


/*************input 태그 값 변경 시(파일 선택 시) 실행할 함수 ***************/
/**
 * @param inputImage : 파일이 선택된 input태그
 * @param order : 이미지 순서
 */
const changeImageFn = (inputImage, //파일이 선택된 input type="file"태그
order //화면에서 몇 번째 사진인지
)=>{
    //byte단위로 10 MB 지정
    const maxSize = 1024*1024*10;

    //업로드된 파일 정보가 담긴 객체를 얻어와 변수에 저장해놨다
    const file = inputImage.files[0];
    // inputImage.files : input태그에서 업로드 한 파일들이 배열 형태로 얻어와진다

    //취소를 누른 경우에도 change 이벤트가 발생해서 따져줘야함
    //--------------//파일 선택한 후에 취소 누른 경우 -> 취소해서 파일이 없는 경우---------------------------------------------------------------
    if(file == undefined){
        
        console.log("파일 선택 취소됨");

        //취소 누른 경우 백업 이미지로 바꾸기
        //backupInputList에서도 order번쨰 꺼내와서 대체해야함
        //같은 순서인 것 꺼내오기
        //같은 순서(order)번째 backupInputList 요소를 얻어와 대체하기

        /*한 번 사용된(화면에 추가된) 요소는 재사용(다른 곳에 또 추가하는 것)이 불가능하다!
        백업본이 한 번 대체되어 들어가면, 백업본은 없어지는 것임 -> 다른 곳에 또 못넣어서 백업의 백업본을 또 만든다 */

        //백업본을 한 번 더 복제
        const temp = backupInputList[order].cloneNode(true);

        //이 요소를 지우고 temp로 바꾸기
        inputImage.after(temp); //원본 다음에 temp를 넣었다 (백업본을 다음 요소로 추가)
        inputImage.remove(); //그 후에 원본을 지운다 (원본을 삭제)
        
        //근데 백업본에는 이벤트리스너를 추가해줘야한다 없어서
        inputImage=temp; //(원본 변수에 백업본을 참조할 수 있게 대입해놨다)
        //백업본이 원본이 돼버렸다
        inputImage.addEventListener("change", (e)=>{
            changeImageFn(e.target,order); //백업본에 없는 이벤트 리스너를 추가
            //재귀호출 (자신의 함수가 자신을 다시 호출)
        });
        return;
        /*input태그에 이미지가 선택된 경우에 선택했다 취소하는 경우 백업본으로 바꾸겠다 */

        
    }
    //-----------------선택된 파일의 크기가 최대 크기(maxSize)를 초과했을 때-------------------------------------------------
    if(file.size>maxSize){
        alert("10MB 이하의 이미지를 선택해 주세요");
        /*10MB 초과의 파일을 선택한 경우 alert 띄우고 마니까, input태그의 value값에 들어가있다
            -> 지우거나, 백업본으로 바꾸거나 해야한다 */
            if(backupInputList[order] == undefined
            || backupInputList[order].value==''){
                //해당 순서의 백업 요소가 존재하지 않거나
                //요소는 있는데 값이 없는 경우(비어있는 경우)
                //==아직 아무 파일도 선택된 적 없을 때 (글쓰러 처음 들어와서 처음 올린 사진의 크기가 초과한 경우)
                inputImage.value=""; //파일 사이즈 초과해서 잘못 업로드 된 파일 값 삭제
                return; //끝내기
            }
            //이전에 정상 선택을 먼저 했는데 다음 선택에서 이미지 크기를 초과한 경우 ->백업본으로 바꾸기
            //백업본을 한 번 더 복제
            const temp = backupInputList[order].cloneNode(true);

            //이 요소를 지우고 temp로 바꾸기
            inputImage.after(temp); //원본 다음에 temp를 넣었다 (백업본을 다음 요소로 추가)
            inputImage.remove(); //그 후에 원본을 지운다 (원본을 삭제)
            
            //근데 백업본에는 이벤트리스너를 추가해줘야한다 없어서
            inputImage=temp; //(원본 변수에 백업본을 참조할 수 있게 대입해놨다)
            //백업본이 원본이 돼버렸다
            inputImage.addEventListener("change", (e)=>{
                changeImageFn(e.target,order); //백업본에 없는 이벤트 리스너를 추가
            });
            return;
    }
    //여기서부터는 문제 상황 다해결됐고 정상적인 상황이다
    //--------------선택된 이미지 미리 보기------------------------
    const reader = new FileReader(); //JS에서 파일을 읽고 저장하는 객체

    //선택된 파일을 JS로 읽어오기
    reader.readAsDataURL(file); //파일을 reader로 읽어와서 DataURL로 바꾼다-> 읽어온 내용이 reader.result 변수에 저장됨 -> 이걸 img의 src 속성에 넣으면 화면에 보임
    reader.addEventListener("load",
    //다 읽어와서 적재 끝났을 때 load 이벤트가 완료됨
    e=>{
    const url = e.target.result;
    //img 태그(.preview)에 src 속성으로 url값을 대입하면 미리보기가 된다
    previewList[order].src=url;

    //같은 순서 backupInputList에 input 태그를 복제해서 대입 (백업본 만들어 놓겠다)
    backupInputList[order] = inputImage.cloneNode(true); //업로드 성공한 것을 백업본 만들어놓겠다

    //이미지가 성공적으로 읽어진 경우
    //3번 이미지 선택했다가 x버튼 눌렀다면 deleteOrder set에 3번 들어갔을건데 그걸 다른 이미지로 바꿨으면 deleteOrder에서 그 번호 지워야 함
    //deleteOrder.delete(2)
    //deleteOrder.add(2)
    //deleteOrder는 Set이어서 중복저장 안된다 2 계속 넣어도 2 한번만 들어감
    deleteOrder.delete(order); //이미지가 성공적으로 읽어진 경우 deleteOrder에서 해당 순서를 삭제
    });
};


//이미지 하나하나가 변경 될 때마다
for(let i=0; i<inputImageList.length ; i++){
    
  //input태그에 이미지가 선택된 경우(값이 변경된 경우)
  // inputImageList == nodeList
  inputImageList[i].addEventListener("change", e=>{ //값이 바뀌면 함수를 호출하겠다
      changeImageFn(e.target,i); //두 개를 전달해서 함수 호출
  });

  // ********** x버튼 클릭 시 **********
  deleteImageList[i].addEventListener("click", ()=>{
    if(previewList[i].getAttribute("src") !=null &&
    previewList[i].getAttribute("src") !=""){ //미리보기 이미지가 있을 때에만

      if(orderList.includes(i)){ //기존에 이미지가 존재하고 있을 경우에만
        //존재했으면 지워도 되는거야
        //기존에 화면 해당 인덱스 칸에 이미지가 있던 때

        //x버튼을 눌러서 삭제된 이미지 순서를 deleteOrder에 몇번이 삭제됐는지 기록
        deleteOrder.add(i);
      }
      
    }

      //미리보기 img의 src 속성 지우기
      //img, input,backup의 인덱스가 모두 일치한다는 특징을 이용하기 (0번끼리, 1번끼리 다 묶여있음)
      previewList[i].src=""; //미리보기 이미지 화면 지워
      inputImageList[i].value=""; //input에서 선택된 파일 제거
      backupInputList[i]=undefined; //이제 백업 필요 없어서 제거

      
  });
}

//---------------------------------------------------------------------------------------------
//제출 시 유효성 검사
const boardUpdateFrm = document.querySelector("#boardUpdateFrm");



//form태그에 값을 같이 보낼 때엔 input 태그로 보내야 해서 input type hidden으로 담아서 보냄 
// -------------------------------------------

// 제출 시 유효성 검사

boardUpdateFrm.addEventListener("submit", e => {

    const boardTitle = document.querySelector("[name='boardTitle']");
    const boardContent = document.querySelector("[name='boardContent']");

    if(boardTitle.value.trim().length == 0){
    alert("제목을 작성해 주세요");
    boardTitle.focus();
    e.preventDefault();
    return;
    }

    if(boardContent.value.trim().length == 0){
    alert("내용을 작성해 주세요");
    boardContent.focus();
    e.preventDefault();
    return;
    }


    document.querySelector("[name='deleteOrder']").value
    = Array.from(deleteOrder);
    //<input type="hidden" name="deleteOrder" value="1,2,3"> 이렇게 된다
  //name이 deleteOrder인 요소를 찾아서
  // input 태그에 삭제할 이미지 순서(Set)를 배열로 만든 후 대입
  //    -> value(문자열) 저장 시 배열은 toString()이 호출돼서 양쪽 []가 사라지게 된다
  //Array.from하면 배열로 만들어진다 [1,2] 모양인데
  //value처럼 String을 저장하는 것에 대입되면 자동으로 대괄호가 사라지고 1,2 모양으로 저장된다

    document.querySelector("[name='querystring']").value=location.search;
    //location.search : 쿼리스트링만 얻어와서 저장하는 것
    
    //deleteOrder랑 querystring이 추가로 더 제출된다
    /*Set(3) {1, 2, 3}
    Array.from(deleteOrder); ==> (3) [1, 2, 3] (배열됨) -> ToString호출됨 */
});



































