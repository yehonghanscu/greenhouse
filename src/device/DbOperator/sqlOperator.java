package device.DbOperator;

import DbOperator.getDbConnection;
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

    // 以下是相关数据库的操作，包括增删改查
    // 判断用户是否可以登录成功
    public int isExist(String account, String password) throws SQLException {
        String sql="select * from accountInfo where account='"+account+"' and PASSWORD='"+password+"'";
        System.out.println("[sqlOperator/isExist()]将要执行sql:"+sql);
        ResultSet resultSet=statement.executeQuery(sql);
        if(resultSet.next()){
            return resultSet.getInt("userLevel");
        }else{
            sql="select * from accountInfo where mail='"+account+"' and PASSWORD='"+password+"'";
            System.out.println("SQL:"+sql);
            resultSet=statement.executeQuery(sql);
            if(resultSet.next()){
                return resultSet.getInt("userLevel");
            }else{
                return -1;
            }
        }
    }

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
            map.put("userLevel",resultSet.getString("userLevel"));
            map.put("userName",resultSet.getString("userName"));
            map.put("userGender",resultSet.getString("userGender"));
            map.put("signUpDate",resultSet.getDate("signUpDate"));
            map.put("lastLoginDate",resultSet.getDate("lastLoginDate"));
            map.put("lastModifyDate",resultSet.getDate("lastModifyDate"));
            map.put("isWorker",resultSet.getInt("isWorker"));
            jsonList.add(map);
        }
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("aaData",jsonList);
        return jsonObject;
    }



    public JSONObject getDeviceRecord(String userLevel, String account) throws SQLException, JSONException {
        String sql="select * from deviceInfo";
        if(userLevel.equals("1")){
            sql="select * from deviceInfo where deviceLocation in (select greenhouse_name from greenhouse_file where greenhouse_file.greenhouse_manager='"+account+"')";
        }
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
    public JSONObject getOwnFeedBackRecord(String account) throws SQLException, JSONException {
        String sql="select * from feedbackInfo where feedBackAccount='"+account+"'";
        System.out.println("[sqlOperator/getRecord()]将要执行SQL："+sql);
        ResultSet resultSet=this.statement.executeQuery(sql);
        // 获取查询到数据的列信息
        ResultSetMetaData resultSetMetaData=resultSet.getMetaData();
        int fieldCount=resultSetMetaData.getColumnCount();

        // 获取数据库中的内容
        List jsonList=new ArrayList();
        while (resultSet.next()){
            Map map=new HashMap();
            for(int i=0;i<fieldCount;i++){
                map.put(resultSetMetaData.getColumnName(i+1),resultSet.getString(resultSetMetaData.getColumnName(i+1)));
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


    // 专门用于导出设备信息表
    public JSONObject exportDeviceRecord(String account,String userLevel) throws SQLException, JSONException {
        String sql="select * from deviceInfo";
        if(userLevel.equals("1")){
            sql="select * from deviceInfo where deviceLocation in (select greenhouse_name from greenhouse_file where greenhouse_file.greenhouse_manager='"+account+"')";
        }
        System.out.println("[sqlOperator/exportDeviceRecord()]将要执行SQL："+sql);
        ResultSet resultSet=this.statement.executeQuery(sql);
        // 获取查询到数据的列信息
        ResultSetMetaData resultSetMetaData=resultSet.getMetaData();
        int fieldCount=resultSetMetaData.getColumnCount();

        // 获取数据库中的内容
        List jsonList=new ArrayList();
        while (resultSet.next()){
            Map map=new HashMap();
            for(int i=0;i<fieldCount;i++){
                map.put(resultSetMetaData.getColumnName(i+1),resultSet.getString(resultSetMetaData.getColumnName(i+1)));
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

    // 该函数用于普适性的获取记录信息，主要用于导出，查询，等情况
    public JSONObject getRecord(String name) throws SQLException, JSONException {
        String sql="select * from "+name;
        System.out.println("[sqlOperator/getRecord()]将要执行SQL："+sql);
        ResultSet resultSet=this.statement.executeQuery(sql);
        // 获取查询到数据的列信息
        ResultSetMetaData resultSetMetaData=resultSet.getMetaData();
        int fieldCount=resultSetMetaData.getColumnCount();

        // 获取数据库中的内容
        List jsonList=new ArrayList();
        while (resultSet.next()){
            Map map=new HashMap();
            for(int i=0;i<fieldCount;i++){
                map.put(resultSetMetaData.getColumnName(i+1),resultSet.getString(resultSetMetaData.getColumnName(i+1)));
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
            case "feedbackinfo":{
                sql+="id="+id;
                break;
            }
        }
        System.out.println("[sqlOperator/DeleteRecord]将要执行SQL:"+sql);
        this.statement.executeUpdate(sql);

    }


    public void addDeviceInfo(HashMap map) throws SQLException {
        System.out.println(map);
        String date=new SimpleDateFormat("yyyy-MM-dd hh-mm-ss").format(new Date());
        System.out.println(date);
        String sql="insert into deviceinfo(deviceNum,deviceName,deviceCompany,devicePrice,deviceLocation,deviceStatus,deviceModifyTime) values('"+map.get("deviceNum")+"','"+map.get("deviceName")+"','"+map.get("deviceCompany")+"',"+Double.parseDouble((String) map.get("devicePrice"))+",'"+map.get("deviceLocation")+"','"+map.get("deviceStatus")+"','"+date+"')";
        System.out.println(sql);
        this.statement.executeUpdate(sql);
    }

    public void ModifyDeviceInfo(HashMap map) throws SQLException {
        String sql="update deviceInfo set deviceNum='"+map.get("deviceNum")+"',deviceLocation='"+map.get("deviceLocation")+"',deviceStatus='"+map.get("deviceStatus")+"'";
        sql+=" where deviceId="+map.get("deviceId");
        System.out.println("[sqlOperator/ModifyRecord]：将要执行SQL:"+sql);
        this.statement.executeUpdate(sql);
    }

    public JSONObject StatisticsDeviceInfo() throws SQLException, JSONException {
        String sql="select count(*) as sumNum from deviceInfo";
        String sql1="select deviceName,count(*) as deviceSum from deviceInfo group by deviceName";
        String sql2="select deviceLocation,count(*) as deviceNum from deviceInfo group by deviceLocation";


        JSONObject json=new JSONObject();

        System.out.println("[sqlOperator/Static]将要执行:"+sql);
        ResultSet resultSet=this.statement.executeQuery(sql);
        resultSet.next();
        json.put("sumNum",resultSet.getInt("sumNum"));
        System.out.println(resultSet.getInt("sumNum"));

        System.out.println("[sqlOperator/Static]将要执行:"+sql1);
        resultSet=this.statement.executeQuery(sql1);
        json.put("温度计",0);
        json.put("湿度计",0);
        json.put("计时器",0);
        while (resultSet.next()){
            if(resultSet.getString("deviceName").equals("温度计")){
                json.put("温度计",resultSet.getInt("deviceSum"));
            }else if(resultSet.getString("deviceName").equals("湿度计")){
                json.put("湿度计",resultSet.getInt("deviceSum"));
            }else{
                json.put("计时器",resultSet.getInt("deviceSum"));
            }

        }

        System.out.println("[sqlOperator/Static]将要执行:"+sql2);
        json.put("一号大棚",0);
        json.put("二号大棚",0);
        resultSet=this.statement.executeQuery(sql2);
        while (resultSet.next()){
            if(resultSet.getString("deviceLocation").equals("1号大棚")){
                json.put("一号大棚",resultSet.getInt("deviceNum"));
            }else if(resultSet.getString("deviceLocation").equals("2号大棚")){
                json.put("二号大棚",resultSet.getInt("deviceNum"));
            }
        }
        return json;
    }

    public JSONObject getFeedbackRecord(String account,String tag) throws SQLException, JSONException {
        String sql="select * from feedbackinfo";
        if(tag.equals("1")){
            sql+=" where feedbackAccount='"+account+"'";
        }
        ResultSet resultSet=this.statement.executeQuery(sql);
        System.out.println("[sqlOperator/getFeedbackRecord()]将要执行SQL："+sql);
        List jsonList=new ArrayList();
        while (resultSet.next()){
            Map map=new HashMap();
            map.put("id",resultSet.getInt("id"));
            map.put("feedbackAccount",resultSet.getString("feedbackAccount"));
            map.put("feedBackContent",resultSet.getString("feedBackContent"));
            map.put("type",resultSet.getInt("type"));
            map.put("feedbackTime",resultSet.getTimestamp("feedbackTime"));
            map.put("isDealed",resultSet.getInt("isDealed"));
            map.put("dealResult",resultSet.getString("dealResult"));
            map.put("dealTime",resultSet.getString("dealTime"));
            map.put("dealManageAccount",resultSet.getString("dealManageAccount"));
            jsonList.add(map);
        }
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("aaData",jsonList);
        return jsonObject;
    }

    public void Submit(HashMap map) throws SQLException {
        String date=new SimpleDateFormat("yyyy-MM-dd hh-mm-ss").format(new Date());
        System.out.println(date);
        String sql="insert into feedbackinfo(feedBackContent,type,feedbackId,feedbackAccount,feedbackTime) values('"+map.get("feedBackContent")+"','"+map.get("type")+"','"+map.get("feedbackId")+"','"+map.get("feedbackAccount")+"','"+date+"')";
        System.out.println(sql);
        this.statement.executeUpdate(sql);
    }

    public JSONObject StatisticsFeedbackInfo() throws SQLException, JSONException {
        String sql="select count(*) as sumNum from feedbackinfo";
        String sql1="select type,count(*) as feedbackSum from feedbackinfo group by type";

        JSONObject json=new JSONObject();

        System.out.println("[sqlOperator/Static]将要执行:"+sql);
        ResultSet resultSet=this.statement.executeQuery(sql);
        resultSet.next();
        json.put("sumNum",resultSet.getInt("sumNum"));
        System.out.println(resultSet.getInt("sumNum"));

        System.out.println("[sqlOperator/Static]将要执行:"+sql1);
        resultSet=this.statement.executeQuery(sql1);
        json.put("系统信息反馈",0);
        json.put("设备信息反馈",0);
        json.put("用户信息反馈",0);
        while (resultSet.next()){
            if(resultSet.getString("type").equals("1")){
                json.put("系统信息反馈",resultSet.getInt("feedbackSum"));
            }else if(resultSet.getString("type").equals("2")){
                json.put("设备信息反馈",resultSet.getInt("feedbackSum"));
            }else{
                json.put("用户信息反馈",resultSet.getInt("feedbackSum"));
            }

        }

        return json;
    }

    // 用户修改反馈信息
    public void modifyFeedBack(String feedbackAccount, String content, String type, String id) throws SQLException {
        String sql="update feedbackInfo set feedBackContent='"+content+"', type="+Integer.parseInt(type)+" where feedbackAccount='"+feedbackAccount+"' and id="+id;
        System.out.println("[sqlOperator/modifyFeedBack]:sql:"+sql);
        this.statement.executeUpdate(sql);
    }

    // 管理员处理反馈信息
    public void dealFeedBack(String id,HashMap map) throws SQLException {
        String sql="update feedbackInfo set dealResult='"+map.get("dealResult")+"', dealManageId="+Integer.parseInt((String) map.get("dealManageId"))+", dealManageAccount='"+map.get("dealManageAccount")+"',";
        sql+="isDealed=1, dealTime='"+new SimpleDateFormat("yyyy-MM-dd hh-mm-ss").format(new Date())+"' where id="+Integer.parseInt(id);
        System.out.println("[sqlOperator/dealFeedBack]:sql:"+sql);
        this.statement.executeUpdate(sql);
    }

    public void saveWeather(HashMap map) throws SQLException {
        int flag=-1;
        String sql0="select id from weatherInfo where day='"+map.get("day")+"' and city='"+map.get("city")+"'";
        getDbConnection getDbConnection=new getDbConnection();
        Statement statement1=getDbConnection.getConnection();
        ResultSet resultSet=statement1.executeQuery(sql0);
        if(resultSet.next()){
            flag=resultSet.getInt("id");
        }
        statement1.close();
        String sql;
        if(flag>=1){
            sql="update weatherInfo set wea='"+map.get("wea")+"',hightem='"+map.get("hightem")+"',lowtem='"+map.get("lowtem")+"',airLevel='"+map.get("airLevel")+"',winSpeed='"+map.get("winSpeed")+"',humidity='"+map.get("humidity")+"' where id="+flag;
        }else{
            sql="insert into weatherInfo(city,day,week,wea,hightem,lowtem,airLevel,winSpeed,humidity) values('"+map.get("city")+"','"+map.get("day")+"'";
            sql+=",'"+map.get("week")+"','"+map.get("wea")+"','"+map.get("hightem")+"','"+map.get("lowtem")+"'";
            sql+=",'"+map.get("airLevel")+"','"+map.get("winSpeed")+"','"+map.get("humidity")+"')";
        }
        System.out.println("[sqlOperator/saveWeather]:sql:"+sql);
        this.statement.executeUpdate(sql);
    }

    public JSONObject getWeatherRecord() throws SQLException, JSONException {
        String sql="select * from weatherinfo order by day desc ";
        ResultSet resultSet=this.statement.executeQuery(sql);
        System.out.println("[sqlOperator/getWeatherRecord()]将要执行SQL："+sql);
        List jsonList=new ArrayList();
        while (resultSet.next()){
            Map map=new HashMap();
            map.put("id",resultSet.getInt("id"));
            map.put("city",resultSet.getString("city"));
            map.put("day",resultSet.getTimestamp("day"));
            map.put("week",resultSet.getString("week"));
            map.put("wea",resultSet.getString("wea"));
            map.put("hightem",resultSet.getString("hightem"));
            map.put("lowtem",resultSet.getString("lowtem"));
            map.put("airLevel",resultSet.getString("airLevel"));
            map.put("winSpeed",resultSet.getString("winSpeed"));
            map.put("humidity",resultSet.getString("humidity"));
            map.put("todoList",resultSet.getString("todoList"));
            jsonList.add(map);
        }
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("aaData",jsonList);
        return jsonObject;
    }

    public void SubmitModify(String id,HashMap map) throws SQLException {
        //System.out.println(date);
        String sql="update weatherInfo set todoList='"+map.get("todoList")+"'where id="+Integer.parseInt(id);
        System.out.println(sql);
        this.statement.executeUpdate(sql);
    }


    public void AddSubmit(String id,HashMap map) throws SQLException {
        String sql="update weatherInfo set todoList='"+map.get("todoList")+"'where id="+Integer.parseInt(id);
        System.out.println(sql);
        this.statement.executeUpdate(sql);
    }

    public void DeleteTodoList(String id,HashMap map) throws SQLException {
        //System.out.println(date);
        String sql="update weatherInfo set todoList=NULL where id="+Integer.parseInt(id);
        System.out.println(sql);
        this.statement.executeUpdate(sql);
    }

    public JSONObject  StatisticsWeatherInfo(String city) throws SQLException,JSONException{
        String sql="select hightem,lowtem,day from weatherinfo where city='"+city+"' order by day";

        JSONObject json=new JSONObject();

        System.out.println();
        ResultSet resultSet=this.statement.executeQuery(sql);
        ArrayList <String>hightem=new ArrayList<>();
        ArrayList <String>lowtem=new ArrayList<>();
        ArrayList <String>day=new ArrayList<>();
        while(resultSet.next()){
            hightem.add(resultSet.getString("hightem"));
            lowtem.add(resultSet.getString("lowtem"));
            day.add(resultSet.getString("day"));
        }
        json.put("hightem",hightem);
        json.put("lowtem",lowtem);
        json.put("day",day);
        return json;
    }

    public JSONObject getGreenHouse(String account,String userLevel) throws SQLException, JSONException {
        JSONObject json=new JSONObject();
        String sql;
        if(userLevel.equals("0")){
            sql="select greenhouse_name from greenhouse_file";
        }else{
            sql="select greenhouse_name from greenhouse_file where greenhouse_manager='"+account+"' order by greenhouse_id";
        }

        ArrayList<String> greenhouseName=new ArrayList<>();
        ResultSet resultSet=this.statement.executeQuery(sql);
        while (resultSet.next()){
            greenhouseName.add(resultSet.getString("greenhouse_name"));
        }
        json.put("greenhouse",greenhouseName);
        return json;
    }

    // 删除天气信息
    public void deleteWeatherInfo(String id) throws SQLException {
        String sql="delete from weatherInfo where id="+id;
        System.out.println("deleteWeatherInfo："+sql);
        this.statement.executeUpdate(sql);
    }

    public void modifyWeatherInfo(HashMap<String,String> map,int id) throws SQLException {
        String sql="update weatherInfo set city='"+map.get("city")+"', day='"+map.get("day")+"',hightem='"+map.get("hightem")+"',lowtem='"+map.get("lowtem")+"',winSpeed='"+map.get("winSpeed")+"',humidity='"+map.get("humidity")+"'";
        sql+=" where id="+id;
        System.out.println("将要执行"+sql);
        this.statement.executeUpdate(sql);
    }


    // 断开链接。
    public void CloseCn() throws SQLException {
        this.statement.close();
        System.out.println("数据库链接关闭！");
    }
}
