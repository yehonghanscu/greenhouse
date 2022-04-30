package login.Servlet;

import com.company.sendCode;
import login.DbOperator.sqlOperator;
import login.export.JsonToFile;
import org.apache.commons.mail.EmailException;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

public class ServletAction extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("执行doGet!");

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("执行doPost!");

    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("执行service!");
        req.setCharacterEncoding("UTF-8");
        // 返回给前端的json
        JSONObject jsonObject=new JSONObject();
        String action=req.getParameter("Action");
        sqlOperator sqlOp=new sqlOperator();
        switch (action){
            // 此增加功能只能进行用户表的增加
            case "addUserInfo":{
                String account=req.getParameter("account");
                String mail=req.getParameter("mail");
                String password=req.getParameter("password");
                String userLevel=req.getParameter("userLevel");
                String isWorker=req.getParameter("isWorker");
                HashMap map=new HashMap();
                map.put("account",account);
                map.put("mail",mail);
                map.put("PASSWORD",password);
                map.put("userLevel",userLevel);
                map.put("isWorker",isWorker);
                try {
                    sqlOp.AddRecord(map);
                    jsonObject.put("ok",200);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

            // 此delete可以对不同的数据表进行适配
            case "deleteInfo":{
                System.out.println("[Login_ServletAction]:deleteInfo");
                String id=req.getParameter("id");
                String dbName=req.getParameter("tableName");
                System.out.println("ID:"+id+"  Dbname:"+dbName);
                try {
                    sqlOp.DeleteRecord(id,dbName);
                    jsonObject.put("ok",200);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

            // 对用户信息表进行modify
            case "modifyUserInfo":{
                String id=req.getParameter("id");
                String account=req.getParameter("account");
                String mail=req.getParameter("mail");
                String userLevel=req.getParameter("userLevel");
                String userName=req.getParameter("userName");
                String userGender=req.getParameter("userGender");
                String isWorker=req.getParameter("isWorker");
                HashMap map=new HashMap();
                map.put("id",id);
                map.put("account",account);
                map.put("mail",mail);
                map.put("userLevel",Integer.parseInt(userLevel));
                map.put("userName",userName);
                map.put("userGender",userGender);
                map.put("isWorker",isWorker);
                try {
                    sqlOp.ModifyUserRecord(map);
                    jsonObject.put("ok",200);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

            // 统计数据
            case "statisticsUserInfo":{
                try {
                    jsonObject=sqlOp.StatisticsUserRecord();
                    jsonObject.put("ok",200);
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
            }

            // 获取用户信息，用于构成DataTable
            case "getUserRecord":{
                String sort=req.getParameter("sort");
                System.out.println(sort);
                try {
                    jsonObject=sqlOp.getUserRecord(sort);
                } catch (SQLException | JSONException e) {
                    System.out.println("获取数据失败!");
                    e.printStackTrace();
                }
                break;
            }
            // 登录
            case "login":{
                String account=req.getParameter("account");
                String password=req.getParameter("password");
                try {
                    jsonObject=sqlOp.isExist(account,password);
                } catch (SQLException | JSONException e) {
                    System.out.println("数据库查询出现异常！");
                    e.printStackTrace();
                }
                break;
            }

            case "logout":{
                String userId=req.getParameter("userId");
                try {
                    sqlOp.logout(userId);
                    jsonObject.put("ok",200);
                } catch (JSONException | SQLException e) {
                    e.printStackTrace();
                }
                break;
            }

            // 注册
            case "register":{
                String registerAccount=req.getParameter("account");
                String password=req.getParameter("password");
                String mail=req.getParameter("mail");
                try {
                    if(sqlOp.Register(registerAccount,password,mail)){
                        jsonObject.put("ok",200);
                    }else{
                        jsonObject.put("ok",400);
                    }
//                    responseBack(req,resp,jsonObject);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

            // 判断注册信息是否合法
            case "isExisted":{
                String mail=req.getParameter("mail");
                String account=req.getParameter("account");
                System.out.println("mail:"+mail+",account:"+account);
                String content,key;
                if(mail == null){
                    content=account;
                    key="account";
                }else{
                    content=mail;
                    key="mail";
                }
                System.out.println("content:"+content+",key:"+key);
                try {
                    int status=sqlOp.isExisted(content,key);
                    if(status==-1){
                        jsonObject.put("ok",500);
                    }else if(status==-2) {
                        jsonObject.put("ok",400);
                    }else if(status==0){
                        jsonObject.put("ok",200);
                    }
//                    responseBack(req,resp,jsonObject);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

            // 发送验证码
            case "sendCode":{
                String way=req.getParameter("way");
                String mail=req.getParameter("mail");
                sendCode sendC=new sendCode(mail);
                try {
                    if(sendC.Send(way)){
                        jsonObject.put("ok",200);
                        System.out.println("验证码:"+sendC.getCode());
                        jsonObject.put("code",sendC.getCode());
                        jsonObject.put("mail",mail);
                    }else{
                        jsonObject.put("ok",404);
                    }
                } catch (EmailException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

            // 导出文件
            case "exportFile":{
                // 该json用于保存导出的数据
                JSONObject json=new JSONObject();
                System.out.println("正在导出");
                // 获取需要操作的表名
                String name=req.getParameter("tableName");
                System.out.println("将要操作的表:"+name);
                try {
                    json=sqlOp.getRecord(name);

                    // JSON导出文件的工具类对象
                    JsonToFile jsonToFile=new JsonToFile(req.getParameter("tag"));
                    jsonObject=jsonToFile.setJsonTOTxt(json);

                    // 转excel
                    jsonToFile.setJsonToExcel(json,jsonObject);
                    // 转pdf
                    jsonToFile.setExcelToPdfCsv(jsonObject);
                    System.out.println(jsonObject);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

            // 获取设备信息
            case "getDeviceRecord":{
                try {
                    jsonObject=sqlOp.getDeviceRecord();
                } catch (JSONException | SQLException e) {
                    e.printStackTrace();
                }
                break;
            }

            // 获取用户注册表信息
            case "getApplyRecord":{
                System.out.println("[Login_ServletAction]:getApplyRecord");
                String sort=req.getParameter("sort");
                System.out.println(sort);
                try {
                    jsonObject=sqlOp.getApplyRecord(sort);
                    System.out.println("jsonObject:"+jsonObject);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            }
            // 添加用户注册信息表
            case "addApplyInfo":{
                System.out.println("[Login_ServletAction]:addApplyInfo");
                String account=req.getParameter("account");
                String mail=req.getParameter("mail");
                String password=req.getParameter("password");
                HashMap map=new HashMap();
                map.put("account",account);
                map.put("mail",mail);
                map.put("password",password);
                try {
                    sqlOp.addApplyInfo(map);
                    jsonObject.put("ok",200);
                } catch (JSONException | SQLException e) {
                    e.printStackTrace();
                }
                break;
            }

            //
            case "modifyApplyInfo":{
                String id=req.getParameter("id");
                HashMap map=new HashMap();
                map.put("account",req.getParameter("account"));
                map.put("mail",req.getParameter("mail"));
                map.put("password",req.getParameter("password"));
                map.put("applyDate",req.getParameter("applyDate"));
                try {
                    sqlOp.ModifyApplyRecord(Integer.parseInt(id),map);
                    jsonObject.put("ok",200);
                } catch (JSONException | SQLException e) {
                    e.printStackTrace();
                }
                break;
            }

            // 普通管理员审核用户注册信息
            case "refuseApply":{
                System.out.println("[Login_ServletAction]:refuseApply");
                String id=req.getParameter("id");
                // 是否全部拒绝？
                boolean refAll= id == null;
                try {
                    sqlOp.dealApply(id,refAll,"refuse");
                    jsonObject.put("ok",200);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

            // 普通管理员审核用户注册信息
            case "agreeApply":{
                System.out.println("[Login_ServletAction]:agreeApply");
                String id=req.getParameter("id");
                // 是否全部允许?
                boolean agrAll=false;
//                String all=req.getHeader("all");
//                if(all.equals("yes")){
//                    agrAll=true;
//                }
                try {
                    sqlOp.dealApply(id,agrAll,"agree");
                    jsonObject.put("ok",200);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

            case "queryApplyInfo":{
                System.out.println("[Login_ServletAction]:queryApplyInfo");
                // 或还是与
                String queryCondition=req.getParameter("queryCondition");
                // 精准或模糊
                String queryWay=req.getParameter("queryWay");
                String account=req.getParameter("account");
                String mail=req.getParameter("mail");
                String queryStartDate=req.getParameter("queryStartDate");
                String queryEndDate=req.getParameter("queryEndDate");
                HashMap map=new HashMap();
                if(!account.equals("")){
                    map.put("account",account);
                }
                if(!mail.equals("")){
                    map.put("mail",mail);
                }
                if(!queryStartDate.equals("") && !queryEndDate.equals("")) {
                    map.put("queryStartDate", queryStartDate);
                    map.put("queryEndDate", queryEndDate);
                }else if(queryStartDate.equals("") && !queryEndDate.equals("")){
                    map.put("queryEndDate",queryEndDate);
                }else if(queryEndDate.equals("") && !queryStartDate.equals("")){
                    map.put("queryStartDate",queryStartDate);
                }
                try {
                    jsonObject=sqlOp.queryApplyInfo(queryCondition,queryWay,map);
                    jsonObject.put("ok",200);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

            // 统计申请注册的用户信息
            case "statisticsApplyInfo":{
                try {
                    jsonObject=sqlOp.StatisticsApplyRecord();
                    jsonObject.put("ok",200);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

            // 获取用户信息
            case "getPersonInfo":{
                String userId=req.getParameter("userId");
                try {
                    jsonObject=sqlOp.getPersonInfo(userId);
                    jsonObject.put("ok",200);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

            // 修改用户信息
            case "modifyPersonInfo":{
                String userId=req.getParameter("userId");
                HashMap map=new HashMap();
                try {
                    if(!sqlOp.canModify(userId,req.getParameter("account"))){
                        jsonObject.put("ok",404);
                    }else{
                        map.put("account",req.getParameter("account"));
                        map.put("userGender",req.getParameter("userGender"));
                        map.put("userName",req.getParameter("username"));
                        sqlOp.modifyPersonInfo(userId,map);
                        jsonObject.put("ok",200);
                    }
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

            // 管理员修改用户权限
            case "modifyPermission":{
                String userId=req.getParameter("userId");
                String userLevel=req.getParameter("userLevel");
                try {
                    sqlOp.modifyPermission(userId,userLevel);
                    jsonObject.put("ok",200);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

            // 获取用户权限，形成表格
            case "getPermissionModifyRecord":{
                try {
                    jsonObject=sqlOp.getPermissionModifyRecord();
                    jsonObject.put("ok",200);
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

            // 统计用户权限信息
            case "staticPermissionInfo":{
                try {
                    jsonObject=sqlOp.staticPermissionInfo();
                    jsonObject.put("ok",200);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

            // 导出用户权限信息
            case "exportPermissionInfo":{
                String tag=req.getParameter("tag");
                try {
                    JSONObject json= sqlOp.exportPermissionInfo(tag);
                    JsonToFile jsonToFile=new JsonToFile(tag);
                    jsonObject=jsonToFile.setJsonTOTxt(json);
                    jsonToFile.setJsonToExcel(json,jsonObject);
                    jsonToFile.setExcelToPdfCsv(jsonObject);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }

                break;
            }

            // 用户申请修改权限
            case "applyPersonPermission":{
                String userId=req.getParameter("userId");
                String account=req.getParameter("account");
                String mail=req.getParameter("mail");
                String oldLevel=req.getParameter("oldLevel");
                String applyLevel=req.getParameter("applyLevel");
                try {
                    jsonObject=sqlOp.applyPermission(userId,account,mail,Integer.parseInt(oldLevel),Integer.parseInt(applyLevel));
                } catch (JSONException | SQLException e) {
                    e.printStackTrace();
                }
                break;
            }

            // 撤销权限申请
            case "cancelPermissionApply":{
                try {
                    jsonObject=sqlOp.cancelPermissionApply(req.getParameter("applyUserId"),req.getParameter("flag"));
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

            // 获取用户权限申请信息
            case "getApplyPermission":{
                try {
                    String applyUserId=req.getParameter("applyUserId");
                    String type=req.getParameter("type");
                    String size=null;
                    if(!type.equals("person")){
                        size=req.getParameter("size");
                    }
                    jsonObject=sqlOp.getApplyPermissionRecord(applyUserId,type,size);
                    jsonObject.put("ok",200);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

            // 统计用户权限申请信息
            case "statisticApplyPermission":{
                try {
                    jsonObject=sqlOp.statisticApplyPermission();
                    jsonObject.put("ok",200);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

            // 管理员处理用户权限修改申请
            case "dealPermissionApply":{
                HashMap<String,String> map=new HashMap<>();
                map.put("applyUserId",req.getParameter("applyUserId"));
                map.put("dealManageAccount",req.getParameter("dealManageAccount"));
                map.put("dealManageLevel",req.getParameter("dealManageLevel"));
                String operator=req.getParameter("operator");
                if(operator.equals("允许申请")){
                    map.put("applyLevel",req.getParameter("applyLevel"));
                }else{
                    if(!operator.equals("拒绝申请")){
                        return;
                    }
                }
                try {
                    sqlOp.dealPermissionApply(operator,map);
                    jsonObject.put("ok",200);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

            // 修改用户权限申请
            case "modifyApplyPermissionRecord":{
                HashMap<String,String> map=new HashMap<>();
                map.put("applyDate",req.getParameter("applyDate"));
                map.put("dealDate",req.getParameter("dealDate"));
                map.put("oldLevel",req.getParameter("oldLevel"));
                map.put("applyLevel",req.getParameter("applyLevel"));
                try {
                    sqlOp.modifyApplyPermissionRecord(map,req.getParameter("applyUserId"),req.getParameter("isDealed"));
                    jsonObject.put("ok",200);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

            // 统计用户权限修改信息
            case "staticPermissionModifyInfo":{
                try {
                    jsonObject=sqlOp.staticPermissionModifyInfo();
                    jsonObject.put("ok",200);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

            // 添加用户权限申请记录
            case "addPermissionModifyRecord":{
                HashMap<String,String> map=new HashMap<>();
                map.put("userAccount",req.getParameter("userAccount"));
                map.put("userMail",req.getParameter("userMail"));
                map.put("signUpDate",req.getParameter("signUpDate"));
                map.put("modifyDate",req.getParameter("modifyDate"));
                map.put("oldPermission",req.getParameter("oldPermission"));
                map.put("newPermission",req.getParameter("newPermission"));
                System.out.println("addPermissionModifyRecord:"+map);
                try {
                    sqlOp.addPermissionModifyRecord(map);
                    jsonObject.put("ok",200);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            }
            // 修改用户权限修改记录
            case "modifyPermissionModifyRecord":{
                HashMap<String,String> map=new HashMap<>();
                map.put("userAccount",req.getParameter("userAccount"));
                map.put("userMail",req.getParameter("userMail"));
                map.put("signUpDate",req.getParameter("signUpDate"));
                map.put("modifyDate",req.getParameter("modifyDate"));
                map.put("oldPermission",req.getParameter("oldPermission"));
                map.put("newPermission",req.getParameter("newPermission"));
                System.out.println("modifyPermissionModifyRecord"+map);
                try {
                    sqlOp.modifyPermissionModifyRecord(map,req.getParameter("id"));
                    jsonObject.put("ok",200);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            }
            // 删除用户权限修改记录
            case "deletePermissionModifyRecord":{
                String id=req.getParameter("id");
                try {
                    sqlOp.deletePermissionModifyRecord(id);
                    jsonObject.put("ok",200);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

            // 更换邮箱
            case "changeMail":{
                String way=req.getParameter("way");
                HashMap<String,String> map=new HashMap<>();
                if(way.equals("way1")){
                    map.put("account",req.getParameter("account"));
                    map.put("password",req.getParameter("password"));
                }else if(way.equals("way2")){
                    map.put("oldMail",req.getParameter("oldMail"));
                }
                map.put("newMail",req.getParameter("newMail"));
                try {
                    jsonObject=sqlOp.changeMail(way,map);

                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }

                break;
            }

            // 修改密码
            case "changePassword":{
                String way=req.getParameter("way");
                HashMap<String,String> map=new HashMap<>();
                if(way.equals("way1")){
                    map.put("mail",req.getParameter("mail"));
                }else if(way.equals("way2")){
                    map.put("account",req.getParameter("account"));
                    map.put("password",req.getParameter("password"));
                }
                map.put("resetPassword",req.getParameter("resetPassword"));
                try {
                    jsonObject=sqlOp.changePassword(way,map);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

            default:{
                break;
            }
        }

        try {
            // 关闭数据库链接
            sqlOp.CloseCn();
            responseBack(req,resp,jsonObject);
        } catch (JSONException | SQLException e) {
            System.out.println("回调失败！|| 数据库链接关闭失败!");
            e.printStackTrace();
        }
    }

    private void responseBack(HttpServletRequest request, HttpServletResponse response, JSONObject json) throws JSONException {
        boolean isAjax=true;if (request.getHeader("x-requested-with") == null || request.getHeader("x-requested-with").equals("com.tencent.mm")){isAjax=false;}	//判断是异步请求还是同步请求，腾讯的特殊
        if(isAjax){
            response.setContentType("application/json; charset=UTF-8");
            try {
                response.getWriter().print(json);
                response.getWriter().flush();
                response.getWriter().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            System.out.println("异步方式");
//            String action=json.getString("action");
//            String errorNo="0";
//            String errorMsg="ok";
//            String url = result.jsp?action="+action+"&result_code="+errorNo+ "&result_msg=" + errorMsg;
//            try {
//                response.sendRedirect(url);
//            } catch (IOException e) {
//                e.printStackTrace();
            }
        }


}
