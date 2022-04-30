package device.export;

import com.spire.xls.FileFormat;
import com.spire.xls.Workbook;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class JsonToFile {
    // 定义导出的路径信息
    final String txtPath="C:\\Users\\ecw\\Desktop\\JsonToFile\\";
    final String excelPath="C:\\Users\\ecw\\Desktop\\JsonToFile\\";
    final String pdfPath="C:\\Users\\ecw\\Desktop\\JsonToFile\\";
    final String csvPath="C:\\Users\\ecw\\Desktop\\JsonToFile\\";

    String txtTemp="export";
    String excelTemp="export";
    String pdfTemp="export";
    String csvTemp="export";

    public JsonToFile(String tag){
        this.txtTemp+=tag+new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date())+".txt";
        this.excelTemp+=tag+new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date())+".xls";
        this.pdfTemp+=tag+new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date())+".pdf";
        this.csvTemp+=tag+new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date())+".csv";
        System.out.println("设置temp成功!");
    }


    // 将JSON导出成TXT文件
    public JSONObject setJsonTOTxt(JSONObject jsonObject) throws JSONException, IOException {
        // JSON转字符串
        String jsonstr=jsonObject.toString();

        File jsonFile=new File(this.txtPath+this.txtTemp);
        if(!jsonFile.exists()){
            jsonFile.createNewFile();
            System.out.println("创建文件成功!");
        }
        // 写入文件
        FileWriter fileWriter=new FileWriter(jsonFile.getAbsoluteFile());
        BufferedWriter bufferedWriter=new BufferedWriter(fileWriter);

        System.out.println("[sqlOperator/setJsonTOTxt]正在写入文件!");
        bufferedWriter.write(jsonstr);
        bufferedWriter.close();
        System.out.println("[sqlOperator/setJsonTOTxt]set JSON to Txt, success");
        // 使用JSON格式反馈下载路径
        JSONObject json=new JSONObject();
        json.put("txtDownloadPath","/upLoad/"+this.txtTemp);
        return json;
    }


    // 将json导出成excel
    public void setJsonToExcel(JSONObject json,JSONObject jsonObject) throws IOException, JSONException {
        HSSFWorkbook workbook=new HSSFWorkbook();
        HSSFSheet sheet=workbook.createSheet("sheet0");

        // 获取表头
        JSONArray jsonTitle=json.getJSONArray("title");
        // 创建第一行
        HSSFRow rowTitle=sheet.createRow(0);
        // 设置表头
        for(int i=0;i<jsonTitle.length();i++){
            HSSFCell cell=rowTitle.createCell(i);
            cell.setCellValue((String) jsonTitle.get(i));
        }
        // 填充表内容
        JSONArray jsonArrayContent=json.getJSONArray("record");
        for(int i=0;i<jsonArrayContent.length();i++){
            HSSFRow row=sheet.createRow(i+1);

            // 获取内容
            int j=0;
            HashMap<String, String> record= (HashMap<String, String>) jsonArrayContent.get(i);
            for(int col=0;col<record.size();col++){
                HSSFCell cell=row.createCell(j);
                cell.setCellValue(record.get(jsonTitle.get(col)));
                j++;
            }
        }
        // 设置自适应宽度
        for(int i=0;i<jsonTitle.length()+1;i++){
            sheet.autoSizeColumn(i);
        }
        autoSizeCh(sheet,jsonTitle.length());
        // 完成自适应宽度
        FileOutputStream outputStream=new FileOutputStream(this.excelPath+this.excelTemp);
        workbook.write(outputStream);
        outputStream.flush();
        outputStream.close();
        jsonObject.put("excelDownloadPath","/upLoad/"+this.excelTemp);
        jsonObject.put("ok",200);
        System.out.println("set JSON to excel, success");
    }

    public void setExcelToPdfCsv(JSONObject jsonObject) throws JSONException {
        Workbook workbook=new Workbook();
        // 获取excel的路径
        workbook.loadFromFile(this.excelPath+this.excelTemp);
        // 转为pdf，存在问题，单元格中内容不能完整显示
        workbook.saveToFile(this.pdfPath+this.pdfTemp, FileFormat.PDF);
        jsonObject.put("pdfDownloadPath","/upLoad/"+this.pdfTemp);
        System.out.println("set excel to pdf, success");
        // 转为csv,正确
        workbook.saveToFile(this.csvPath+this.csvTemp,FileFormat.CSV);
        jsonObject.put("csvDownloadPath","/upLoad/"+this.csvTemp);
        System.out.println("set excel to csv, success");
    }


    // 处理中文excel表格中的宽度自适应问题 col表示列数
    private void autoSizeCh(Sheet sheet,int col) {
        for (int columnNum = 0; columnNum < col+1; columnNum++) {

            int columnWidth = sheet.getColumnWidth(columnNum) / 256;
//            System.out.println("第"+columnNum+"列初始宽度:"+columnWidth);
            for (int rowNum = 0; rowNum < sheet.getLastRowNum()+1; rowNum++) {

                HSSFRow currentRow;
                //当前行未被使用过
                if (sheet.getRow(rowNum) == null) {
                    currentRow = (HSSFRow) sheet.createRow(rowNum);
                } else {
                    currentRow = (HSSFRow) sheet.getRow(rowNum);
                }

                if (currentRow.getCell(columnNum) != null) {
                    HSSFCell currentCell = currentRow.getCell(columnNum);
                    if (currentCell.getCellType() == Cell.CELL_TYPE_STRING ) {
                        int length = currentCell.getStringCellValue().getBytes(StandardCharsets.UTF_8).length;
                        if (columnWidth < length) {
                            columnWidth = length;
                        }
                    }
                }
            }
            sheet.setColumnWidth(columnNum, columnWidth * 256);
//            System.out.println("第"+columnNum+"列最终宽度:"+sheet.getColumnWidth(columnNum)/256);
        }
    }

}
