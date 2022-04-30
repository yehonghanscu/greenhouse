jQuery(document).ready(function() {
    console.log("进入index.js");
    // initWarningTable();
    cropInfo();
    fertilizerInfo();
    productInfo();
    topInfo();
    userInfo();
    setTimeout('wrapBegin()',2000);
});

//获取大棚页面传入id-------------------------------------------------------------------------------------------------------
function getParent_id(){
    var result;
    var url=decodeURI(window.location.search); //获取url中"?"符后的字串
    if(url.indexOf("?") != -1) {
        result = url.substr(url.indexOf("=")+1);
    }
    return result;
}
let Child_id=getParent_id();
let infoView=[];

function cropInfo(){
    let url="../cropModule_file_servlet_action";
    let message={};
    message.Action="getcropRecordList";
    message.value=Child_id;
    $.post(url,message,function (json) {
            if (json.ok === 200) {

                let aaData=json.aaData;
                $("#crop_record").html(aaData.length);
                let sum=0;
                var plantCap=[];
                var datalist=[];
                for(let i=0;i<aaData.length;i++){
                    sum+=parseInt(aaData[i].crop_number);
                    plantCap.push({name:aaData[i].crop_name,value:aaData[i].crop_number});
                    datalist.push({offset:[Math.random()*100,Math.random()*100],symbolSize:(parseInt(plantCap[0].value)/sum)*110,color:'rgba(73,188,247,.14)'})
                    $("#infoView").append("<li>\n" +
                        "                                <p>"+aaData[i].crop_name+"-"+aaData[i].crop_number+"kg-"+aaData[i].crop_status+"</p>\n" +
                        "                            </li>")
                }

                $("#crop_sum").html(sum);

                var myChart = echarts.init(document.getElementById('echarts4'));
                console.log(plantCap[0].value);


                var datas = [];
                for (var i = 0; i < plantCap.length; i++) {
                    var item = plantCap[i];
                    var itemToStyle = datalist[i];
                    datas.push({
                        name: item.value + '\n' + item.name,
                        value: itemToStyle.offset,
                        symbolSize: itemToStyle.symbolSize,
                        label: {
                            normal: {
                                textStyle: {
                                    fontSize: 14
                                }
                            }
                        },
                        itemStyle: {
                            normal: {
                                color: itemToStyle.color,
                                opacity: itemToStyle.opacity
                            }
                        },
                    })
                }
                option = {
                    grid: {
                        show: false,
                        top: 10,
                        bottom: 10
                    },
                    xAxis: [{
                        gridIndex: 0,
                        type: 'value',
                        show: false,
                        min: 0,
                        max: 100,
                        nameLocation: 'middle',
                        nameGap: 5
                    }],
                    yAxis: [{
                        gridIndex: 0,
                        min: 0,
                        show: false,
                        max: 100,
                        nameLocation: 'middle',
                        nameGap: 30
                    }],
                    series: [{
                        type: 'scatter',
                        symbol: 'circle',
                        symbolSize: 120,
                        label: {
                            normal: {
                                show: true,
                                formatter: '{b}',
                                color: '#FFF',
                                textStyle: {
                                    fontSize: '30'
                                }
                            },
                        },
                        itemStyle: {
                            normal: {
                                color: '#F30'
                            }
                        },
                        data: datas
                    }]
                };
                myChart.setOption(option);
                $(document).ready(function () {
                    myChart.resize();

                })
                window.addEventListener("resize", function () {
                    myChart.resize();
                });

            } else {
                // 否则失败
                alert("导出失败！");
            }
        }
    )
}

function fertilizerInfo(){
    let url="../fertilizerModule_file_servlet_action";
    let message={};
    message.Action="getFertilizerRecordList";
    message.value=Child_id;
    $.post(url,message,function (json) {
            if (json.ok === 200) {

                let aaData=json.aaData;
                $("#fossil_record").html(aaData.length);
                let sum=0;
                for(let i=0;i<aaData.length;i++){
                    sum+=parseInt(aaData[i].fertilizer_number);
                    $("#infoView").append("<li>\n" +
                        "                                <p>"+aaData[i].fertilizer_name+"-"+aaData[i].fertilizer_number+"kg-"+aaData[i].fertilizer_status+"</p>\n" +
                        "                            </li>")
                }

                $("#fossil_sum").html(sum);

            } else {
                // 否则失败
                alert("导出失败！");
            }
        }
    )
}

function productInfo(){
    let url="../maturecropModule_file_servlet_action";
    let message={};
    message.Action="getMaturecropRecordList";
    message.value=Child_id;
    $.post(url,message,function (json) {
            if (json.ok === 200) {

                let aaData=json.aaData;
                $("#product_record").html(aaData.length);
                let sum=0;
                for(let i=0;i<aaData.length;i++){
                    sum+=parseInt(aaData[i].maturecrop_number);
                    $("#infoView").append("<li>\n" +
                        "                                <p>"+aaData[i].maturecrop_name+"-"+aaData[i].maturecrop_number+"kg-"+aaData[i].maturecrop_status+"</p>\n" +
                        "                            </li>")
                }

                $("#product_sum").html(sum);

            } else {
                // 否则失败
                alert("导出失败！");
            }
        }
    )
}

function topInfo(){
    let url="../sensorModule_file_servlet_action";
    let message={};
    message.Action="visInfoGet";
    message.sensor_name="传感器"+Child_id;
    $.post(url,message,function (json) {
            if (json.ok === 200) {
                let aaData=json.aaData;
                // console.log(aaData);
                $("#avgTemp").html(aaData.avg(temperature));
                $("#avgHumi").html(aaData.avg(humidity))

            } else {
                // 否则失败
                alert("导出失败！");
            }
        }
    )
}

function userInfo(){
    let url="../Login_ServletAction";
    let message={};
    message.Action="getUserRecord";
    message.sort="up";
    $.post(url,message,function (json) {
            if (json.ok === 200) {
                let aaData=json.aaData;
                // console.log(aaData);
                let v1=0;
                let v2=0;
                let v3=0;
                for(let i=0;i<aaData.length;i++){
                    if(aaData[i].userLevel==2)
                    {
                        v1++;
                    }
                    else if(aaData[i].userLevel==1)
                    {
                        v2++;
                    }
                    else
                    {
                        v3++;
                    }
                    $("#userList").append(" <li class=\"clearfix\"> <span class=\"pulll_left\"><img src=\"images/head.jpg\">"+aaData[i].account+"-"+aaData[i].mail+"</span> <span class=\"pulll_right\">level："+aaData[i].userLevel+" </span> </li>")
                }
                console.log(v1,v2,v3)

                //用户概览
                var myChart1;
                myChart1= echarts.init(document.getElementById('echarts1'));

                option1 = {
                    series: [{
                        type: 'pie',
                        radius: ['70%', '80%'],
                        color: '#0088cc',
                        label: {
                            normal: {
                                position: 'center'
                            }
                        },
                        data: [{
                            value: v1,
                            name: '普通用户',
                            label: {
                                normal: {
                                    formatter: v1 + '',
                                    textStyle: {
                                        fontSize: 20,
                                        color: '#fff',
                                    }
                                }
                            }
                        },
                        ]
                    }]
                };


                var myChart3 = echarts.init(document.getElementById('echarts3'));

                option2 = {
                    series: [{
                        type: 'pie',
                        radius: ['70%', '80%'],
                        color: '#fccb00',
                        label: {
                            normal: {
                                position: 'center'
                            }
                        },
                        data: [{
                            value: v2,
                            name: '普通管理员',
                            label: {
                                normal: {
                                    formatter: v2 + '',
                                    textStyle: {
                                        fontSize: 20,
                                        color: '#fff',
                                    }
                                }
                            }
                        },
                        ]
                    }]
                };


                var myChart2 = echarts.init(document.getElementById('echarts2'));
                option3 = {
                    series: [{
                        type: 'pie',
                        radius: ['70%', '80%'],
                        color: '#62b62f',
                        label: {
                            normal: {
                                position: 'center'
                            }
                        },
                        data: [{
                            value: v3,
                            name: '系统管理员',
                            label: {
                                normal: {
                                    formatter: v3 + '',
                                    textStyle: {
                                        fontSize: 20,
                                        color: '#fff',
                                    }
                                }
                            }
                        },
                        ]
                    }]
                };
                setTimeout(function () {
                    myChart1.setOption(option1);
                    myChart2.setOption(option2);
                    myChart3.setOption(option3);
                }, 500);
                myChart1.resize();
                myChart2.resize();
                myChart3.resize();

            } else {
                // 否则失败
                alert("导出失败！");
            }
        }

    )
}



