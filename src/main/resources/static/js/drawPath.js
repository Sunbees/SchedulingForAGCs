let margin = ({top: 20, right: 30, bottom: 30, left: 40}),
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
            //.attr('transform', 'translate('+0+','+margin.bottom+')')
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
    .domain(["path1", "path2", "path3"])
    .range(["blue", "red", "orange"]);
const render_update = (routes, flag, id) => {
    let circleUpdate = g.select(`#${flag}`).selectAll(`#${flag}id${id}`).data([routes]);
    let circleEnter = circleUpdate.enter()
        .append('path')
        .attr("id", `${flag}id${id}`)
        .attr("class", flag)
        .attr("d", line)
        .attr("stroke", color(flag))
        .attr("fill", "none");

    circleUpdate.merge(circleEnter)
        .transition().ease(d3.easeLinear).duration(10)
        .attr("d", line)
}
const loadAndRender = (filePath, flag) => {
    d3.csv(filePath).then(data => {
        data = processData(data);
        let routes = [];
        let c = 0;
        let intervalID = setInterval(() => {
            if (c >= data.length) {
                clearInterval(intervalID);
            } else {
                if (c !== 0 && c % 50 === 0) {
                    routes = [data[c - 1]];
                }
                routes.push(data[c]);
                render_update(routes, flag, Math.floor(c / 50));
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
            } else {
                $("#path1").attr("display", 'none');
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
            } else {
                $("#path2").attr("display", 'none');
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
            } else {
                $("#path3").attr("display", 'none');
            }

        }
    })
})


function psHandler() {
    $('#pageloading').hide();
    $("[name=path]").css("display", "inherit")
    // show the detail data
}

