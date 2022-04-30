//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package sensorModule.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONException;
import org.json.JSONObject;
import sensorModule.dao.sqlOperator;

public class ServletAction extends HttpServlet {
    public ServletAction() {
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("执行doGet!");
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("执行doPost!");
    }

    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("执行service!");
        req.setCharacterEncoding("UTF-8");
        JSONObject jsonObject = new JSONObject();
        String action = req.getParameter("Action");
        sqlOperator sqlOp = new sqlOperator();
        byte var7 = -1;
        if(action.equals("visInfoGet")){
            var7 = 7;
        }
        switch(action.hashCode()) {
            case -364495971:
                if (action.equals("statisticWarningRecord")) {
                    var7 = 5;
                }
                break;
            case -291956621:
                if (action.equals("modifyWarningRecord")) {
                    var7 = 3;
                }
                break;
            case -243630299:
                if (action.equals("queryWarningRecord")) {
                    var7 = 6;
                }
                break;
            case 884513071:
                if (action.equals("showWarningTable")) {
                    var7 = 0;
                }
                break;
            case 1220322233:
                if (action.equals("exportWarningRecord")) {
                    var7 = 4;
                }
                break;
            case 1234983714:
                if (action.equals("deleteWarningRecord")) {
                    var7 = 2;
                }
                break;
            case 1476028396:
                if (action.equals("addWarningRecord")) {
                    var7 = 1;
                }
        }

        String warningRecord;
        switch(var7) {
            case 0:
                String isOrdered = req.getParameter("isOrdered");
                System.out.println("isOrdered=" + isOrdered);

                try {
                    this.responseBack(req, resp, sqlOp.showWarningTable(isOrdered));
                } catch (SQLException | JSONException var20) {
                    var20.printStackTrace();
                }
                break;
            case 1:
                warningRecord = req.getParameter("warning_record");
                String var10 = req.getParameter("greenhouse_id");
                break;
            case 2:
                String warningId = req.getParameter("id");

                try {
                    if (sqlOp.deleteWarningRecord(warningId)) {
                        jsonObject.put("ok", 200);
                    } else {
                        jsonObject.put("ok", 400);
                    }

                    this.responseBack(req, resp, jsonObject);
                } catch (JSONException | SQLException var19) {
                    var19.printStackTrace();
                }
                break;
            case 3:
                String modifyWarningRecord = req.getParameter("warning_record");
                int modifyWarningId = Integer.parseInt(req.getParameter("warning_id"));

                try {
                    if (sqlOp.modifyWarningRecord(modifyWarningRecord, modifyWarningId)) {
                        jsonObject.put("ok", 200);
                        System.out.println("成功修改");
                    } else {
                        System.out.println("修改失败");
                        jsonObject.put("ok", 400);
                    }

                    this.responseBack(req, resp, jsonObject);
                } catch (JSONException | SQLException var18) {
                    var18.printStackTrace();
                }
                break;
            case 4:
                try {
                    JSONObject json = sqlOp.showWarningTable("false");
                    this.getExportWarningRecordToExcel(json);
                    json.put("ok", 200);
                    this.responseBack(req, resp, json);
                } catch (SQLException | JSONException var17) {
                    var17.printStackTrace();
                }
                break;
            case 5:
                try {
                    System.out.println("进入统计ServletAction");
                    this.responseBack(req, resp, sqlOp.statisticWarningTable());
                } catch (SQLException | JSONException var16) {
                    var16.printStackTrace();
                }
                break;
            case 6:
                warningRecord = req.getParameter("warning_record");

                try {
                    this.responseBack(req, resp, sqlOp.queryWarningRecord(warningRecord));
                } catch (JSONException | SQLException var15) {
                    var15.printStackTrace();
                }
            case 7:
                String sensorName = req.getParameter("sensor_name");
                try {
                    this.responseBack(req,resp,sqlOp.visInfoGet(sensorName));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }

    }

    private void responseBack(HttpServletRequest request, HttpServletResponse response, JSONObject json) throws JSONException {
        boolean isAjax = true;
        if (request.getHeader("x-requested-with") == null || request.getHeader("x-requested-with").equals("com.tencent.mm")) {
            isAjax = false;
        }

        if (isAjax) {
            response.setContentType("application/json; charset=UTF-8");

            try {
                response.getWriter().print(json);
                response.getWriter().flush();
                response.getWriter().close();
            } catch (IOException var6) {
                var6.printStackTrace();
            }
        } else {
            System.out.println("异步方式");
        }

    }

    private void getExportWarningRecordToFile(JSONObject json) throws JSONException {
        String jsonStr = json.toString();
        File jsonFile = new File("D:\\test\\maintain\\device\\export_device.txt");
        json.put("download_url", "/test/maintain/device/export_device.txt");

        try {
            if (!jsonFile.exists()) {
                jsonFile.createNewFile();
            }

            FileWriter fileWriter = new FileWriter(jsonFile.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fileWriter);
            bw.write(jsonStr);
            bw.close();
        } catch (IOException var6) {
            var6.printStackTrace();
        }

    }

    private void getExportWarningRecordToExcel(JSONObject json) throws JSONException, IOException {
        MyExcel me = new MyExcel("D:\\test\\maintain\\device\\export_device.xls");
        json.put("download_url", "/test/maintain/device/export_device.xls");
        json.put("file_path", "D:\\test\\maintain\\device\\export_device.xls");
        me.exportData(json);
    }
}
