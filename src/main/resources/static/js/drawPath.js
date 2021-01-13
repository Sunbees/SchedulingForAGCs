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

const render_init = () => {
    let xAxis = g => g
        .attr("transform", `translate(0,${height - margin.bottom})`)
        .call(d3.axisBottom(xScale));

    let yAxis = g => g
        .attr("transform", `translate(${margin.left},0)`)
        .call(d3.axisLeft(yScale));

    g.append("g").call(xAxis);
    g.append("g").call(yAxis);
    render_block("../data/StoreLocation.csv");
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

const tip = d3.tip()
    .attr("class", "d3-tip")
    .html(data => {
        return `
        <h6>执行行车：${data.crane}</h6>
        <h6>钢卷号：${data.coilNo}</h6>
        <h6>订单号：${data.orderNo}</h6>
        <h6>类型：${data.type === "0" ? "倒剁" : data.type === "1" ? "出入库" : "倒机组"}</h6>
        `
    })
svg.call(tip);

$("#mainsvg").click(function (e) {
    if (e.target === this) {
        tip.hide();
    }
})

let data1;

d3.csv("../data/order.csv").then(data => {
    data1 = processOrderData(data);
    data = data1;
    let start1 = 1;
    let start2 = 1;
    let start3 = 1;
    let end1 = 1;
    let end2 = 1;
    let end3 = 1;

    let startEnter = g.selectAll("circle").data(data).enter();
    startEnter.append("g")
        .attr("name", "start")
        .append("circle")
        .attr("cx", data => xScale(+(data.start[0]) / 1000))
        .attr("cy", data => yScale(+(data.start[1]) / 1000))
        .attr("r", 6)
        .style("fill", color("start"))
        .text("1")
        .style("font-color", "#000")
        .attr('stroke-width', 1)
        .attr("opacity", 0.9)
        .attr("class", data => {
            if (data.crane.endsWith("1")) {
                return "pc1"
            } else if (data.crane.endsWith("2")) {
                return "pc2"
            } else {
                return "pc3"
            }
        })
        .on("mouseover", function () {
            d3.select(this)
                .attr('opacity', 0.5)
                .attr("stroke", color("start"))
                .attr('stroke-width', 4)
        })
        .on("mouseout", function () {
            d3.select(this)
                .attr('opacity', 0.9)
                .attr('stroke', color("start"))
                .attr('stroke-width', 1)
        })
        .on("click", function (d) {
            tip.show(d)
        });
    startEnter.append("g")
        .attr("name", "end")
        .append("circle")
        .attr("cx", data => xScale(+(data.end[0]) / 1000))
        .attr("cy", data => yScale(+(data.end[1]) / 1000))
        .attr("r", 6)
        .style("fill", color("end"))
        .attr('stroke-width', 1)
        .attr("opacity", 0.9)
        .attr("class", data => {
            if (data.crane.endsWith("1")) {
                return "pc1"
            } else if (data.crane.endsWith("2")) {
                return "pc2"
            } else {
                return "pc3"
            }
        })
        .on("mouseover", function () {
            d3.select(this)
                .attr('opacity', 0.5)
                .attr("stroke", color("end"))
                .attr('stroke-width', 4)
        })
        .on("mouseout", function () {
            d3.select(this)
                .attr('opacity', 0.9)
                .attr('stroke', color("end"))
                .attr('stroke-width', 1)
        })
        .on("click", function (data) {
            tip.show(data)
        });

    let textEnter = g.selectAll(".tt").data(data).enter();
    textEnter.append("text")
        .attr("class", data => {
            if (data.crane.endsWith("1")) {
                return "tt pc1";
            } else if (data.crane.endsWith("2")) {
                return "tt pc2";
            } else {
                return "tt pc3";
            }
        })
        .attr("x", data => xScale(+(data.start[0]) / 1000))
        .attr("y", data => yScale(+(data.start[1]) / 1000))
        .attr("text-anchor", "middle")
        .attr("dy", 4)
        .style("font-size", "12px")
        .style("font-weight", "bold")
        .text((data) => {
            if (data.crane.endsWith("1")) {
                return start1++;
            } else if (data.crane.endsWith("2")) {
                return start2++;
            } else {
                return start3++;
            }
        })
    textEnter.append("text")
        .attr("class", data => {
            if (data.crane.endsWith("1")) {
                return "tt pc1";
            } else if (data.crane.endsWith("2")) {
                return "tt pc2";
            } else {
                return "tt pc3";
            }
        })
        .attr("x", data => xScale(+(data.end[0]) / 1000))
        .attr("y", data => yScale(+(data.end[1]) / 1000))
        .attr("text-anchor", "middle")
        .attr("dy", 4)
        .style("font-size", "12px")
        .style("font-weight", "bold")
        .text((data) => {
            if (data.crane.endsWith("1")) {
                return end1++;
            } else if (data.crane.endsWith("2")) {
                return end2++;
            } else {
                return end3++;
            }
        })
})


function processOrderData(data) {
    data.forEach(d => {
        const start = d.start;
        const end = d.end;
        d.start = start.substring(1, start.length - 1).split("-");
        d.end = end.substring(1, end.length - 1).split("-");
    })
    return data;
}


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

