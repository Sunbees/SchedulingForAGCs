let margin = ({top: 10, right: 30, bottom: 30, left: 40}),
    width = window.innerWidth - margin.left - margin.right,
    height = window.innerHeight * 0.9 - margin.top - margin.bottom;

let svg = d3.select('#mainsvg')
    .attr('width', width + margin.left + margin.top)
    .attr('height', height + margin.top + margin.bottom);
let xScale = d3.scaleLinear()
    .domain([0, 320])
    .range([margin.left, width - margin.right]);
let yScale = d3.scaleLinear()
    .domain([0, 42])
    .range([height - margin.bottom, margin.top]);

const g = svg.append("g")
    .attr("id", "maingroup")
    .attr('transform', `translate(${margin.left},${margin.top})`);

const xValue = d => d.CurX / 1000;
const yValue = d => d.CurY / 1000;

const line = d3.line()
    .x(d => xScale(xValue(d)))
    .y(d => yScale(yValue(d)))
    .curve(d3.curveCatmullRom);

const tip = d3.tip()
    .attr("class", "d3-tip")
    .html(data => {
        return `
        <h6>执行行车：${data.crane}</h6>
        <h6>钢卷号：${data.coilNo}</h6>
        <h6>订单号：${data.orderNo}</h6>
        <h6>类型：${data.type === "0" ? "倒剁" : data.type === "1" ? "出入库" : "倒机组"}</h6>
        `
    });
svg.call(tip);

const render_block = (filePath) => {
    d3.csv(filePath).then(data => {
        g.selectAll('rect').data(data).enter()
            .append('g')
            .append('rect')
            .attr('x', function (d) {
                // console.log(data);
                return xScale(d.x)
            })
            .attr('y', function (d) {
                return yScale(d.y)
            })
            .attr("width", function (d) {
                return xScale(d.width) - margin.left
            })
            .attr("height", function (d) {
                return height - margin.bottom - yScale(d.height)
            })
            .style("fill", "#888");
        //文字
        g.selectAll('g')
            .enter()
            .append('text').data(data).enter().append('text')
            .attr('x', function (d) {
                return xScale(d.x);
            })
            .attr('y', function (d) {
                return yScale(d.y) - 5;
            })
            .text(function (d) {
                return d.name;
            })
            .attr('fill', 'black');

        g.append("g").attr("id", "path1");
        g.append("g").attr("id", "path2");
        g.append("g").attr("id", "path3");
    });
};

const addPoint = (flag, enter) => {
    enter.append("g")
        .attr("name", flag)
        .append("circle")
        .attr("cx", data => xScale(+(flag === "start" ? data.start[0] : data.end[0]) / 1000))
        .attr("cy", data => yScale(+(flag === "start" ? data.start[1] : data.end[1]) / 1000))
        .attr("r", 6)
        .style("fill", color(flag))
        .text("1")
        .style("font-color", "#000000")
        .attr('stroke-width', 1)
        .attr("opacity", 0.8)
        .attr("class", data => "pc" + data.flag)
        .on("mouseover", function () {
            d3.select(this)
                .attr('opacity', 0.5)
                .attr("stroke", color(flag))
                .attr('stroke-width', 4)
        })
        .on("mouseout", function () {
            d3.select(this)
                .attr('opacity', 0.8)
                .attr('stroke', color(flag))
                .attr('stroke-width', 1)
        })
        .on("click", function (d) {
            tip.show(d)
        });
}

const addText = (flag, enter, pointNo) => {
    enter.append("text")
        .attr("class", data => "tt pc" + data.flag)
        .attr("x", data => xScale(+(flag === "start" ? data.start[0] : data.end[0]) / 1000))
        .attr("y", data => yScale(+(flag === "start" ? data.start[1] : data.end[1]) / 1000))
        .attr("text-anchor", "middle")
        .attr("dy", 4)
        .style("font-size", "12px")
        .style("font-weight", "bold")
        .text(data => pointNo[+(data.flag) - 1]++)
}

function processOrderData(data) {
    data.forEach(d => {
        const start = d.start;
        const end = d.end;
        d.start = start.substring(1, start.length - 1).split("-");
        d.end = end.substring(1, end.length - 1).split("-");
    })
    return data;
}

let dataT;
let render_order = (orderPath) => {
    d3.csv(orderPath).then(data => {
        data = processOrderData(data);
        dataT = data;

        let pointEnter = g.selectAll("circle").data(data).enter();

        addPoint("start", pointEnter);
        addPoint("end", pointEnter);

        let textEnter = g.selectAll(".tt").data(data).enter();
        let startNo = [1, 1, 1], endNo = [1, 1, 1];
        addText("start", textEnter, startNo);
        addText("end", textEnter, endNo);
    })
}

const render_init = () => {
    let xAxis = g => g
        .attr("transform", `translate(0,${height - margin.bottom})`)
        .call(d3.axisBottom(xScale));

    let yAxis = g => g
        .attr("transform", `translate(${margin.left},0)`)
        .call(d3.axisLeft(yScale));

    g.append("g").call(xAxis);
    g.append("g").call(yAxis);
    let renderB = () => new Promise((resolve) => {
        render_block("../data/StoreLocation.csv");
        resolve()
    })
    let renderO = () => new Promise((resolve) => {
        render_order("../data/order.csv");
        resolve()
    })

    let taskArr = [renderB, renderO];
    let runArr = (arr, start = 0) => {
        if (start > arr.length || start < 0) return; // 参数start不能超过    arr.length，不能为负数
        let next = function (i) {
            if (i < arr.length) {
                let fn = arr[i];
                fn().then(() => {
                    i++;
                    next(i)
                })
            }
        }
        next(start);
    }
    runArr(taskArr, 0);
    // new Promise((resolve) => {
    //     render_block("../data/StoreLocation.csv");
    //     return resolve();
    // }).then(() => {
    //     render_order("../data/order.csv");
    // });

};

const processData = (path) => {
    path.forEach(d => {
        d.CurX = +(d.CurX);
        d.CurY = +(d.CurY);
        d.CurZ = +(d.CurZ);
    });
    return path;
};
const color = d3.scaleOrdinal()
    .domain(["path1", "path2", "path3", "start", "end"])
    .range(["blue", "red", "orange", "lightgreen", "yellow"]);
const render_update = (routes, flag, id) => {
    let circleUpdate = g.select(`#${flag}`).selectAll(`#${flag}id${id}`).data([routes]);
    let circleEnter = circleUpdate.enter()
        .append('path')
        .attr("id", `${flag}id${id}`)
        .attr("class", flag)
        .attr("d", line)
        .attr("stroke", color(flag))
        .attr("stroke-width", 2)
        .attr("fill", "none");

    circleUpdate.merge(circleEnter)
        .transition().ease(d3.easeLinear).duration(1000)
        .attr("d", line);

    if (id >= 50) {
        let disappear = g.select(`#${flag}`).selectAll(`#${flag}id${id - 50}`);
        disappear.transition().ease(d3.easeLinear).duration(10).attr("stroke-opacity", .2);
    }
}
const loadAndRender = (filePath, flag) => {
    d3.csv(filePath).then(data => {
        data = processData(data);
        let routes = [];
        let c = 0;
        let intervalID = setInterval(() => {
            if (c >= data.length) {
                clearInterval(intervalID);
                g.selectAll("path").attr("stroke-opacity", 1)
            } else {
                if (c !== 0 && c % 10 === 0) {
                    routes = [data[c - 1]];
                }
                routes.push(data[c]);
                render_update(routes, flag, Math.floor(c / 10));
                ++c;
            }
        }, 10)
    });
};

render_init();
loadAndRender("../data/20201102_1_1.csv", "path1");
loadAndRender("../data/20201102_1_2.csv", "path2");
loadAndRender("../data/20201102_1_3.csv", "path3");
psHandler();


$("#mainsvg").click(function (e) {
    if (e.target === this) {
        tip.hide();
    }
})


$("#reshow1").click(() => {
    $("#path1").html("");
    loadAndRender("../data/20201102_1_1.csv", "path1");
})
$("#reshow2").click(() => {
    $("#path2").html("");
    loadAndRender("../data/20201102_1_2.csv", "path2");
})
$("#reshow3").click(() => {
    $("#path3").html("");
    loadAndRender("../data/20201102_1_3.csv", "path3");
})
$("#reshowAll").click(() => {
    $("#path1").html("");
    loadAndRender("../data/20201102_1_1.csv", "path1");
    $("#path2").html("");
    loadAndRender("../data/20201102_1_2.csv", "path2");
    $("#path3").html("");
    loadAndRender("../data/20201102_1_3.csv", "path3");
})


$(function () {
    $('[name="switch1"]').bootstrapSwitch({
        onText: "",
        offText: "",
        handleWidth: 30,
        onColor: "default",
        offColor: "success",
        size: "mini",
        inverse: true,
        onSwitchChange: function (event, state) {
            if (!state === true) {
                $("#path1").attr("display", 'true');
                $(".pc1").attr("display", 'true');
            } else {
                $("#path1").attr("display", 'none');
                $(".pc1").attr("display", 'none');
            }
        }
    })
    $('[name="switch2"]').bootstrapSwitch({
        onText: "",
        offText: "",
        handleWidth: 30,
        onColor: "default",
        offColor: "success",
        size: "mini",
        inverse: true,
        onSwitchChange: function (event, state) {
            if (!state === true) {
                $("#path2").attr("display", 'true');
                $(".pc2").attr("display", 'true');
            } else {
                $("#path2").attr("display", 'none');
                $(".pc2").attr("display", 'none');
            }
        }
    })
    $('[name="switch3"]').bootstrapSwitch({
        onText: "",
        offText: "",
        handleWidth: 30,
        onColor: "default",
        offColor: "success",
        size: "mini",
        inverse: true,
        onSwitchChange: function (event, state) {
            if (!state === true) {
                $("#path3").attr("display", 'true');
                $(".pc3").attr("display", 'true');
            } else {
                $("#path3").attr("display", 'none');
                $(".pc3").attr("display", 'none');
            }
        }
    })
})


function psHandler() {
    $('#pageloading').hide();
    $("[name=path]").css("display", "inherit")
    // show the detail data
}

