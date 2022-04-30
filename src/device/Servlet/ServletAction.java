package device.Servlet;

import device.DbOperator.sqlOperator;
import device.export.JsonToFile;
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
        System.out.println(action);
        switch (action){
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
            case "device":{
                String account=req.getParameter("account");
                String password=req.getParameter("password");
                try {
                    int level= sqlOp.isExist(account,password);
                    if(level>=0){
                        jsonObject.put("level",level);
                        jsonObject.put("ok",200);
                        jsonObject.put("account",account);
                    }else{
                        jsonObject.put("ok",404);
                    }
//                    responseBack(req,resp,jsonObject);
                } catch (SQLException | JSONException e) {
                    System.out.println("数据库查询出现异常！");
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
                    // 获取到查询结果的json
                    json=sqlOp.getRecord(name);

                    // JSON导出文件的工具类对象
                    JsonToFile jsonToFile=new JsonToFile(req.getParameter("tag"));
                    jsonObject=jsonToFile.setJsonTOTxt(json);

                    // 转excel
                    jsonToFile.setJsonToExcel(json,jsonObject);
                    jsonToFile.setExcelToPdfCsv(jsonObject);
                    System.out.println(jsonObject);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

            // 获取设备信息
            case "getDeviceRecord":{
                String userLevel=req.getParameter("userLevel");
                String account=req.getParameter("account");
                try {
                    jsonObject=sqlOp.getDeviceRecord(userLevel,account);
                } catch (JSONException | SQLException e) {
                    e.printStackTrace();
                }
                break;
            }

            // 导出设备信息
            case "exportDeviceRecord":{
                JSONObject json=new JSONObject();
                String userLevel=req.getParameter("userLevel");
                String account=req.getParameter("account");
                try {
                    json=sqlOp.exportDeviceRecord(account,userLevel);
                    // 导出txt
                    JsonToFile jsonToFile=new JsonToFile(req.getParameter("tag"));
                    jsonObject=jsonToFile.setJsonTOTxt(json);
                    // 转excel
                    jsonToFile.setJsonToExcel(json,jsonObject);
                    jsonToFile.setExcelToPdfCsv(jsonObject);
                    System.out.println(jsonObject);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

            case "addDeviceInfo":{
                HashMap map=new HashMap();
                map.put("deviceNum",req.getParameter("deviceNum"));
                map.put("deviceName",req.getParameter("deviceName"));
                map.put("deviceCompany",req.getParameter("deviceCompany"));
                map.put("devicePrice",req.getParameter("devicePrice"));
                map.put("deviceLocation",req.getParameter("deviceLocation"));
                map.put("deviceStatus",req.getParameter("deviceStatus"));
                try {
                    sqlOp.addDeviceInfo(map);
                    jsonObject.put("ok",200);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

            case "deleteInfo":{
                String tablename=req.getParameter("tableName");
                String id=req.getParameter("id");
                try {
                    sqlOp.DeleteRecord(id,tablename);
                    jsonObject.put("ok",200);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

            case "modifyDeviceInfo":{
                HashMap map=new HashMap();
                map.put("deviceNum",req.getParameter("deviceNum"));
                map.put("deviceLocation",req.getParameter("deviceLocation"));
                map.put("deviceStatus",req.getParameter("deviceStatus"));
                map.put("deviceId",req.getParameter("id"));
                try {
                    sqlOp.ModifyDeviceInfo(map);
                    jsonObject.put("ok",200);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }

                break;
            }

            case "StatisticsDeviceInfo":{
                try {
                    jsonObject=sqlOp.StatisticsDeviceInfo();
                    jsonObject.put("ok",200);
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
            }

            // 获取设备信息
            case "getFeedbackRecord":{
                try {
                    String account=req.getParameter("account");
                    String tag=req.getParameter("tag");
                    System.out.println(account+","+tag);
                    jsonObject=sqlOp.getFeedbackRecord(account,tag);
                } catch (JSONException | SQLException e) {
                    e.printStackTrace();
                }
                break;
            }

            case "Submit":{
                HashMap map=new HashMap();
                map.put("feedBackContent",req.getParameter("feedBackContent"));
                map.put("type",req.getParameter("type"));
                map.put("feedbackId",req.getParameter("feedbackId"));
                map.put("feedbackAccount",req.getParameter("feedbackAccount"));
                try {
                    sqlOp.Submit(map);
                    jsonObject.put("ok",200);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

            case "StatisticsFeedbackInfo":{
                try {
                    jsonObject=sqlOp.StatisticsFeedbackInfo();
                    jsonObject.put("ok",200);
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
            }

            // 个人修改自己的反馈
            case "modifyFeedBack":{
                String feedbackAccount=req.getParameter("feedbackAccount");
                String content=req.getParameter("feedBackContent");
                String type=req.getParameter("type");
                String id=req.getParameter("id");
                try {
                    sqlOp.modifyFeedBack(feedbackAccount,content,type,id);
                    jsonObject.put("ok",200);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

            case "dealFeedBack":{
                String id=req.getParameter("id");
                HashMap map=new HashMap();
                map.put("dealResult",req.getParameter("dealResult"));
                map.put("dealManageId",req.getParameter("dealManageId"));
                map.put("dealManageAccount",req.getParameter("dealManageAccount"));
                try {
                    sqlOp.dealFeedBack(id, map);
                    jsonObject.put("ok",200);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

            case "saveWeather":{
                try {
                    HashMap map=new HashMap();
                    map.put("city", req.getParameter("city"));
                    map.put("day", req.getParameter("day"));
                    map.put("week",req.getParameter("week"));
                    map.put("wea",req.getParameter("wea"));
                    map.put("hightem",req.getParameter("hightem"));
                    map.put("lowtem",req.getParameter("lowtem"));
                    map.put("airLevel",req.getParameter("airLevel"));
                    map.put("winSpeed",req.getParameter("winSpeed"));
                    map.put("humidity",req.getParameter("humidity"));
                    System.out.println(map);
                    sqlOp.saveWeather(map);
                    jsonObject.put("ok",200);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

            case "getWeatherRecord":{
                try {
                    jsonObject=sqlOp.getWeatherRecord();
                } catch (JSONException | SQLException e) {
                    e.printStackTrace();
                }
                break;
            }

            case "AddSubmit":{
                String id=req.getParameter("id");
                HashMap map=new HashMap();
                map.put("todoList",req.getParameter("todoList"));
                try {
                    sqlOp.AddSubmit(id,map);
                    jsonObject.put("ok",200);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

            case "deleteTodoList":{
                String id=req.getParameter("id");
                HashMap map=new HashMap();
                map.put("todoList",req.getParameter("todoList"));
                try {
                    sqlOp.DeleteTodoList(id,map);
                    jsonObject.put("ok",200);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

            case "SubmitModify":{
                String id=req.getParameter("id");
                HashMap map=new HashMap();
                map.put("todoList",req.getParameter("todoList"));
                try {
                    sqlOp.SubmitModify(id,map);
                    jsonObject.put("ok",200);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

            case "StatisticsWeatherInfo":{
                String city=req.getParameter("city");
                try{
                    jsonObject= sqlOp.StatisticsWeatherInfo(city);
                    jsonObject.put("ok",200);
                } catch (SQLException | JSONException throwables) {
                    throwables.printStackTrace();
                }
                break;
            }

            case "getGreenHouse":{
                String account=req.getParameter("account");
                String userLevel=req.getParameter("userLevel");
                try {
                    jsonObject=sqlOp.getGreenHouse(account,userLevel);
                    jsonObject.put("ok",200);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

            case "exportOwnFeedBackRecord":{
                JSONObject json=new JSONObject();
                String account=req.getParameter("account");
                try {
                    json=sqlOp.getOwnFeedBackRecord(account);
                    // 导出txt
                    JsonToFile jsonToFile=new JsonToFile(req.getParameter("tag"));
                    jsonObject=jsonToFile.setJsonTOTxt(json);
                    // 转excel
                    jsonToFile.setJsonToExcel(json,jsonObject);
                    jsonToFile.setExcelToPdfCsv(jsonObject);
                    System.out.println(jsonObject);

                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

            case "deleteWeatherInfo":{
                String id=req.getParameter("id");
                try {
                    sqlOp.deleteWeatherInfo(id);
                    jsonObject.put("ok",200);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

            case "modifyWeatherInfo":{
                String id=req.getParameter("id");
                HashMap<String,String> map=new HashMap<>();
                map.put("city", req.getParameter("city"));
                map.put("day", req.getParameter("day"));
                map.put("week",req.getParameter("week"));
                map.put("wea",req.getParameter("wea"));
                map.put("hightem",req.getParameter("hightem"));
                map.put("lowtem",req.getParameter("lowtem"));
                map.put("airLevel",req.getParameter("airLevel"));
                map.put("winSpeed",req.getParameter("winSpeed"));
                map.put("humidity",req.getParameter("humidity"));
                try {
                    sqlOp.modifyWeatherInfo(map,Integer.parseInt(id));
                    jsonObject.put("ok",200);
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
