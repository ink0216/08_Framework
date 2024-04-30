//img 태그에는 preview라는 클래스 존재
/*
querySelector(), querySelectorAll()은 
호출되었을 시점의 요소 형태를 그대로 얻어옴

예시
#text가 있었는데 처음 value가 10이었을 때

처음 화면에 previewList,inputImageList,deleteImageList의  src  속성에 아무것도 없었을 거다
처음 해석 시의 아무것도 없었던 상태로 저장돼서 나중에 추가해도 안바뀌는 문제가 있다

---------------------------------------------------
getElementsByClassName()같은 경우에는
요소를 얻어와 계속 추적 -> 실시간으로 변경*변경된 값 확인 가능하다
inputImageList[0].value값이
querySelector나 querySelectorAll로 하면 썸네일 이미지 바꿔서 선택할 떄마다 값이 안변하는데 getElementsByClassName으로 하면 실시간으로 값이 바뀐다(콘솔 눈알)
//근데 querySelectorAll 쓰고 싶으면 밑의 세 줄을 이벤트 리스너 안에다 넣으면 된다
그럼 이벤트 발생할 시점의 값을 계속 새로 가져옴
*/
/*선택된 이미지 미리보기*/
const previewList = document.getElementsByClassName("preview"); //img 태그 다섯 개 배열로 얻어옴

//파일을 진짜 선택하는 input태그
const inputImageList = document.getElementsByClassName("inputImage"); //input 태그 다섯 개 배열로 얻어옴

const deleteImageList = document.getElementsByClassName("delete-image"); //x버튼 다섯 개 배열로 얻어옴

// <input type="file"> 클릭해서 a.jpg 선택했다가 다시 클릭해서 이번에는 취소 눌렀을 때 a.jpg가 남아있지 않고 사라졌었다
//그래서 백업 이용했었다
// 이미지 선택 이후 취소를 누를 경우를 대비한 백업 이미지 만들기(clone 이용)
//(복제품으로 기존 요소를 대체하는 형식의 백업 원리)
const backupInputList = new Array(inputImageList.length); //inputImageList의 길이 만큼 칸 수 만듦

/*************input 태그 값 변경 시(파일 선택 시) 실행할 함수 ***************/
/**
 * @param inputImage : 파일이 선택된 input태그
 * @param order : 이미지 순서
 */
const changeImageFn = (inputImage, //파일이 선택된 input태그
order
)=>{
    //byte단위로 10 MB 지정
    const maxSize = 1024*1024*10;

    //업로드된 파일 정보가 담긴 객체를 얻어와 변수에 저장해놨다
    const file = inputImage.files[0]; //파일은 한 번에 하나씩만 업로드하니까 0번쨰에 있다
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
    reader.readAsDataURL(file); //파일을 읽어와서 DataURL로 바꾼다-> 읽어온 내용이 reader.result 변수에 저장됨 -> 이걸 img의 src 속성에 넣으면 화면에 보임
    reader.addEventListener("load",
    //다 읽어와서 적재 끝났을 때 load 이벤트가 완료됨
    e=>{
    const url = e.target.result;
    //img 태그(.preview)에 src 속성으로 url값을 대입
    previewList[order].src=url;

    //같은 순서 backupInputList에 input 태그를 복제해서 대입 (백업본 만들어 놓겠다)
    backupInputList[order] = inputImage.cloneNode(true);
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
        //미리보기 img의 src 속성 지우기
        //img, input,backup의 인덱스가 모두 일치한다는 특징을 이용하기 (0번끼리, 1번끼리 다 묶여있음)
        previewList[i].src=""; //미리보기 이미지 화면 지워
        inputImageList[i].value=""; //input에서 선택된 파일 제거
        backupInputList[i].value=""; //이제 백업 필요 없어서 제거
    });
}

//작성 form 유효성 검사
//버튼이 클릭되었을 때 보다 form이 제출되었을 때 로 하는게 더 좋다!!!
document.querySelector("#boardWriteFrm").addEventListener("submit", e=>{
    const boardTitle = document.querySelector("[name='boardTitle']");
    const boardContent = document.querySelector("[name='boardContent']");

    if(boardTitle.value.trim().length==0){
        //제목을 안썼다
        alert("제목을 작성해 주세요");
        boardTitle.focus();
        e.preventDefault();
        return; //더이상 검사할 필요 없다
    }

    if(boardContent.value.trim().length==0){
        //제목을 안썼다
        alert("내용을 작성해 주세요");
        boardContent.focus();
        e.preventDefault();
        return; //더이상 검사할 필요 없다
    }
});













