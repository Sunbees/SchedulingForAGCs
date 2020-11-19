function row(d) {
    return d;
}

$(function () {
    d3.csv('../data/order.csv', row).then(function (data) {
        console.log(data);
        drawTable(data);
    })
});


function drawTable(data) {
    d3.timeout(function () {
        let tb = d3.select('#tb')
        for (let d of data) {
            console.log(d)
            let tr = tb.append("tr")
            tr.append("th").attr("scope", "row").text(d.orderNo)
            tr.append("td").text(d.type)
            tr.append("td").text(d.crane)
            tr.append("td").text(d.start)
            tr.append("td").text(d.end)
            tr.append("td").text(d.coilNo)
            tr.append("td").text(d.startTime)
        }
    }, 500)

}

