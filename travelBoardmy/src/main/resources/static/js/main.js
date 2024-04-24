//대륙 선택 드롭다운 요소
const selectContinent = document.getElementById("selectContinent");

//게시판 접속 버튼 요소
const connectBoard = document.querySelector("#connectBoard");



connectBoard.addEventListener("click",()=>{

    if(selectContinent.value == "대륙 선택"){

        alert("제출할 수 없습니다.");

        return;
    }
});
