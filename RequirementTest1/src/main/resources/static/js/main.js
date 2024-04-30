const selectBtn = document.querySelector("#selectBtn");
selectBtn.addEventListener("click", ()=>{
    const tbody = document.querySelector("#tbody");
    tbody.innerHTML="";
    fetch("/select")
    .then(resp=>resp.json())
    .then(studentList =>{
        for(let student of studentList){
            const tr = document.createElement("tr");
            const keyList = ['studentNo', 'studentName', 'studentMajor', 'studentGender'];
            keyList.forEach(key =>{
                const td = document.createElement("td");
                td.innerHTML=student[key];
                tr.append(td);
                tbody.append(tr);
            });
        }
    
    });
});