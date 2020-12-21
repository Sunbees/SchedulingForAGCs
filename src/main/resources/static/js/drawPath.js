function row(d) {
    return d;
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
                $("path#crane1").attr("display", 'true');
            } else {
                $("path#crane1").attr("display", 'none');
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
                $("path#crane2").attr("display", 'true');
            } else {
                $("path#crane2").attr("display", 'none');
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
                $("path#crane3").attr("display", 'true');
            } else {
                $("path#crane3").attr("display", 'none');
            }

        }
    })
})

let path1;
let path2;
let path3;
//20200928_1_3  20200925_1_2 20200927_1_2
//d3.csv('../data/20200928_1_3.csv',,row).then(function(data) {
d3.csv('../data/20201102_1_1.csv', row).then(function (data) {
    // console.log(data);
    // drawPath(data);
    path1 = data

})
d3.csv('../data/20201102_1_2.csv', row).then(function (data) {
    // console.log(data);
    // drawPath(data);
    path2 = data
})
d3.csv('../data/20201102_1_3.csv', row).then(function (data) {
    path3 = data
})
d3.timeout(function () {
    drawPath()
}, 1)


//绘制路径
function drawPath() {
    var dur = 2000,
        format = d3.format(',');


    var margin = ({top: 20, right: 30, bottom: 30, left: 40}),
        width = window.innerWidth - margin.left - margin.right,
        height = window.innerHeight * 0.9 - margin.top - margin.bottom;

    var svg = d3.select('#vis')
        .append('svg')
        .attr('width', width + margin.left + margin.top)
        .attr('height', height + margin.top + margin.bottom);

    var xScale = d3.scaleLinear()
        .domain([0, 320])
        .range([margin.left, width - margin.right]);

    var yScale = d3.scaleLinear()
        .domain([0, 42])
        .range([height - margin.bottom, margin.top]);

    var xAxis = g => g
        .attr("transform", `translate(0,${height - margin.bottom})`)
        .call(d3.axisBottom(xScale));

    var yAxis = g => g
        .attr("transform", `translate(${margin.left},0)`)
        .call(d3.axisLeft(yScale));

    svg.append("g")
        .call(xAxis);

    svg.append("g")
        .call(yAxis);

    var line = d3.line()
        .x(function (d) {
            return xScale(d.CurX / 1000);
        })
        .y(function (d) {
            return yScale(d.CurY / 1000);
        })
        // .interpolate("line");
        .curve(d3.curveCatmullRom)
    ;
    // append the path
    d3.timeout(function () {
        svg.append("path")
            .attr("id", "crane1")
            .attr("class","line")
            .attr('stroke-width', 5)
            .style("stroke", "blue")
            .style("fill", "none")
            .attr("d", line(path1));

        // console.log(sss);
        svg.append("path")
            .attr("id", "crane2")
            .attr("class","line")
            .attr('stroke-width', 5)
            .style("stroke", "red")
            .style("fill", "none")
            .attr("d", line(path2));

        svg.append("path")
            .attr("id", "crane3")
            .attr("class","line")
            .attr('stroke-width', 5)
            .style("stroke", "orange")
            .style("fill", "none")
            .attr("d", line(path3));

        var path = document.getElementsByClassName('line');  //获取class标签为line的元素
        let length = path[0].getTotalLength();
        for (let pathElement of path) {
            if(pathElement.getTotalLength() > length) {
                length = pathElement.getTotalLength()
            }
        }
        const animation_dur = length / 150
        for (let pathElement of path) {
            pathElement.style.animation = "dash "+animation_dur+"s linear forwards";
        }
        //获取第一个折线的总共的长度
        d3.selectAll('.line')
            .style('stroke-dasharray', length)          //根据上面获取的值来设置stroke-dasharray值
            .style('stroke-dashoffset', length);

        psHandler()
    }, dur * 1.4);

    function drawCircle(context, radius) {
        context.moveTo(xScale(radius), yScale(0));
        context.arc(xScale(0), yScale(0), radius, 0, 2 * Math.PI);
    }

    function square(context) {
        context.rect(xScale(10), yScale(10), xScale(10), yScale(10));
        return context;
    }

    //////
    d3.timeout(function () {
        //xicheng.json
        // d3.json('Z12.json').then(ready);
        d3.csv('../data/StoreLocation.csv').then(ready);


        function ready(data) {
            // console.log(data);
            svg.selectAll('rect').data(data).enter()
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
                    ;
                    return xScale(d.width) - margin.left
                })
                .attr("height", function (d) {
                    return height - margin.bottom - yScale(d.height)
                })
                //.attr('transform', 'translate('+0+','+margin.bottom+')')
                .style("fill", "#888");


            //文字
            svg.selectAll('g')
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


        } // ready()
    }, dur * 0.5);
}

//字符串转时间
function getDataTime(dataTimeStr) {
    if (dataTimeStr.length = 14) {
        var yyyy, mm, dd, HH, MM, SS;
        yyyy = dataTimeStr.substring(0, 4);
        mm = dataTimeStr.substring(4, 6) - 1; //月份-1
        dd = dataTimeStr.substring(6, 8);
        HH = dataTimeStr.substring(8, 10);
        MM = dataTimeStr.substring(10, 12);
        SS = dataTimeStr.substring(12, 14);
        var tmpTime = new Date(yyyy, mm, dd, HH, MM, SS);
        //console.log(tmpTime);
        return tmpTime;
    } else {
        return new Date;
    }
}


function psHandler(data) {
    $('#pageloading').hide();
    $("[name=path]").css("display", "inherit")
    // show the detail data
}

