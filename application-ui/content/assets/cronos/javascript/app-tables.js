const tables = new Map();

function initializeTable(id,rows) {
    if(tables.has(id)) { tables.delete(id); }

    const data = getTableData(id);
    const table = document.getElementById(id);
    table.innerText = "";

    if (typeof rows !== 'number') {
        rows = 2;
    }

    for (let i = 0; i < rows; i++) {
        data.set(i,new Map());
        data.get(i).set(0,"Column "+(i+1));
    }
    reloadTable(id);
}

function getTableData(id) {
    if(!tables.has(id)) {
        tables.set(id,new Map());
    }
    return tables.get(id);
}

function setTableContent(tableId,columnNumber,rowNumber,text) {
    if(typeof columnNumber === 'number' && typeof rowNumber === 'number') {
        getTableData(tableId).get(columnNumber).set(rowNumber,text);
        reloadTable(tableId);
    }
}

function reloadTable(id) {
    const table = document.getElementById(id);
    const data = getTableData(id);
    table.innerText = "";

    for (let i = 0; i < data.size; i++) {
        let column = id + "-column-" + i;
        const columnData = data.get(i);
        let columnClass = "";
        if(i===0) {
            columnClass = "class='left' "
        } else if(i===(data.size-1)) {
            columnClass = "class='right' "
        }
        table.innerHTML += "<div "+columnClass+"id='"+column+"'></div>"
        column = document.getElementById(column);
        for (let j = 0; j < columnData.size; j++) {
            let row = (column.id)+"-row-"+i;
            let text = columnData.get(j);
            if(typeof text === 'string') {
                text = text.replaceAll('<','‹').replaceAll('>','›')
            }
            let rowClass = "";
            if (j % 2 === 0) {
                rowClass="class='alt' ";
            }
            if(j===0) {
                column.innerHTML += "<h4 id='"+row+"'>"+text+"</h4>";
            } else {
                column.innerHTML += "<p "+rowClass+"id='"+row+"'>"+text+"</p>";
            }
        }
    }
}
