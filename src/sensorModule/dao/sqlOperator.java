package sensorModule.dao;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import DbOperator.getDbConnection;
import org.json.JSONException;
import org.json.JSONObject;

public class sqlOperator {
    private Statement statement;

    public sqlOperator() {
        getDbConnection dbConnection = new getDbConnection();
        this.statement = dbConnection.getConnection();
        System.out.println("准备数据库操作完毕!");
    }

    public JSONObject showWarningTable(String isOrdered) throws SQLException, JSONException {
        System.out.println("sqlOperator中isOrdered=" + isOrdered);
        String sql;
        if (isOrdered.equals("true")) {
            System.out.println("按温度排序");
            sql = "select * from sensor_file order by temperature";
        } else {
            System.out.println("按湿度排序");
            sql = "select * from sensor_file order by humidity";
        }

        ArrayList jsonList = new ArrayList();
        ArrayList jsonName = new ArrayList();

        try {
            ResultSet rs = this.statement.executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();
            int fieldCount = rsmd.getColumnCount();

            while(rs.next()) {
                Map map = new HashMap();

                for(int i = 0; i < fieldCount; ++i) {
                    map.put(rsmd.getColumnName(i + 1), rs.getString(rsmd.getColumnName(i + 1)));
                }

                jsonList.add(map);
            }

            rs.close();

            for(int i = 0; i < rsmd.getColumnCount(); ++i) {
                String columLabel = rsmd.getColumnLabel(i + 1);
                jsonName.add(columLabel);
            }
        } catch (Exception var10) {
            var10.printStackTrace();
            System.out.println("[queryRecord]查询数据库出现错误：" + sql);
        }

        JSONObject json = new JSONObject();
        json.put("aaFieldName", jsonName);
        json.put("aaData", jsonList);
        return json;
    }

    public Boolean deleteWarningRecord(String warningId) throws SQLException, JSONException {
        String sql = "delete from sensor_file where id=" + warningId;

        try {
            this.statement.executeUpdate(sql);
        } catch (Exception var4) {
            var4.printStackTrace();
            System.out.println("[queryRecord]查询数据库出现错误：" + sql);
            return false;
        }

        return true;
    }

    public Boolean modifyWarningRecord(String warningRecord, int warningId) throws SQLException, JSONException {
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String sql = "update sensor_file set create_device='" + warningRecord + "'";
        sql = sql + " where id=" + warningId + ";";

        try {
            this.statement.executeUpdate(sql);
            System.out.println("成功执行sql语句：" + sql);
        } catch (Exception var7) {
            var7.printStackTrace();
            System.out.println("[queryRecord]查询数据库出现错误：" + sql);
            return false;
        }

        return true;
    }

    public JSONObject statisticWarningTable() throws SQLException, JSONException {
        String sql = "select greenhouse_id,count(*) as warning_num from warning_file group by greenhouse_id;";
        ArrayList jsonList = new ArrayList();

        try {
            ResultSet rs = this.statement.executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();
            int fieldCount = rsmd.getColumnCount();

            while(rs.next()) {
                Map map = new HashMap();

                for(int i = 0; i < fieldCount; ++i) {
                    map.put(rsmd.getColumnName(i + 1), rs.getString(rsmd.getColumnName(i + 1)));
                }

                jsonList.add(map);
            }

            rs.close();
        } catch (Exception var8) {
            var8.printStackTrace();
            System.out.println("[queryRecord]查询数据库出现错误：" + sql);
        }

        JSONObject json = new JSONObject();
        json.put("aaData", jsonList);
        json.put("ok", 200);
        return json;
    }

    public JSONObject queryWarningRecord(String warningRecord) throws SQLException, JSONException {
        String sql = "select * from sensor_file where create_device='" + warningRecord + "';";
        ArrayList jsonList = new ArrayList();
        System.out.println("执行的sql语句为：" + sql);

        try {
            ResultSet rs = this.statement.executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();
            int fieldCount = rsmd.getColumnCount();

            while(rs.next()) {
                Map map = new HashMap();

                for(int i = 0; i < fieldCount; ++i) {
                    map.put(rsmd.getColumnName(i + 1), rs.getString(rsmd.getColumnName(i + 1)));
                }

                jsonList.add(map);
            }

            rs.close();
        } catch (Exception var9) {
            var9.printStackTrace();
            System.out.println("[queryRecord]查询数据库出现错误：" + sql);
        }

        JSONObject json = new JSONObject();
        json.put("ok", 200);
        json.put("aaData", jsonList);
        return json;
    }

    public JSONObject visInfoGet(String sensorName) throws JSONException {
        String sql = "select avg(temperature),avg(humidity) from sensor_file where create_device='" + sensorName + "';";
        ArrayList jsonList = new ArrayList();
        System.out.println("执行的sql语句为：" + sql);

        try {
            ResultSet rs = this.statement.executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();
            int fieldCount = rsmd.getColumnCount();

            while(rs.next()) {
                Map map = new HashMap();

                for(int i = 0; i < fieldCount; ++i) {
                    map.put(rsmd.getColumnName(i + 1), rs.getString(rsmd.getColumnName(i + 1)));
                }

                jsonList.add(map);
            }

            rs.close();
        } catch (Exception var9) {
            var9.printStackTrace();
            System.out.println("[queryRecord]查询数据库出现错误：" + sql);
        }

        JSONObject json = new JSONObject();
        json.put("ok", 200);
        json.put("aaData", jsonList);
        return json;
    }
}
