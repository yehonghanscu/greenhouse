jQuery(document).ready(function() {
    console.log("进入check_table.js");
    initDatatable();
});

let url="../checkModule_file_servlet_action";

let checkId;

function initDatatable(isOrdered){
    $('.datatable').dataTable( {
        "destroy":true,
        "paging":true,
        "searching":false,
        "oLanguage": {
            "aria": {
                "sortAscending": ": activate to sort column ascending",
                "sortDescending": ": activate to sort column descending"
            },
            "sProcessing":   "处理中...",
            "sLengthMenu":   "_MENU_ 记录/页",
            "sZeroRecords":  "没有匹配的记录",
            "sInfo":         "显示第 _START_ 至 _END_ 项记录，共 _TOTAL_ 项",
            "sInfoEmpty":    "显示第 0 至 0 项记录，共 0 项",
            "sInfoFiltered": "(由 _MAX_ 项记录过滤)",
            "sInfoPostFix":  "",
            "sSearch":       "过滤:",
            "oPaginate": {
                "sFirst":    "首页",
                "sPrevious": "上页",
                "sNext":     "下页",
                "sLast":     "末页"
            }
        },
        "aoColumns": [{"mRender": function(data, type, full) {
                sReturn = '<input type="checkbox" class="checkboxes" value="'+full.check_id+'"/>';
                return sReturn;
            },"orderable": false
        },{"mRender": function(data, type, full) {
                sReturn = '<div>'+full.employee_id+'</div>';
                return sReturn;
            },"orderable": false
        },{"mRender": function(data, type, full) {
                sReturn = '<div>'+full.employee_name+'</div>';
                return sReturn;
            },"orderable": false
        },{"mRender": function(data, type, full) {
                sReturn = '<div>'+full.employee_duty+'</div>';
                return sReturn;
            },"orderable": false
        },{"mRender": function(data, type, full) {
                sReturn = '<div>'+full.check_position+'</div>';
                return sReturn;
            },"orderable": false
        },{"mRender": function(data, type, full) {
                sReturn = '<div>'+full.check_remark+'</div>';
                return sReturn;
            },"orderable": false
        },{"mRender": function(data, type, full) {
                sReturn = '<div>'+full.whetherCheck+'</div>';
                return sReturn;
            },"orderable": false
        },{"mRender": function(data, type, full) {
                sReturn = '<div>'+full.check_datetime+'</div>';
                return sReturn;
            },"orderable": false
        },{"mRender": function(data, type, full) {
                sReturn = '<div>'+"<a onclick=\"submitDeleteRecord("+full.check_id+")\">删除</a>"+'</div>';
                sReturn += '<div>'+"<a onclick=\"onModifyRecord("+full.check_id+")\">修改</a>"+'</div>';
                return sReturn;
            },"orderable": false
        }],


        "aLengthMenu": [[5,10,15,20,25,40,50,-1],[5,10,15,20,25,40,50,"所有记录"]],
        "fnDrawCallback": function(){$(".checkboxes").uniform();$(".group-checkable").uniform();},
        //"sAjaxSource": "get_record.jsp"
        "sAjaxSource": isOrdered?"../checkModule_file_servlet_action?Action=showCheckTable&isOrdered=true":"../checkModule_file_servlet_action?Action=showCheckTable&isOrdered=false"
    });
    $('.datatable').find('.group-checkable').change(function () {
        var set = jQuery(this).attr("data-set");
        var checked = jQuery(this).is(":checked");
        jQuery(set).each(function () {
            if (checked) {
                $(this).attr("checked", true);
                $(this).parents('tr').addClass("active");
            } else {
                $(this).attr("checked", false);
                $(this).parents('tr').removeClass("active");
            }
        });
        jQuery.uniform.update(set);
    });
    $('.datatable').on('change', 'tbody tr .checkboxes', function () {
        $(this).parents('tr').toggleClass("active");
    });
}


function onAddRecord(){
    $("#AddCheckRecord").show();
}

function onQueryRecord(){
    $("#QueryCheckRecord").show();
}

function onPrintRecord(){
    alert("前往打印");
    window.parent.location.href="print_check_table.html";
}

function onOrderRecord(){
    // $("#record_list tbody").remove();
    initDatatable(true);

    alert("按打卡时间降序");
}

function onStatisticRecord(){
    window.location.href="statistic_table.html";
}

function onExportRecord(){
    let url="../checkModule_file_servlet_action";
    let message={};
    message.Action="exportCheckRecord";
    $.post(url,message,function (json) {
            if (json.ok === 200) {
                alert(JSON.stringify(json));
                alert("导出成功！");
                $("#ExportCheckRecord #download_url").attr("href",json.download_url);
                $("#ExportCheckRecord").show();
            } else {
                // 否则登录失败
                alert("导出失败！");
            }
        }
    )
}

function onModifyRecord(check_id){
    checkId=check_id;
    $("#ModifyCheckRecord").show();
}

function submitAddRecord(){
    let url="../checkModule_file_servlet_action";
    let message={};
    message.Action="addCheckRecord";
    message.employee_id=$("#AddCheckRecord #employee_id").val();
    message.employee_name=$("#AddCheckRecord #employee_name").val();
    message.employee_duty=$("#AddCheckRecord #employee_duty").val();
    message.check_position=$("#AddCheckRecord #check_position").val();
    message.check_remark=$("#AddCheckRecord #check_remark").val();
    message.whetherCheck=$("#AddCheckRecord #whetherCheck").val();
    $.post(url,message,function (json) {
            if (json.ok === 200) {
                window.location.href="check_table.html";
                alert("添加成功！");
            } else {
                // 否则登录失败
                alert("添加失败！");
            }
        }
    )
    $("#AddCheckRecord").hide();
}

function submitDeleteRecord(check_id){
    let url="../checkModule_file_servlet_action";
    let message={};
    message.check_id=check_id;
    message.Action="deleteCheckRecord";
    $.post(url,message,function (json) {
            if (json.ok === 200) {
                window.location.href="check_table.html";
                alert("删除成功！");
            } else {
                alert("删除失败！");
            }
        }
    )
}

function submitModifyRecord(){
    let url="../checkModule_file_servlet_action";
    let message={};
    message.Action="modifyCheckRecord";
    message.check_id=checkId;
    message.employee_id=$("#ModifyCheckRecord #employee_id").val();
    message.employee_name=$("#ModifyCheckRecord #employee_name").val();
    message.employee_duty=$("#ModifyCheckRecord #employee_duty").val();
    message.check_position=$("#ModifyCheckRecord #check_position").val();
    message.check_remark=$("#ModifyCheckRecord #check_remark").val();
    message.whetherCheck=$("#ModifyCheckRecord #whetherCheck").val();
    $.post(url,message,function (json) {
            if (json.ok === 200) {
                window.location.href="check_table.html";
                alert("修改成功！");
            } else {
                // 否则登录失败
                alert("修改失败！");
            }
        }
    )
    $("#ModifyCheckRecord").hide();
}

function submitQueryRecord(){
    $('.datatable').dataTable( {
        "destroy":true,
        "paging":true,
        "searching":false,
        "oLanguage": {
            "aria": {
                "sortAscending": ": activate to sort column ascending",
                "sortDescending": ": activate to sort column descending"
            },
            "sProcessing":   "处理中...",
            "sLengthMenu":   "_MENU_ 记录/页",
            "sZeroRecords":  "没有匹配的记录",
            "sInfo":         "显示第 _START_ 至 _END_ 项记录，共 _TOTAL_ 项",
            "sInfoEmpty":    "显示第 0 至 0 项记录，共 0 项",
            "sInfoFiltered": "(由 _MAX_ 项记录过滤)",
            "sInfoPostFix":  "",
            "sSearch":       "过滤:",
            "oPaginate": {
                "sFirst":    "首页",
                "sPrevious": "上页",
                "sNext":     "下页",
                "sLast":     "末页"
            }
        },
        "aoColumns": [{"mRender": function(data, type, full) {
                sReturn = '<input type="checkbox" class="checkboxes" value="'+full.check_id+'"/>';
                return sReturn;
            },"orderable": false
        },{"mRender": function(data, type, full) {
                sReturn = '<div>'+full.employee_id+'</div>';
                return sReturn;
            },"orderable": false
        },{"mRender": function(data, type, full) {
                sReturn = '<div>'+full.employee_name+'</div>';
                return sReturn;
            },"orderable": false
        },{"mRender": function(data, type, full) {
                sReturn = '<div>'+full.employee_duty+'</div>';
                return sReturn;
            },"orderable": false
        },{"mRender": function(data, type, full) {
                sReturn = '<div>'+full.check_position+'</div>';
                return sReturn;
            },"orderable": false
        },{"mRender": function(data, type, full) {
                sReturn = '<div>'+full.check_remark+'</div>';
                return sReturn;
            },"orderable": false
        },{"mRender": function(data, type, full) {
                sReturn = '<div>'+full.whetherCheck+'</div>';
                return sReturn;
            },"orderable": false
        },{"mRender": function(data, type, full) {
                sReturn = '<div>'+full.check_datetime+'</div>';
                return sReturn;
            },"orderable": false
        },{"mRender": function(data, type, full) {
                sReturn = '<div>'+"<a onclick=\"submitDeleteRecord("+full.check_id+")\">删除</a>"+'</div>';
                sReturn += '<div>'+"<a onclick=\"onModifyRecord("+full.check_id+")\">修改</a>"+'</div>';
                return sReturn;
            },"orderable": false
        }],


        "aLengthMenu": [[5,10,15,20,25,40,50,-1],[5,10,15,20,25,40,50,"所有记录"]],
        "fnDrawCallback": function(){$(".checkboxes").uniform();$(".group-checkable").uniform();},
        //"sAjaxSource": "get_record.jsp"
        "sAjaxSource": "../checkModule_file_servlet_action?Action=queryCheckRecord&employee_name="+$("#QueryCheckRecord #employee_name").val()+"&whetherCheck="+$("#QueryCheckRecord #whetherCheck").val(),
    });
    $('.datatable').find('.group-checkable').change(function () {
        var set = jQuery(this).attr("data-set");
        var checked = jQuery(this).is(":checked");
        jQuery(set).each(function () {
            if (checked) {
                $(this).attr("checked", true);
                $(this).parents('tr').addClass("active");
            } else {
                $(this).attr("checked", false);
                $(this).parents('tr').removeClass("active");
            }
        });
        jQuery.uniform.update(set);
    });
    $('.datatable').on('change', 'tbody tr .checkboxes', function () {
        $(this).parents('tr').toggleClass("active");
    });
    $("#QueryCheckRecord").hide();
}

function onDeleteRecord(){
    $(".tip").show();
}

