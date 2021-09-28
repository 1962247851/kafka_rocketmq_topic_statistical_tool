package top.ordinaryroad.util;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.jetbrains.annotations.NotNull;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 苗锦洲
 * @date 2021/7/26
 */
public class PoiUtil {

    /**
     * 写入到一个文件
     *
     * @param path 文件路径
     * @param map  env, [topic, [consumers]]
     */
    public static void writeTopicsToExcel(@NotNull String path, Map<String, Map<String, List<String>>> map) {
        System.out.println("写入Excel...");
        // 创建新的Excel 工作簿
        XSSFWorkbook workbook = new XSSFWorkbook();

        // 在Excel工作簿中建一工作表，其名为缺省值
        // 如要新建一名为"model"的工作表，其语句为：
        // HSSFSheet sheet = workbook.createSheet("model");

        try {
            if (map != null) {
                map.forEach((env, map1) -> {
                    AtomicInteger showIndex = new AtomicInteger(1);
                    AtomicInteger rowIndex = new AtomicInteger(1);
                    XSSFSheet sheet = workbook.createSheet(env);
                    // 创建表格表头
                    List<String> headCellValueList = new ArrayList<>(3);
                    headCellValueList.add("序号");
                    headCellValueList.add("Topic");
                    headCellValueList.add("消费者组Id");
                    headCellValueList.add("应用服务");
                    headCellValueList.add("描述");
                    generateTableHead(workbook, sheet, headCellValueList);

                    map1.forEach((topic, consumerList) -> {
                        // 创建行
                        XSSFRow row = sheet.createRow(rowIndex.getAndIncrement());
                        // 创建序号单元格
                        row.createCell(0, CellType.STRING).setCellValue(showIndex.getAndIncrement());
                        // 创建Topic单元格
                        row.createCell(1, CellType.STRING).setCellValue(topic);

                        for (int i = 0; i < consumerList.size(); i++) {
                            String consumer = consumerList.get(i);
                            if (i > 0) {
                                row = sheet.createRow(rowIndex.getAndIncrement());
                            }
                            // 创建消费者组Id单元格
                            XSSFCell cell = row.createCell(2, CellType.STRING);
                            XSSFCellStyle cellStyle = workbook.createCellStyle();
                            cellStyle.setWrapText(true);
                            cell.setCellValue(consumer);
                            cell.setCellStyle(cellStyle);
                        }
                        if (consumerList.size() > 1) {
                            // 合并单元格
                            sheet.addMergedRegion(new CellRangeAddress(rowIndex.get() - consumerList.size(), rowIndex.get() - 1, 0, 0));
                            sheet.addMergedRegion(new CellRangeAddress(rowIndex.get() - consumerList.size(), rowIndex.get() - 1, 1, 1));
                        }

                    });
                });
            }

            // 新建一输出文件流
            FileOutputStream fileOut = new FileOutputStream(path, false);

            // 把相应的Excel 工作簿存盘
            workbook.write(fileOut);
            fileOut.flush();

            // 操作结束，关闭文件
            fileOut.close();
            System.out.println("完成：" + path);
        } catch (Exception e) {
            System.out.println("已运行 xlCreate() : " + e);
        }
    }


    /**
     * 写入到多个文件
     *
     * @param path 文件路径
     * @param map  env, [topic, [consumers]]
     */
    public static void writeTopicsToMultiExcel(@NotNull String path, Map<String, Map<String, List<String>>> map) {
        map.forEach((env, maps) -> {
            String realPath = path.substring(0, path.length() - 5) + "-" + env + ".xlsx";
            Map<String, Map<String, List<String>>> mapTemp = new TreeMap<>();
            mapTemp.put(env, maps);
            writeTopicsToExcel(realPath, mapTemp);
        });
    }


    private static void generateTableHead(XSSFWorkbook workbook, XSSFSheet sheet, List<String> cellValueList) {
        // 固定表头
        sheet.createFreezePane(0, 1);
        // 公式 y=255.86x+184.27 来源 https://blog.csdn.net/duqian42707/article/details/51491312
        sheet.setColumnWidth(1, 256 * 50 + 184);
        sheet.setColumnWidth(2, 256 * 70 + 184);
        // 创建行
        XSSFRow headRow = sheet.createRow(0);
        for (int i = 0; i < cellValueList.size(); i++) {
            String cellValue = cellValueList.get(i);
            XSSFCellStyle indexCellStyle = workbook.createCellStyle();
            XSSFFont font = workbook.createFont();
            font.setFontName("宋体");
            font.setBold(true);
            indexCellStyle.setFont(font);
            XSSFCell indexCell = headRow.createCell(i, CellType.STRING);
            indexCell.setCellStyle(indexCellStyle);
            indexCell.setCellValue(cellValue);
        }
    }

}