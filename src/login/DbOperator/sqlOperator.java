package login.DbOperator;

import DbOperator.getDbConnection;
import com.company.sendCode;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;

public class sqlOperator {

    private Statement statement;

    public sqlOperator(){
        getDbConnection dbConnection=new getDbConnection();
        this.statement=dbConnection.getConnection();
        System.out.println("准备数据库操作完毕!");
    }

    // 展示函数运行信息
    private void showFunInfo(String funTitle, String sql){
        System.out.println("[sqlOperator/"+funTitle+"]即将执行:sql:   "+sql);
    }

    // 以下是相关数据库的操作，包括增删改查
    // 判断用户是否可以登录成功
    public JSONObject isExist(String account, String password) throws SQLException, JSONException {
        JSONObject json=new JSONObject();
        String sql="select * from accountInfo where account='"+account+"' and PASSWORD='"+password+"'";
        System.out.println("[sqlOperator/isExist()]将要执行sql:"+sql);
        ResultSet resultSet=statement.executeQuery(sql);
        if(resultSet.next()){
            json.put("userLevel",resultSet.getInt("userLevel"));
            json.put("account",resultSet.getString("account"));
            json.put("mail",resultSet.getString("mail"));
            json.put("userId",resultSet.getInt("userId"));
            json.put("ok",200);
        }else{
            sql="select * from accountInfo where mail='"+account+"' and PASSWORD='"+password+"'";
            System.out.println("SQL:"+sql);
            resultSet=statement.executeQuery(sql);
            if(resultSet.next()){
                json.put("userLevel",resultSet.getInt("userLevel"));
                json.put("account",resultSet.getString("account"));
                json.put("mail",resultSet.getString("mail"));
                json.put("userId",resultSet.getInt("userId"));
                json.put("ok",200);
            }else{
                json.put("ok",404);
            }
        }
        // 更新登录日期
        if(json.has("userId")){
            String date=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            System.out.println(date);
            String sql1="update accountInfo set lastLoginDate='"+date+"' where userId="+json.get("userId");
            System.out.println("[sqlOperator/isExist]:sql"+sql1);
            this.statement.executeUpdate(sql1);
        }

        System.out.println(json);
        return json;
    }

    // 用户注册
    public Boolean Register(String account, String password, String mail) throws SQLException {
        System.out.println("注册!");
        String date=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        System.out.println(date);
        String sql="insert into applyInfo (account, PASSWORD, mail, applyDate) VALUES('"+account+"','"+password+"','"+mail+"','"+date+"')";
        System.out.println("[sqlOperator/Register()]将要执行sql:"+sql);
        if(statement.executeUpdate(sql)>0){
            return true;
        }else{
            return false;
        }

    }

    public void logout(String userId) throws SQLException {
        String sql="delete from accountInfo where userId="+Integer.parseInt(userId);
        System.out.println("[sqlOperator/logout]:sql:"+sql);
        this.statement.executeUpdate(sql);
    }

    // 判断邮箱或用户名是否已注册或已申请注册
    public int isExisted(String content,String key) throws SQLException {
        String sql = null;
        String sql1= null;
        if(key.equals("mail")){
            sql="select * from accountInfo where mail='"+content+"'";
            sql1="select * from applyInfo where mail='"+content+"'";
        }else if(key.equals("account")){
            sql="select * from accountInfo where account='"+content+"'";
            sql1="select * from applyInfo where account='"+content+"'";
        }
        System.out.println("[sqlOperator/isExisted()]将要执行SQL:"+sql);
        ResultSet resultSet=statement.executeQuery(sql);
        if(resultSet.next()){
            // -1 说明用户账户或邮箱已经存在
            return -1;
        }else {
            System.out.println("SQL:"+sql1);
            resultSet=statement.executeQuery(sql1);
            if(resultSet.next()){
                // -2 说明用户账户或邮箱已经被申请注册，但尚未完成审核
                resultSet.close();
                return -2;
            }else{
                // 0表示用户账户或邮箱可以申请注册，合法
                resultSet.close();
                return 0;
            }
        }
    }

    // 后续可以优化
    public JSONObject getUserRecord(String sortWay) throws SQLException, JSONException {
        String sql="select * from accountInfo";
        // 定义升序或降序
        if(sortWay.equals("up")){
            sql+=" order by account";
        }else if(sortWay.equals("down")){
            sql+=" order by account desc";
        }

        ResultSet resultSet=this.statement.executeQuery(sql);
        System.out.println("[sqlOperator/getUserRecord()]执行SQL："+sql);
        List jsonList=new ArrayList();
        while (resultSet.next()){
            Map map=new HashMap();
            map.put("id",resultSet.getInt("userId"));
            map.put("account",resultSet.getString("account"));
            map.put("mail",resultSet.getString("mail"));
//            map.put("password",resultSet.getString("PASSWORD"));
            map.put("userLevel",resultSet.getString("userLevel"));
            map.put("userName",resultSet.getString("userName"));
            map.put("userGender",resultSet.getString("userGender"));
            map.put("signUpDate",resultSet.getDate("signUpDate"));
            map.put("lastLoginDate",resultSet.getDate("lastLoginDate"));
            map.put("lastModifyDate",resultSet.getDate("lastModifyDate"));
            map.put("isWorker",resultSet.getInt("isWorker"));
            System.out.println(map);
            jsonList.add(map);
        }
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("aaData",jsonList);
        jsonObject.put("ok",200);
        return jsonObject;
    }

    // 获取注册申请
    public JSONObject getApplyRecord(String sortWay) throws SQLException, JSONException {
        JSONObject json=new JSONObject();
        String sql="select * from applyInfo ";
        System.out.println("[sqlOperator/getApplyRecord]:sql"+sql);
        if(sortWay.equals("up")){
            sql+=" order by account";
        }else if(sortWay.equals("down")){
            sql+=" order by account desc";
        }
        ResultSet resultSet=this.statement.executeQuery(sql);
        ResultSetMetaData resultSetMetaData=resultSet.getMetaData();
        int fieldCount=resultSetMetaData.getColumnCount();
        List jsonList=new ArrayList();
        while (resultSet.next()){
            HashMap map=new HashMap();
            for(int i=0;i<fieldCount;i++){
                map.put(resultSetMetaData.getColumnName(i+1),resultSet.getString(resultSetMetaData.getColumnName(i+1)));
            }
            jsonList.add(map);
        }
        json.put("aaData",jsonList);
        resultSet.close();
        System.out.println(json);
        return json;
    }

    public JSONObject getDeviceRecord() throws SQLException, JSONException {
        String sql="select * from deviceInfo";
        ResultSet resultSet=this.statement.executeQuery(sql);
        System.out.println("[sqlOperator/getDeviceRecord()]将要执行SQL："+sql);
        List jsonList=new ArrayList();
        while (resultSet.next()){
            Map map=new HashMap();
            map.put("deviceId",resultSet.getInt("deviceId"));
            map.put("deviceNum",resultSet.getString("deviceNum"));
            map.put("deviceName",resultSet.getString("deviceName"));
            map.put("devicePrice",resultSet.getString("devicePrice"));
            map.put("deviceCompany",resultSet.getString("deviceCompany"));
            map.put("deviceLocation",resultSet.getString("deviceLocation"));
            map.put("deviceStatus",resultSet.getString("deviceStatus"));
            map.put("deviceModifyTime",resultSet.getTimestamp("deviceModifyTime"));
            map.put("lastLocation",resultSet.getString("lastLocation"));
            jsonList.add(map);
        }
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("aaData",jsonList);
        return jsonObject;
    }

    // 该函数用于普适性的获取记录信息，主要用于导出，查询，等情况
    public JSONObject getRecord(String name) throws SQLException, JSONException {
        String sql="select * from "+name;
        System.out.println("[sqlOperator/getRecord()]将要执行SQL："+sql);
        ResultSet resultSet=this.statement.executeQuery(sql);
        // 获取查询到数据的列信息
        ResultSetMetaData resultSetMetaData=resultSet.getMetaData();
        int fieldCount=resultSetMetaData.getColumnCount();

        // 获取数据库中的内容
        ArrayList<String> array=new ArrayList<>();
        array.add("系统管理员");
        array.add("普通管理员");
        array.add("普通用户");
        List jsonList=new ArrayList();
        while (resultSet.next()){
            Map map=new HashMap();
            for(int i=0;i<fieldCount;i++) {
                if (resultSetMetaData.getColumnName(i + 1).contains("Level") || resultSetMetaData.getColumnName(i + 1).contains("Permission")) {
                    // 处理数据库中的空值
                    if(resultSet.getString(String.valueOf(resultSetMetaData.getColumnName(i+1)))!=null){
                        map.put(resultSetMetaData.getColumnName(i + 1), array.get(resultSet.getInt(resultSetMetaData.getColumnName(i + 1))));
                    }else{
                        map.put(resultSetMetaData.getColumnName(i + 1), resultSet.getString(resultSetMetaData.getColumnName(i + 1)));
                    }
                } else {
                    map.put(resultSetMetaData.getColumnName(i + 1), resultSet.getString(resultSetMetaData.getColumnName(i + 1)));

                }
            }
            jsonList.add(map);
        }

        // 获取数据库中的属性，以作为表头
        List jsonName=new ArrayList();
        for(int i=0;i<fieldCount;i++){
            jsonName.add(resultSetMetaData.getColumnName(i+1));
        }
        JSONObject json=new JSONObject();
        json.put("record",jsonList);
        json.put("title",jsonName);
        resultSet.close();
        return json;
    }

    // 该函数可以用于记录删除，传入参数：表名；id
    public void DeleteRecord(String id,String tableName) throws SQLException {
        String sql="delete from "+tableName+" where ";
        // 获取删除的sql
        switch (tableName){
            case "accountInfo":{
                sql+="userId="+id;
                break;
            }
            case "deviceInfo":{
                sql+="deviceId="+id;
                break;
            }
            case "applyInfo":{
                sql+="id="+Integer.parseInt(id);
            }
        }
        System.out.println("[sqlOperator/DeleteRecord]将要执行SQL:"+sql);
        this.statement.executeUpdate(sql);

    }

    // 添加用户记录
    public void AddRecord(HashMap map) throws SQLException {

        // 指定属性插入
        String date=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String sql="insert into accountInfo(account,mail,password,userLevel,signUpDate,lastModifyDate,isWorker) values('"+map.get("account")+"','"+map.get("mail")+"','"+map.get("PASSWORD")+"','"+map.get("userLevel")+"','"+date+"','"+date+"',"+map.get("isWorker")+")";
        System.out.println("[sqlOperator/AddRecord]将要执行:"+sql);
        this.statement.executeUpdate(sql);
    }

    // 修改信息
    public void ModifyUserRecord(HashMap map) throws SQLException {
        String sql="update accountInfo set account='"+map.get("account")+"',mail='"+map.get("mail")+"',userLevel='"+map.get("userLevel")+"',userName='"+map.get("userName")+"',userGender='"+map.get("userGender")+"',isWorker='"+map.get("isWorker")+"',lastModifyDate='"+new SimpleDateFormat("yyyy-MM-dd").format(new Date())+"'";
        sql+=" where userId="+map.get("id");
        System.out.println("[sqlOperator/ModifyRecord]：将要执行SQL:"+sql);
        this.statement.executeUpdate(sql);
    }

    // 统计用户信息
    public JSONObject StatisticsUserRecord() throws SQLException, JSONException {
        String sql="select count(*) as sumNum from accountInfo";
        String sql1="select userLevel,count(*) as levelNum  from accountInfo group by userLevel";
        String sql2="select count(*) as applyNum from applyInfo";

        JSONObject json=new JSONObject();

        System.out.println("[sqlOperator/Static]将要执行:"+sql);
        ResultSet resultSet=this.statement.executeQuery(sql);
        resultSet.next();
        json.put("sumNum",resultSet.getInt("sumNum"));
        System.out.println(resultSet.getInt("sumNum"));

        System.out.println("[sqlOperator/Static]将要执行:"+sql1);
        resultSet=this.statement.executeQuery(sql1);
        json.put("manager",0);
        json.put("generalManager",0);
        json.put("generalUser",0);
        while (resultSet.next()){
            if(resultSet.getInt("userLevel")==0){
                json.put("manager",resultSet.getInt("levelNum"));
            }else if(resultSet.getInt("userLevel")==1){
                json.put("generalManager",resultSet.getInt("levelNum"));
            }else{
                json.put("generalUser",resultSet.getInt("levelNum"));
            }

        }

        System.out.println("[sqlOperator/Static]将要执行:"+sql2);
        resultSet=this.statement.executeQuery(sql2);
        resultSet.next();
        json.put("applyNum",resultSet.getInt("applyNum"));
        System.out.println(resultSet.getString("applyNum"));
        return json;
    }

    public void addApplyInfo(HashMap map) throws SQLException {
        // 获取日期
        String date=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String sql="insert into applyInfo(account,mail,PASSWORD,applyDate) values('"+map.get("account")+"','"+map.get("mail")+"','"+map.get("password")+"','"+date+"')";
        System.out.println("[sqlOperator/addApplyInfo]:SQL:"+sql);
        this.statement.executeUpdate(sql);
    }

    // 处理申请信息
    public void dealApply(String id, boolean All,String operator) throws SQLException {
        String sql="select * from applyInfo";
        if(!All){
            sql+=" where id="+Integer.parseInt(id);
        }
        System.out.println("[sqlOperator/dealApply]:sql:"+sql);
        // 保存查询结果
        // 一个statement同时只可执行一个sql，一个resultSet没有处理完毕时，不可执行另外一个sql
        getDbConnection db=new getDbConnection();
        ResultSet resultSet=db.getConnection().executeQuery(sql);
        ResultSetMetaData resultSetMetaData=resultSet.getMetaData();
        int fieldCount=resultSetMetaData.getColumnCount();
        while (resultSet.next()){
            HashMap map=new HashMap();
            for(int i=0;i<fieldCount;i++){
                map.put(resultSetMetaData.getColumnName(i+1),resultSet.getString(resultSetMetaData.getColumnName(i+1)));
            }
            // 判断是否添加到用户信息表
            if(operator.equals("agree")){
                map.put("userLevel",2);
                map.put("isWorker",0);
                AddRecord(map);
                sendCode send=new sendCode((String) map.get("mail"));
                send.sendApplyResult(operator,(String) map.get("account"),(String) map.get("applyDate"));
            }else{
                sendCode send=new sendCode((String) map.get("mail"));
                send.sendApplyResult(operator,(String) map.get("account"),(String) map.get("applyDate"));
            }
        }
        db.close();
        // 接下来删除applyInfo中的信息...
        sql="delete from applyInfo ";
        if(!All){
            sql+=" where id="+Integer.parseInt(id);
        }
        this.statement.executeUpdate(sql);
    }

    public void ModifyApplyRecord(int id,HashMap map) throws SQLException {
        String sql="update applyInfo set account='"+map.get("account")+"',mail='"+map.get("mail")+"',PASSWORD='"+map.get("password")+"',applyDate='"+map.get("applyDate")+"'";
        sql+=" where Id="+id;
        System.out.println("[sqlOperator/ModifyApplyRecord]:sql"+sql);
        this.statement.executeUpdate(sql);
    }

    // 查询注册信息
    public JSONObject queryApplyInfo(String queryCondition,String queryWay,HashMap map) throws SQLException, JSONException {
        JSONObject json=new JSONObject();
        String startDate= (String) map.get("queryStartDate");
        String endDate= (String) map.get("queryEndDate");
        // 生成查询条件
        String condition;
        String[] way=new String[3];
        String sql="select * from ApplyInfo where ";
        if(queryCondition.equals("与")){
            condition=" and ";
        }else{
            condition=" or ";
        }
        if(queryWay.equals("精准查询")){
            way[0]="=";
            way[1]="";
            way[2]="";
        }else{
            way[0]=" like ";
            way[1]="%";
            way[2]="%";
        }
        // 生成sql
        if(map.containsKey("account")){
            sql+="account"+way[0]+"'"+way[1]+map.get("account")+way[2]+"' ";
            if(map.containsKey("mail")){
                sql+=condition+" mail"+way[0]+"'"+way[1]+map.get("mail")+way[2]+"' ";
            }
            if(startDate!=null && endDate!=null){
                sql+=condition+" applyDate between '"+startDate+"' and '"+endDate+"'";
            }else if(startDate==null && endDate!=null){
                sql+=condition+" applyDate <'"+endDate+"'";
            }else if(startDate != null){
                sql+=condition+" applyDate >'"+startDate+"'";
            }
        }else if(map.containsKey("mail")){
            sql+=" mail"+way[0]+"'"+way[1]+map.get("mail")+way[2]+"' ";
            if(startDate!=null && endDate!=null){
                sql+=condition+" applyDate between '"+startDate+"' and '"+endDate+"'";
            }else if(startDate==null && endDate!=null){
                sql+=condition+" applyDate <'"+endDate+"'";
            }else if(startDate != null){
                sql+=condition+" applyDate >'"+startDate+"'";
            }
        }else{
            if(startDate!=null && endDate!=null){
                sql+=" applyDate between '"+startDate+"' and '"+endDate+"'";
            }else if(startDate==null && endDate!=null){
                sql+=" applyDate <'"+endDate+"'";
            }else if(startDate != null){
                sql+=" applyDate >'"+startDate+"'";
            }
        }
        System.out.println("[sqlOperator/queryApplyInfo]:sql"+sql);
        ResultSet resultSet=this.statement.executeQuery(sql);
        ResultSetMetaData resultSetMetaData=resultSet.getMetaData();
        int fieldCount=resultSetMetaData.getColumnCount();
        List jsonList=new ArrayList();
        while(resultSet.next()){
            HashMap newmap=new HashMap();
            for(int i=0;i<fieldCount;i++){
                newmap.put(resultSetMetaData.getColumnName(i+1),resultSet.getString(resultSetMetaData.getColumnName(i+1)));
            }
            jsonList.add(newmap);
        }
        json.put("aaData",jsonList);
        return json;
    }

    // 统计注册申请信息
    public JSONObject StatisticsApplyRecord() throws SQLException, JSONException {
        JSONObject json=new JSONObject();

        // 统计所有记录数量
        String sql="select count(*) as allApply from applyInfo";
        System.out.println("[sqlOperator/StatisticsApplyRecord]:sql"+sql);
        ResultSet resultSet=this.statement.executeQuery(sql);
        resultSet.next();
        json.put("applyCount",resultSet.getInt("allApply"));

        // 统计今日申请数量
        sql="select count(*) as todayApply from applyInfo where applyDate='"+new SimpleDateFormat("yyyy-MM-dd").format(new Date())+"'";
        System.out.println("[sqlOperator/StatisticApplyRecord]:sql"+sql);
        resultSet=this.statement.executeQuery(sql);
        resultSet.next();
        json.put("todayCount",resultSet.getInt("todayApply"));

        // 统计昨日申请数量
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.DATE,-1);
        Date tag=calendar.getTime();
        sql="select count(*) as dayApply from applyInfo where applyDate ='"+sdf.format(tag)+"'";
        System.out.println("[sqlOperator/StatisticApplyInfo]:sql"+sql);
        resultSet=this.statement.executeQuery(sql);
        resultSet.next();
        json.put("dayCount",resultSet.getInt("dayApply"));

        // 统计一周内申请数量
        Calendar calendar1=Calendar.getInstance();
        calendar1.add(Calendar.DATE,-7);
        tag=calendar1.getTime();
        sql="select count(*) as weekApply from applyInfo where applyDate between '"+sdf.format(tag)+"' and '"+new SimpleDateFormat("yyyy-MM-dd").format(new Date())+"'";
        System.out.println("sqlOperator/StatisticApplyInfo:sql"+sql);
        resultSet=this.statement.executeQuery(sql);
        resultSet.next();
        json.put("weekCount",resultSet.getInt("weekApply"));

        // 统计一个月内申请数量
        Calendar calendar2=Calendar.getInstance();
        calendar2.add(Calendar.DATE,-30);
        tag=calendar2.getTime();
        sql="select count(*) as monApply from applyInfo where applyDate between '"+sdf.format(tag)+"' and '"+new SimpleDateFormat("yyyy-MM-dd").format(new Date())+"'";
        System.out.println("sqlOperator/StatisticApplyInfo:sql"+sql);
        resultSet=this.statement.executeQuery(sql);
        resultSet.next();
        json.put("monCount",resultSet.getInt("monApply"));
        return json;
    }

    public JSONObject getPersonInfo(String userId) throws SQLException, JSONException {
        JSONObject json=new JSONObject();
        String sql="select * from accountInfo where userId='"+userId+"'";
        System.out.println("[sqlOperator/getApplyRecord]:sql"+sql);
        ResultSet resultSet=this.statement.executeQuery(sql);
        ResultSetMetaData resultSetMetaData=resultSet.getMetaData();
        int fieldCount=resultSetMetaData.getColumnCount();
        List jsonList=new ArrayList();
        while (resultSet.next()){
            HashMap map=new HashMap();
            for(int i=0;i<fieldCount;i++){
                map.put(resultSetMetaData.getColumnName(i+1),resultSet.getString(resultSetMetaData.getColumnName(i+1)));
            }
            jsonList.add(map);
        }
        json.put("Data",jsonList);
        resultSet.close();
        return json;
    }

    public boolean canModify(String userId,String account) throws SQLException {
        String sql="select * from accountInfo where userId!="+Integer.parseInt(userId)+" and account='"+account+"'";
        ResultSet resultSet=this.statement.executeQuery(sql);
        if(resultSet.next()){
            return false;
        }
        String sql1="select * from applyInfo where account='"+account+"'";
        ResultSet resultSet1=this.statement.executeQuery(sql1);
        return !resultSet1.next();
    }

    public void modifyPersonInfo(String userId, HashMap map) throws SQLException {
        String sql="update accountInfo set account='"+map.get("account")+"', userGender='"+map.get("userGender")+"', userName='"+map.get("userName")+"',lastModifyDate='"+new SimpleDateFormat("yyyy-MM-dd").format(new Date())+"' where userId="+ Integer.parseInt(userId);
        System.out.println("[sqlOperator/modifyPersonInfo]:sql"+sql);
        this.statement.executeUpdate(sql);
    }

    // 管理员修改用户权限
    public void modifyPermission(String userId,String userLevel) throws SQLException {
        String sql="select * from accountInfo where userId="+Integer.parseInt(userId);
        showFunInfo("modifyPermission",sql);
        ResultSet resultSet=this.statement.executeQuery(sql);
        ResultSetMetaData resultSetMetaData=resultSet.getMetaData();
        int fieldCount=resultSetMetaData.getColumnCount();
        HashMap map=new HashMap();
        while (resultSet.next()){
            for(int i=0;i<fieldCount;i++){
                map.put(resultSetMetaData.getColumnName(i+1),resultSet.getString(resultSetMetaData.getColumnName(i+1)));
            }
        }
        System.out.println(map);
        String sql1="update accountInfo set userLevel="+Integer.parseInt(userLevel)+" where userId="+Integer.parseInt(userId);
        showFunInfo("modifyPermission",sql1);
        this.statement.executeUpdate(sql1);
        // 将权限修改同步到权限修改记录中
        String sql2="insert into permissionModifyInfo(userAccount,userMail,signUpDate,oldPermission,newPermission,modifyDate) values('"+map.get("account")+"','"+map.get("mail")+"','"+map.get("signUpDate")+"','"+map.get("userLevel")+"','"+userLevel+"','"+new SimpleDateFormat("yyyy-MM-dd").format(new Date())+"')";
        showFunInfo("modifyPermission",sql2);
        this.statement.executeUpdate(sql2);
        // 修改用户信息表中的lastModifyDate
        String date=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        sql="update accountInfo set lastModifyDate='"+date+"' where userId="+userId ;
        showFunInfo("modifyPermission",sql);
        this.statement.executeUpdate(sql);
    }

    public JSONObject getPermissionModifyRecord() throws SQLException, JSONException {
        JSONObject json=new JSONObject();
        String sql="select * from permissionModifyInfo";
        ResultSet resultSet=this.statement.executeQuery(sql);
        ResultSetMetaData resultSetMetaData=resultSet.getMetaData();
        int count=resultSetMetaData.getColumnCount();
        List jsonList=new ArrayList();
        while (resultSet.next()){
            HashMap map=new HashMap();
            for(int i=0;i<count;i++){
                map.put(resultSetMetaData.getColumnName(i+1),resultSet.getString(resultSetMetaData.getColumnName(i+1)));
            }
            jsonList.add(map);
        }
        json.put("aaData",jsonList);
        return json;
    }

    // 统计用户权限信息
    public JSONObject staticPermissionInfo() throws SQLException, JSONException {
        JSONObject json=new JSONObject();
        String sql="select count(*) as SManager from accountInfo where userLevel=0";
        String sql1="select count(*) as CManager from accountInfo where userLevel=1";
        String sql2="select count(*) as user from accountInfo where userLevel=2";
        showFunInfo("staticPermissionInfo",sql);
        ResultSet resultSet=this.statement.executeQuery(sql);
        resultSet.next();
        json.put("SManager",resultSet.getInt("SManager"));

        showFunInfo("staticPermissionInfo",sql1);
        resultSet=this.statement.executeQuery(sql1);
        resultSet.next();
        json.put("CManager",resultSet.getInt("CManager"));

        showFunInfo("staticPermissionInfo",sql2);
        resultSet=this.statement.executeQuery(sql2);
        resultSet.next();
        json.put("user",resultSet.getInt("user"));
        return json;
    }

    // 导出用户权限信息
    public JSONObject exportPermissionInfo(String tag) throws SQLException, JSONException {
        JSONObject json=new JSONObject();
        String sql="select account,mail,userLevel,signUpDate,lastLoginDate,lastModifyDate from accountInfo";
        showFunInfo("exportPermissionInfo",sql);
        ResultSet resultSet=this.statement.executeQuery(sql);
        ResultSetMetaData resultSetMetaData=resultSet.getMetaData();
        int count=resultSetMetaData.getColumnCount();
        List jsonList=new ArrayList();
        ArrayList<String> array=new ArrayList<>();
        array.add("系统管理员");
        array.add("普通管理员");
        array.add("普通用户");
        while (resultSet.next()){
            HashMap map=new HashMap();
            for(int i=0;i<count;i++){
                if(resultSetMetaData.getColumnName(i+1).equals("userLevel")){
                    map.put(resultSetMetaData.getColumnName(i+1),array.get(resultSet.getInt(resultSetMetaData.getColumnName(i+1))));
                }else{
                    map.put(resultSetMetaData.getColumnName(i+1),resultSet.getString(resultSetMetaData.getColumnName(i+1)));
                }
            }
            jsonList.add(map);
        }
        json.put("record",jsonList);

        // 产生表头
        jsonList.clear();
        for(int i=0;i<count;i++){
            jsonList.add(resultSetMetaData.getColumnName(i+1));
        }
        json.put("title",jsonList);
        return json;
    }

    // 提交新的权限申请
    public JSONObject applyPermission(String userId,String account,String mail,int oldLevel,int applyLevel) throws SQLException, JSONException {
        JSONObject json=new JSONObject();
        // 查询是否存在提交的权限修改申请
        String sql="select isDealed from applyPermissionInfo where applyUserId="+userId;
        System.out.println("[sqlOperator/applyPermission]:sql"+sql);
        ResultSet resultSet=this.statement.executeQuery(sql);
        // 如果存在申请记录
        if(resultSet.next()){
            // 如果申请没有被处理
            if(resultSet.getString("isDealed").equals("no")){
                json.put("ok",500);
                return json;
            }else{
                // 申请已经被处理
                //  提交新的申请，需要删除旧的申请记录
                sql="delete from applyPermissionInfo where applyUserId="+userId;
                System.out.println("[sqlOperator/applyPermission]:sql"+sql);
                this.statement.executeUpdate(sql);
            }
        }
        // 处理之后，将新的权限申请记录插入
        sql="insert into applyPermissionInfo(applyUserId,applyAccount,mail,oldLevel,applyLevel,applyDate)";
        sql+=" values("+userId+",'"+account+"','"+mail+"',"+oldLevel+","+applyLevel+",'"+new SimpleDateFormat("yyyy-MM-dd").format(new Date())+"')";
        System.out.println("[sqlOperator/applyPermission]:sql:"+sql);
        this.statement.executeUpdate(sql);
        json.put("ok",200);
        return json;
    }

    // 撤销权限申请
    public JSONObject cancelPermissionApply(String applyUserId,String flag) throws SQLException, JSONException {
        JSONObject json=new JSONObject();
        String sql="select isDealed from applyPermissionInfo where applyUserId="+applyUserId;
        ResultSet resultSet=this.statement.executeQuery(sql);
        // 如果存在权限申请
        if(resultSet.next()){
            String isDealed=resultSet.getString("isDealed");
            if(isDealed.equals("no")){
                sql="delete from applyPermissionInfo where applyUserId="+applyUserId;
                this.statement.executeUpdate(sql);
                json.put("ok",200);
            }else if(isDealed.equals("yes")){
                // 如果用户确认，那么也将删除该权限申请记录
                if(flag.equals("sure")){
                    sql="delete from applyPermissionInfo where applyUserId="+applyUserId;
                    this.statement.executeUpdate(sql);
                    json.put("ok",200);
                }else{
                    json.put("ok",500);
                }
            }
        }else{  // 不存在权限申请
            json.put("ok",404);
        }
        return json;
    }

    // 获取权限申请信息
    public JSONObject getApplyPermissionRecord(String applyUserId, String type, String size) throws SQLException, JSONException {
        JSONObject json=new JSONObject();
        String sql="select * from applyPermissionInfo";
        // 判断是否为个人查看自己的申请
        if(type.equals("person")){
            sql+=" where applyUserId="+applyUserId;
        }else{
            switch (size){
                case "2":{
                    sql+=" where isDealed='yes'";
                    break;
                }
                case "3":{
                    sql+=" where isDealed='no'";
                    break;
                }
                default:{
                    break;
                }
            }
        }
        System.out.println("[sqlOperator/getApplyPermissionRecord]:sql"+sql);
        ResultSet resultSet=this.statement.executeQuery(sql);
        ResultSetMetaData resultSetMetaData=resultSet.getMetaData();
        int count=resultSetMetaData.getColumnCount();
        List jsonList=new ArrayList();
        while (resultSet.next()){
            HashMap map=new HashMap();
            for(int i=0;i<count;i++){
                map.put(resultSetMetaData.getColumnName(i+1),resultSet.getString(resultSetMetaData.getColumnName(i+1)));
            }
            jsonList.add(map);
        }
        json.put("aaData",jsonList);
        return json;
    }

    // 管理员操作用户权限申请(尚未完成!)
    public JSONObject dealPermissionApply(String operator, HashMap<String,String> map) throws SQLException, JSONException {
        // 允许申请需要对权限修改记录，用户权限等进行同步操作
        JSONObject json=new JSONObject();
        String sql="update applyPermissionInfo set isDealed='yes', dealManageAccount='"+map.get("dealManageAccount")+"', dealResult='"+operator+"', dealManageLevel="+map.get("dealManageLevel")+", dealDate='"+new SimpleDateFormat("yyyy-MM-dd").format(new Date())+"'";
        sql+=" where applyUserId="+map.get("applyUserId");
        showFunInfo("dealPermissionApply",sql);
        this.statement.executeUpdate(sql);
        if(operator.equals("允许申请")){
            String sql1="select * from accountInfo where userId="+map.get("applyUserId");
            showFunInfo("dealPermissionApply",sql1);
            ResultSet resultSet=this.statement.executeQuery(sql1);
            resultSet.next();

            // 同步到权限修改记录
            String sql2="insert into permissionModifyInfo(userAccount,userMail,signUpDate,modifyDate,oldPermission,newPermission) values('"+resultSet.getString("account")+"','"+resultSet.getString("mail")+"','"+resultSet.getString("signUpDate")+"','"+resultSet.getString("lastModifyDate")+"','"+resultSet.getInt("userLevel")+"','"+map.get("applyLevel")+"')";
            showFunInfo("dealPermissionApply",sql2);
            this.statement.executeUpdate(sql2);

            // 同步到用户信息表
            String sql3="update accountInfo set userLevel="+map.get("applyLevel")+" where userId="+map.get("applyUserId");
            showFunInfo("dealPermissionApply",sql3);
            this.statement.executeUpdate(sql3);
            json.put("ok",200);

        }else if(operator.equals("拒绝申请")){
            json.put("ok",200);
        }
        return json;
    }

    // 修改用户权限申请信息
    public void modifyApplyPermissionRecord(HashMap<String, String> map,String applyUserId, String isDealed) throws SQLException {
        String sql="update applyPermissionInfo set applyDate='"+map.get("applyDate")+"',applyLevel="+map.get("applyLevel")+",oldLevel="+map.get("oldLevel");
        if(isDealed.equals("yes")){
            sql+=",dealDate='"+map.get("dealDate")+"',isDealed='yes'";
        }else{
            sql+=",isDealed='no',dealDate=NULL,dealManageAccount='尚未处理',dealManageLevel=NULL,dealResult='尚未处理'";
        }
        sql+=" where applyUserId="+applyUserId;
        showFunInfo("modifyApplyPermissionRecord",sql);
        this.statement.executeUpdate(sql);
    }

    // 统计用户权限申请信息
    public JSONObject statisticApplyPermission() throws SQLException, JSONException {
        JSONObject json=new JSONObject();
        // 统计总的申请量
        String sql="select count(*) as applicationNum from applyPermissionInfo";
        showFunInfo("statisticApplyPermission", sql);
        ResultSet resultSet=this.statement.executeQuery(sql);
        resultSet.next();
        json.put("applicationNum",resultSet.getInt("applicationNum"));

        sql="select count(*) as SManagerApplication from applyPermissionInfo where applyLevel=0";
        showFunInfo("statisticApplyPermission",sql);
        resultSet=this.statement.executeQuery(sql);
        resultSet.next();
        json.put("SManagerApplication",resultSet.getInt("SManagerApplication"));

        sql="select count(*) as CManagerApplication from applyPermissionInfo where applyLevel=1";
        showFunInfo("statisticApplyPermission",sql);
        resultSet=this.statement.executeQuery(sql);
        resultSet.next();
        json.put("CManagerApplication", resultSet.getInt("CManagerApplication"));

        sql="select count(*) as user from applyPermissionInfo where applyLevel=2";
        showFunInfo("statisticApplyPermission",sql);
        resultSet=this.statement.executeQuery(sql);
        resultSet.next();
        json.put("user",resultSet.getInt("user"));

        sql="select count(*) as SManagerApply from applyPermissionInfo where oldLevel=0";
        showFunInfo("statisticApplyPermission",sql);
        resultSet=this.statement.executeQuery(sql);
        resultSet.next();
        json.put("SManagerApply",resultSet.getInt("SManagerApply"));

        sql="select count(*) as CManagerApply from applyPermissionInfo where oldLevel=1";
        showFunInfo("statisticApplyPermission",sql);
        resultSet=this.statement.executeQuery(sql);
        resultSet.next();
        json.put("CManagerApply",resultSet.getInt("CManagerApply"));

        sql="select count(*) as userApply from applyPermissionInfo where oldLevel=2";
        showFunInfo("statisticApplyPermission",sql);
        resultSet=this.statement.executeQuery(sql);
        resultSet.next();
        json.put("userApply",resultSet.getInt("userApply"));

        return json;
    }

    // 统计用户权限修改信息
    public JSONObject staticPermissionModifyInfo() throws SQLException, JSONException {
        JSONObject json=new JSONObject();

        String sql="select count(*) as modifyNum from permissionModifyInfo";
        showFunInfo("statisticPermissionModifyInfo",sql);
        ResultSet resultSet=this.statement.executeQuery(sql);
        resultSet.next();
        json.put("modifyNum", resultSet.getInt("modifyNum"));

        sql="select count(*) as SManagerNum from permissionModifyInfo where oldPermission=0";
        showFunInfo("statisticPermissionModifyInfo",sql);
        resultSet=this.statement.executeQuery(sql);
        resultSet.next();
        json.put("SManagerNum", resultSet.getInt("SManagerNum"));

        sql="select count(*) as CManagerNum from permissionModifyInfo where oldPermission=1";
        showFunInfo("statisticPermissionModifyInfo",sql);
        resultSet=this.statement.executeQuery(sql);
        resultSet.next();
        json.put("CManagerNum", resultSet.getInt("CManagerNum"));

        sql="select count(*) as UserNum from permissionModifyInfo where oldPermission=2";
        showFunInfo("statisticPermissionModifyInfo",sql);
        resultSet=this.statement.executeQuery(sql);
        resultSet.next();
        json.put("UserNum", resultSet.getInt("UserNum"));

        sql="select count(*) as NewSManager from permissionModifyInfo where newPermission=0";
        showFunInfo("statisticPermissionModifyInfo",sql);
        resultSet=this.statement.executeQuery(sql);
        resultSet.next();
        json.put("NewSManager", resultSet.getInt("NewSManager"));

        sql="select count(*) as NewCManager from permissionModifyInfo where oldPermission=1";
        showFunInfo("statisticPermissionModifyInfo",sql);
        resultSet=this.statement.executeQuery(sql);
        resultSet.next();
        json.put("NewCManager", resultSet.getInt("NewCManager"));

        sql="select count(*) as newUser from permissionModifyInfo where oldPermission=2";
        showFunInfo("statisticPermissionModifyInfo",sql);
        resultSet=this.statement.executeQuery(sql);
        resultSet.next();
        json.put("newUser", resultSet.getInt("newUser"));

        return json;
    }
    // 添加用户权限修改信息
    public void addPermissionModifyRecord(HashMap<String,String> map) throws SQLException {
        String sql="insert into permissionModifyInfo(userAccount,userMail,signUpDate,modifyDate,oldPermission,newPermission) values('"+map.get("userAccount")+"','"+map.get("userMail")+"','"+map.get("signUpDate")+"','"+map.get("modifyDate")+"','"+map.get("oldPermission")+"','"+map.get("newPermission")+"')";
        showFunInfo("addPermissionModifyRecord",sql);
        this.statement.executeUpdate(sql);
    }

    // 删除用户权限修改记录
    public void deletePermissionModifyRecord(String id) throws SQLException {
        String sql="delete from PermissionModifyInfo where id="+id;
        showFunInfo("deletePermissionModifyRecord",sql);
        this.statement.executeUpdate(sql);
    }

    // 修改用户权限修改记录
    public void modifyPermissionModifyRecord(HashMap<String,String> map,String id) throws SQLException {
        String sql="update PermissionModifyInfo set userAccount='"+map.get("userAccount")+"',userMail='"+map.get("userMail")+"',signUpDate='"+map.get("signUpDate")+"',modifyDate='"+map.get("modifyDate")+"',oldPermission="+map.get("oldPermission")+",newPermission="+map.get("newPermission");
        sql+=" where id="+id;
        showFunInfo("modifyPermissionModifyRecord",sql);
        this.statement.executeUpdate(sql);

    }

    // 更换邮箱
    public JSONObject changeMail(String way,HashMap<String, String> map) throws SQLException, JSONException {
        JSONObject json=new JSONObject();
        String sql="select userId from accountInfo where";
        if(way.equals("way1")){
            sql+=" account='"+map.get("account")+"' and password='"+map.get("password")+"'";
        }else if(way.equals("way2")){
            sql+=" mail='"+map.get("oldMail")+"'";
        }
        showFunInfo("changeMail",sql);
        ResultSet resultSet=this.statement.executeQuery(sql);
        int userId;
        if(resultSet.next()){
            userId=resultSet.getInt("userId");
            String sql1="update accountInfo set mail='"+map.get("newMail")+"' where userId="+userId;
            showFunInfo("changeMail",sql1);
            this.statement.executeUpdate(sql1);
            json.put("ok",200);
        }else{
            json.put("ok",404);
        }
        return json;
    }

    // 更换密码
    public JSONObject changePassword(String way,HashMap<String,String> map) throws SQLException, JSONException {
        JSONObject json=new JSONObject();
        String sql="select userId from accountInfo where ";
        if(way.equals("way1")){
            sql+=" mail='"+map.get("mail")+"'";
        }else if(way.equals("way2")){
            sql+=" (account='"+map.get("account")+"' or mail='"+map.get("account")+"') and PASSWORD='"+map.get("password")+"'";
        }
        showFunInfo("changePassword",sql);
        ResultSet resultSet=this.statement.executeQuery(sql);
        if(resultSet.next()){
            int userId=resultSet.getInt("userId");
            String sql1="update accountInfo set password='"+map.get("resetPassword")+"' where userId="+userId;
            showFunInfo("changePassword",sql1);
            this.statement.executeUpdate(sql1);
            json.put("ok",200);
        }else {
            json.put("ok",404);
        }
        return json;
    }


    // 断开链接。
    public void CloseCn() throws SQLException {
        this.statement.close();
        System.out.println("数据库链接关闭！");
    }
}
