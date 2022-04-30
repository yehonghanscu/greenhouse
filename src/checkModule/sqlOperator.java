package checkModule;

import DbOperator.getDbConnection;
import org.json.JSONException;
import org.json.JSONObject;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class sqlOperator {

    private Statement statement;

    public sqlOperator(){
        getDbConnection dbConnection=new getDbConnection();
        this.statement=dbConnection.getConnection();
        System.out.println("准备数据库操作完毕!");
    }

    // 以下是相关数据库的操作，包括增删改查

    public JSONObject showCheckTable(String isOrdered) throws SQLException, JSONException {
        String sql;
        System.out.println("sqlOperator中isOrdered="+isOrdered);
        if(isOrdered.equals("true")){
            System.out.println("按打卡时间降序");
            sql="select * from check_file order by check_datetime desc ";
        }
        else{
            sql="select * from check_file";
        }

        ArrayList jsonList=new ArrayList();
        ArrayList jsonName=new ArrayList();
        try {
            ResultSet rs = statement.executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();
            int fieldCount = rsmd.getColumnCount();
            while (rs.next()) {
                Map map = new HashMap();
                for (int i = 0; i < fieldCount; i++) {
                    map.put(rsmd.getColumnName(i + 1), rs.getString(rsmd.getColumnName(i + 1)));
                }
                jsonList.add(map);
            }
            rs.close();
            //加表头信息
            for(int i=0;i<rsmd.getColumnCount();i++){
                String columLabel= rsmd.getColumnLabel(i+1);
                jsonName.add(columLabel);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[queryRecord]查询数据库出现错误：" + sql);
        }
        JSONObject json=new JSONObject();
        json.put("aaFieldName",jsonName);
        json.put("aaData",jsonList);
        return json;
    }

    public Boolean addCheckRecord(String employee_id,String employee_name,String employee_duty,String check_position,String check_remark,String whetherCheck) throws SQLException, JSONException {
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String sql="insert into check_file(employee_id,employee_name,employee_duty,check_position,check_remark,whetherCheck,check_datetime)";
        sql=sql+" values('"+employee_id+"'";
        sql=sql+" ,'"+employee_name+"'";
        sql=sql+" ,'"+employee_duty+"'";
        sql=sql+" ,'"+check_position+"'";
        sql=sql+" ,'"+check_remark+"'";
        sql=sql+" ,'"+whetherCheck+"'";
        sql=sql+" ,'"+ dateTime.format(formatter) +"')";
        try {
            statement.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[queryRecord]查询数据库出现错误：" + sql);
            return false;
        }
        return true;
    }

    public Boolean deleteCheckRecord(String check_id) throws SQLException, JSONException {
        String sql="delete from check_file where check_id="+check_id;

        try {
            statement.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[queryRecord]查询数据库出现错误：" + sql);
            return false;
        }
        return true;
    }

    public Boolean modifyCheckRecord(String check_id,String employee_id,String employee_name,String employee_duty,String check_position,String check_remark,String whetherCheck) throws SQLException, JSONException {
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String sql="update check_file set employee_id='"+employee_id+"',employee_name='"+employee_name+"',employee_duty='"+employee_duty+"',check_position='"+check_position+"',check_remark='"+check_remark+"',whetherCheck='"+whetherCheck+"',check_datetime='"+dateTime.format(formatter)+"'";
        sql+=" where check_id="+check_id+";";
        try {
            statement.executeUpdate(sql);
            System.out.println("成功执行sql语句："+sql);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[queryRecord]查询数据库出现错误：" + sql);
            return false;
        }
        return true;
    }

    public JSONObject queryCheckRecord(String employee_name,String check_position) throws SQLException, JSONException {
        String sql="select * from check_file where employee_name like '"+employee_name+"' or check_position='"+check_position+"';";
        ArrayList jsonList=new ArrayList();
        System.out.println("执行的sql语句为："+sql);

        try {
            ResultSet rs = statement.executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();
            int fieldCount = rsmd.getColumnCount();
            while (rs.next()) {
                Map map = new HashMap();
                for (int i = 0; i < fieldCount; i++) {
                    map.put(rsmd.getColumnName(i + 1), rs.getString(rsmd.getColumnName(i + 1)));
                }
                jsonList.add(map);
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[queryRecord]查询数据库出现错误：" + sql);
        }

        JSONObject json=new JSONObject();
        json.put("ok",200);
        json.put("aaData",jsonList);
        return json;
    }

    public JSONObject statisticCheckTable() throws SQLException, JSONException {
        String sql="select  count(*) as check_1 from check_file where whetherCheck = '是'";
        JSONObject json=new JSONObject();
        System.out.println("[sqlOperator/StatisticsGreenhouseSizeRecord]将要执行:"+sql);
        ResultSet resultSet=this.statement.executeQuery(sql);
        resultSet.next();
        json.put("check_1",resultSet.getInt("check_1"));

        String sql1="select  count(*) as check_2 from check_file where whetherCheck = '否'";
        resultSet=this.statement.executeQuery(sql1);
        resultSet.next();
        json.put("check_2",resultSet.getInt("check_2"));

        return json;
    }

    public void CloseCn() throws SQLException {
        this.statement.close();
        System.out.println("数据库链接关闭！");
    }

}
