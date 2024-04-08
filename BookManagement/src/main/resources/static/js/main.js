/* td만드는 함수 */
const createTd = (text)=>{
    const td = document.createElement("td");
    td.innerText=text;
    return td;
};
/* 조회하기 버튼 */
const viewBtn = document.querySelector("#viewBtn");
viewBtn.addEventListener("click", ()=>{
    const tbody = document.querySelector("tbody");
    fetch("/selectAll")
    .then(resp=>resp.json())
    .then(list=>{
        console.log(list);
            list.forEach((book)=>{
                const tr = document.createElement("tr");
                const keyList = ['bookNo', 'bookTitle', 'bookWriter', 'bookPrice','regDate'];
                /* 번호 제목 글쓴이 가격 등록일  */
                keyList.forEach(key=>{
                    const td = createTd(book[key]);

                    tr.append(td);
                    tbody.append(tr);
                });

        });
    })
});