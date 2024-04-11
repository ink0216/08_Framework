const searchBtn = document.querySelector("#searchBtn");
searchBtn.addEventListener("click", ()=>{
    const search = document.querySelector("#search"); //input태그
    const keyword = search.value;
    fetch("/search?keyword="+keyword)
    .then(resp=>resp.json())
    .then(list=>{
        const tbody = document.querySelector("tbody");
        console.log(list);
        tbody.innerText="";
        list.forEach(book=>{ //이 안에서 book.bookNo 이런식으로 다 꺼내서 쓸 수 있다
            const tr = document.createElement("tr");
            
            
            const keyList = ['bookNo', 'bookTitle', 'bookWriter', 'bookPrice',
        'regDate'];
        keyList.forEach(key=>{
            const td = document.createElement("td");
            td.innerText=book[key];
            tr.append(td);
            
        });

        const updateBtn = document.createElement("button");
        updateBtn.innerText="수정버튼";
        updateBtn.addEventListener("click", (e)=>{
            let newPrice = Number(prompt("수정할 가격 입력 : "));
            if(!isNaN(newPrice)){
                console.log(book.bookNo); //이제 여기서 book.bookNo를 담아서 요청 보내기!!!!!
                const obj = {
                    bookNo : book.bookNo,
                    newPrice : newPrice
                };
                const content = JSON.stringify(obj);
                fetch("/edit", {
                    method : "PUT",
                    headers : {"Content-Type" : "application/json"},
                    /* {"Content-Type" : "application/json"} */
                    body : content
                    //body에 한개만 담아서 보내면, 
                    //컨트롤러에서 받은 것을 어떤 변수명으로 저장할 지는
                    //중요하지 않다!(그것의 자료형만 중요하다!)
                    //그리고 body에는 한 개만 보낼 수 있어서
                    //여러 개를 보내야 하면 객체로 묶어서 json으로 변환해서
                    //보내야 한다
                    //js와 java 모두에서 통용되는 자료형은 String밖에 없어서!
                })
                .then(resp=>resp.text())
                .then(result=>{
                    if(result>0){
                    
    e.target.parentElement.previousElementSibling.previousElementSibling.innerText
    =newPrice;
    /* document.querySelector("body > table > tbody > tr:nth-child(4) > td:nth-child(4)").firstChild (콘솔창) */
                    }  
                });
                //두 개를 묶어서 보내려면 
                //json으로 만들어서 보낸다
                //js 객체
            }else{
                //숫자를 안 쓴 경우
                e.preventDefault();
            }
            
        });

        const deleteBtn = document.createElement("button");
        deleteBtn.innerText="삭제버튼";
        deleteBtn.addEventListener("click", ()=>{
            //삭제 요청 보내기
            //delete 요청
            fetch("/delete", {
                method : "DELETE",
                headers : {"Content-Type" : "application/json"},
                body : book.bookNo
            })
            .then(resp=>resp.text())
            .then(result=>{
                if(result>0){
                    alert("삭제 성공!!!");
                    location.href="/"; 
                } 
                else{
                    alert("삭제 성공!!!");
                    location.href="/"; 
                } 
            });
        });
        const td1 = document.createElement("td");
        const td2 = document.createElement("td");
        td1.append(updateBtn);
        td2.append(deleteBtn);
        tr.append(td1);
        tr.append(td2);
        tbody.append(tr);
        });
    });
});