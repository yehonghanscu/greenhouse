package cropModule.file;


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
//        super.doGet(req, resp);
        System.out.println("执行doGet!");

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        super.doPost(req, resp);
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

            // 此增加功能进行数据的增加-------------------------------------------------------------------------------
            case "add_crop_record":{
                String crop_name=req.getParameter("crop_name");
                String greenhouse_id=req.getParameter("greenhouse_id");
                String crop_number=req.getParameter("crop_number");
                String crop_status=req.getParameter("crop_status");
                String crop_remark=req.getParameter("crop_remark");
                String crop_datetime=req.getParameter("crop_datetime");
                HashMap map=new HashMap();
                map.put("crop_name",crop_name);
                map.put("greenhouse_id",greenhouse_id);
                map.put("crop_number",crop_number);
                map.put("crop_status",crop_status);
                map.put("crop_remark",crop_remark);
                map.put("crop_datetime",crop_datetime);
                try {
                    sqlOp.AddCropRecord(map);
                    jsonObject.put("ok",200);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

            // 此delete可以对不同的数据表进行适配----------------------------------------------------------------------------
            case "delete_crop_record":{
                String id=req.getParameter("id");
                String dbName=req.getParameter("tableName");
                System.out.println("ID:"+id+"  Dbname:"+dbName);
                try {
                    sqlOp.DeleteCropRecord(id,dbName);
                    jsonObject.put("ok",200);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

            //修改-------------------------------------------------------------------------------------------------------
            case "modify_crop_record":{
                String id=req.getParameter("id");
                String crop_name=req.getParameter("crop_name");
                String crop_number=req.getParameter("crop_number");
                String crop_status=req.getParameter("crop_status");
                String crop_remark=req.getParameter("crop_remark");
                String crop_datetime=req.getParameter("crop_datetime");
                HashMap map=new HashMap();
                map.put("id",id);
                map.put("crop_name",crop_name);
                map.put("crop_number",crop_number);
                map.put("crop_status",crop_status);
                map.put("crop_remark",crop_remark);
                map.put("crop_datetime",crop_datetime);
                try {
                    sqlOp.ModifyCropRecord(map);
                    jsonObject.put("ok",200);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

            //统计大棚数据表-----------------------------------------------------------------------------------------------
            case "statistics_crop_record":{
                String value=req.getParameter("value");
                System.out.println(value);
                try {
                    jsonObject=sqlOp.StatisticsCropRecord(value);
                    jsonObject.put("ok",200);
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
            }

            // 获取大棚信息，用于构成DataTable------------------------------------------------------------------------------
            case "getcropRecordList":{
                String value=req.getParameter("value");
                System.out.println(value);
                try {
                    jsonObject=sqlOp.getcropRecordList(value);
                    jsonObject.put("ok",200);
                } catch (SQLException | JSONException e) {
                    System.out.println("获取数据失败!");
                    System.out.println(e.toString());
                    e.printStackTrace();
                }
                break;
            }

            // 导出文件--------------------------------------------------------------------------------------------------
            case "exportFile":{
                // 该json用于保存导出的数据
                JSONObject json=new JSONObject();
                System.out.println("正在导出");
                // 获取需要操作的表名
                String name=req.getParameter("tableName");
                System.out.println("将要操作的表:"+name);
                try {
                    // 获取到查询结果的json
                    json=sqlOp.getRecord(name);

                    // JSON导出文件的工具类对象
                    JsonToFile jsonToFile=new JsonToFile();
                    jsonObject=jsonToFile.setJsonTOTxt(json);

                    // 转excel
                    jsonToFile.setJsonToExcel(json,jsonObject);
                    System.out.println(jsonObject);
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

    //回显---------------------------------------------------------------------------------------------------------------
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

        }
    }


}
