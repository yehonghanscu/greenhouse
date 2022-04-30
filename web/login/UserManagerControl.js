// 权限控制菜单栏显示
function UserMenuControl(userLevel){
    if(userLevel==0){
        initSystemManagerMenu();
    }else if(userLevel==1){
        initCommonManagerMenu();
    }else if(userLevel==2){ // 普通用户
        document.getElementById("deviceMenu").style.display="none";
        document.getElementById("workerMenu").style.display="none";
        document.getElementById("cautionMenu").style.display="none";
    }
    setOnclick();
}

// 对列表项目设置监听
function setOnclick(){
    $(".menuson li").click(function(){
        $(".menuson li.active").removeClass("active")
        $(this).addClass("active");
    });
}



// 初始化普通管理员菜单
function initCommonManagerMenu(){
    // 用户管理模块
    let ele=document.createElement('li');
    ele.innerHTML="<li><cite></cite><a onClick='getUserRecordTable()'>查看用户信息</a><i></i></li>";
    let other=document.createElement('li');
    other.innerHTML="<li><cite></cite><a onClick='CheckUserApply()'>审核注册信息</a><i></i></li>";

    document.getElementById("ManagerUserMenu").appendChild(ele);
    document.getElementById("ManagerUserMenu").appendChild(other);
    // 用户管理模块

    // // 权限管理模块
    // let permission1=document.createElement("li");
    // permission1.innerHTML="<li><cite></cite><a onclick=\"viewPermission()\">查看用户权限</a><i></i></li>";
    // let permission2=document.createElement("li");
    // permission2.innerHTML="<li><cite></cite><a onclick=\"dealPermissionApply()\">处理权限申请</a><i></i></li>";
    // let permission3=document.createElement("li");
    // permission3.innerHTML="<li><cite></cite><a onclick=\"PermissionModifyRecord()\">权限修改记录</a><i></i></li>";
    // document.getElementById("permissionMenu").appendChild(permission1);
    // document.getElementById("permissionMenu").appendChild(permission2);
    // document.getElementById("permissionMenu").appendChild(permission3);
    // 权限管理模块

    // 薪资管理模块
    // let salary1=document.createElement("li");
    // salary1.innerHTML="<li><cite></cite><a href=\"#\">增加员工</a><i></i></li>";
    // let salary2=document.createElement("li");
    // salary2.innerHTML="<li><cite></cite><a href=\"#\">删除员工</a><i></i></li>";
    // let salary3=document.createElement("li");
    // salary3.innerHTML="<li><cite></cite><a href=\"#\">修改员工</a><i></i></li>";
    // document.getElementById("salaryMenu").appendChild(salary1);
    // document.getElementById("salaryMenu").appendChild(salary2);
    // document.getElementById("salaryMenu").appendChild(salary3);


}
// 初始化管理员用户菜单
function initSystemManagerMenu(){
    initCommonManagerMenu();

    // 权限管理模块
    let permission1=document.createElement("li");
    permission1.innerHTML="<li><cite></cite><a onclick=\"viewPermission()\">查看用户权限</a><i></i></li>";
    let permission2=document.createElement("li");
    permission2.innerHTML="<li><cite></cite><a onclick=\"dealPermissionApply()\">处理权限申请</a><i></i></li>";
    let permission3=document.createElement("li");
    permission3.innerHTML="<li><cite></cite><a onclick=\"PermissionModifyRecord()\">权限修改记录</a><i></i></li>";
    document.getElementById("permissionMenu").appendChild(permission1);
    document.getElementById("permissionMenu").appendChild(permission2);
    document.getElementById("permissionMenu").appendChild(permission3);
    // 权限管理模块


    // 反馈模块
    let feedBack=document.createElement("li");
    feedBack.innerHTML="<li><cite></cite><a onClick='dealFeedback()'>处理反馈信息</a><i></i></li>";
    document.getElementById("feedBackMenu").appendChild(feedBack);
    // 反馈模块
}


// 以下是Js控制onclick函数
// 审核注册信息 点击函数
function CheckUserApply(){
    window.parent.frames['rightFrame'].location="login/checkDataTable.html?_="+Math.random();
}

// 实现页面跳转
// 查看用户信息
function getUserRecordTable(){
    window.parent.frames['rightFrame'].location="login/DataTable.html?_="+Math.random();
}
// 删除用户信息
function deleteUserRecord(){
    // 调用[login/DataTable.html]下的函数
    alert("请在表格最后一栏中直接删除！");
    window.parent.frames['rightFrame'].location="login/DataTable.html?_="+Math.random();
}
// 修改用户信息
function modifyUserRecord(){
    //
    alert("请在表格操作栏进行修改！")
    window.parent.frames['rightFrame'].location="login/DataTable.html?_="+Math.random();
}

// 查看个人信息
function viewPersonInfo(){
    window.parent.frames['rightFrame'].location="login/personalInfo/viewPersonInfo.html?_="+Math.random();
}

// 完善个人信息
function modifyPersonInfo(){
    window.parent.frames['rightFrame'].location="login/personalInfo/viewPersonInfo.html?modify="+Math.random();
}

// 查看用户权限
function viewPermission(){
    window.parent.frames['rightFrame'].location="login/permission/viewPermission.html?modify="+Math.random();
}

// 查看权限修改记录
function PermissionModifyRecord(){
    window.parent.frames['rightFrame'].location="login/permission/modifyRecord.html?modify="+Math.random();

}

// 申请权限
function applyPermission(){
    window.parent.frames['rightFrame'].location="login/permission/applyPermission.html?modify="+Math.random();
}

// 处理权限申请
function dealPermissionApply(){
    window.parent.frames['rightFrame'].location="login/permission/dealApplyPermission.html?modify="+Math.random();
}