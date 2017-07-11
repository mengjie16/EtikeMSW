package utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.impl.cookie.DateUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;

/**
 * 文件操作工具类
 * 
 * @author Calm
 * @since v1.0
 * @created 2016年8月26日 下午4:55:33
 */
public class TSFileUtil {

    private static final Logger log = LoggerFactory.getLogger(TSFileUtil.class);
    /**
     * excel type
     */
    public static final String EXCEL_XLS = "xls";

    public static final String EXCEL_XLSX = "xlsx";

    /**
     * 返回 Googletable from excel
     *
     * @param file
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年8月31日 上午3:14:24
     */
    public static Table parseExcel(File file) {
        if (file == null) {
            return HashBasedTable.create();
        }
        String fileName = Optional.fromNullable(file.getName()).or(".xls");
        boolean isXls = EXCEL_XLS.equals(fileName.substring(fileName.lastIndexOf(".") + 1));
        try {
            if (isXls) {
                return readXlsWithOutSheet(file);
            } else {
                return readXlsxWithOutSheet(file);
            }
        } catch (IOException ex) {
            log.info("read file error");
        }
        return HashBasedTable.create();
    }

    /**
     * 返回 Googletable list from excel
     *
     * @param file
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年8月31日 上午3:14:57
     */
    public static List<Table> parseExcelToList(File file) {
        if (file == null) {
            return Lists.newArrayList();
        }
        String fileName = Optional.fromNullable(file.getName()).or(".xls");
        boolean isXls = EXCEL_XLS.equals(fileName.substring(fileName.lastIndexOf(".") + 1));
        try {
            if (isXls) {
                return readXls(file);
            } else {
                return readXlsx(file);
            }

        } catch (IOException ex) {
            log.info("read file error");
        }
        return Lists.newArrayList();
    }

    /**
     * Read the Excel 2010
     *
     * @param file
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年8月26日 下午5:52:10
     */
    public static List<Table> readXlsx(File file) {
        InputStream is;
        try {
            is = new FileInputStream(file);

            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
            List<Table> list = Lists.newArrayList();
            // Read the Sheet
            for (int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets(); numSheet++) {
                XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(numSheet);
                if (xssfSheet == null) {
                    continue;
                }
                Table<Integer, Integer, String> table = HashBasedTable.create();
                // Read the Row
                for (int rowNum = 0; rowNum < xssfSheet.getLastRowNum(); rowNum++) {
                    XSSFRow xssfRow = xssfSheet.getRow(rowNum);
                    if (xssfRow == null) {
                        continue;
                    }
                    Iterator<Cell> iterator = xssfRow.cellIterator();
                    int cellNum = 0;
                    while (iterator.hasNext()) {
                        Cell cell = iterator.next();
                        // row,column,value
                        table.put(rowNum, cellNum, getValue(cell));
                        cellNum++;
                    }
                }
                list.add(table);
            }
            return list;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Read the Excel 2003-2007
     *
     * @param path
     * @throws IOException
     * @since v1.0
     * @author Calm
     * @created 2016年8月26日 下午5:52:27
     */
    public static List<Table> readXls(File file) throws IOException {

        InputStream is = new FileInputStream(file);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
        List<Table> list = new ArrayList();
        // Read the Sheet
        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                continue;
            }
            Table<Integer, Integer, String> table = HashBasedTable.create();
            // Read the Row
            for (int rowNum = 0; rowNum < hssfSheet.getLastRowNum(); rowNum++) {
                HSSFRow hssfRow = hssfSheet.getRow(rowNum);
                if (hssfRow == null) {
                    continue;
                }
                Iterator<Cell> iterator = hssfRow.cellIterator();
                int cellNum = 0;
                while (iterator.hasNext()) {
                    Cell cell = iterator.next();
                    // row,column,value
                    table.put(rowNum, cellNum, getValue(cell));
                    cellNum++;
                }
            }
            list.add(table);
        }
        return list;
    }

    /**
     * 导出excel文件(xls格式)
     *
     * @param os 输出流
     * @param headers 表头
     * @param datas 数据列
     * @param sheetTitle 工作薄名称
     * @since v1.0
     * @author Calm
     * @throws Exception
     * @created 2016年9月13日 下午3:54:12
     */
    public static boolean exportExcel(OutputStream os, String[] headers, List<Map<Integer, Object>> datas,
        String sheetTitle) throws Exception {
        try {
            // 没有数据导什么
            if (datas == null || datas.size() <= 0) {
                return false;
            }
            // 创建工作簿
            HSSFWorkbook workBook = new HSSFWorkbook();
            // 创建工作表
            HSSFSheet sheet = workBook.createSheet(Strings.isNullOrEmpty(sheetTitle) ? "工作薄(0)" : sheetTitle);
            // 检查标头
            if (headers == null || headers.length <= 0) {
                return false;
            }
            // 创建工作表标题行
            HSSFRow rowm = sheet.createRow(0);
            HSSFCellStyle headerStyle = getHeaderStyle(workBook);
            for (int i = 0; i < headers.length; i++) {
                // 创建单元格
                HSSFCell cell = rowm.createCell(i);
                // 设置列头单元格样式
                cell.setCellStyle(headerStyle);
                // 设置列头单元格的数据类型
                // cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                HSSFRichTextString text = new HSSFRichTextString(headers[i]);
                cell.setCellValue(text);
            }

            int rowCount = 1;
            HSSFCellStyle cellStyle = getStyle(workBook);
            // 填充sheet工作薄
            for (int i = 0; i < datas.size(); i++) {
                Map<Integer, Object> rowData = datas.get(i);
                if (rowData == null) {
                    continue;
                }

                // 创建一行(不含表头)
                HSSFRow row = sheet.createRow(rowCount);
                rowCount++;
                for (int j = 0; j < rowData.values().size(); j++) {
                    // 创建一个单元格
                    HSSFCell cell = row.createCell(j);
                    // 设置样式
                    cell.setCellStyle(cellStyle);
                    Object cellData = rowData.get(j);
                    if (cellData == null) {
                        cell.setCellType(HSSFCell.CELL_TYPE_BLANK);
                    }
                    if (cellData instanceof Integer) {
                        int intValue = (Integer) cellData;
                        cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                        cell.setCellValue(intValue);
                    } else if (cellData instanceof Float) {
                        float fValue = (Float) cellData;
                        HSSFRichTextString textValue = new HSSFRichTextString(String.valueOf(fValue));
                        cell.setCellValue(textValue);
                    } else if (cellData instanceof Double) {
                        double dValue = (Double) cellData;
                        HSSFRichTextString textValue = new HSSFRichTextString(String.valueOf(dValue));
                        cell.setCellValue(textValue);
                    } else if (cellData instanceof Date) {
                        String dateValue = DateUtils.formatDate((Date) cellData, "yyyy-MM-dd HH:mm:ss");
                        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                        cell.setCellValue(dateValue);
                    } else {
                        if (cellData == null) {
                            cell.setCellType(HSSFCell.CELL_TYPE_BLANK);
                        } else {
                            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                            cell.setCellValue(String.valueOf(cellData));
                        }
                    }
                }
            }
            // 让列宽随着导出的列长自动适应
            for (int colNum = 0; colNum < headers.length; colNum++) {
                int maxCellWidth = sheet.getColumnWidth(colNum);
                for (int rowNum = 0; rowNum < sheet.getLastRowNum(); rowNum++) {
                    HSSFRow currentRow = sheet.getRow(rowNum);
                    if (currentRow != null) {
                        HSSFCell currentCell = currentRow.getCell(colNum);
                        if (currentCell == null) {
                            continue;
                        }
                        String dataStr = "";
                        if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                            dataStr = currentCell.getNumericCellValue() + "";
                        } else if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                            dataStr = currentCell.getStringCellValue();
                        } else {
                            continue;
                        }
                        int width = dataStr.length() * 256;
                        if (maxCellWidth < width) {
                            maxCellWidth = width;
                        }
                    }

                }
                sheet.setColumnWidth(colNum, maxCellWidth);
            }
            workBook.write(os);
            return true;
        } catch (Exception ex) {
            throw (ex);
            // log.error("export excel error = {}",ex);
        }
    }

    /**
     * 获取字体样式
     *
     * @param workBook
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年9月13日 下午2:57:20
     */
    public static HSSFFont getHSSFFont(HSSFWorkbook workBook) {
        // 设置字体
        HSSFFont font = workBook.createFont();
        // 设置字体大小
        font.setFontHeightInPoints((short) 12);
        // 字体加粗
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        // 设置字体名字
        font.setFontName("Courier New");
        return font;
    }

    /**
     * 工作薄标题单元格样式
     *
     * @param workBook
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年9月13日 下午2:58:34
     */
    public static HSSFCellStyle getHeaderStyle(HSSFWorkbook workBook) {
        HSSFFont font = workBook.createFont();
        // 设置样式;
        HSSFCellStyle style = workBook.createCellStyle();
        // 设置底边框;
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        // 设置底边框颜色;
        style.setBottomBorderColor(HSSFColor.BLACK.index);
        // 设置左边框;
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        // 设置左边框颜色;
        style.setLeftBorderColor(HSSFColor.BLACK.index);
        // 设置右边框;
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        // 设置右边框颜色;
        style.setRightBorderColor(HSSFColor.BLACK.index);
        // 设置顶边框;
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        // 设置顶边框颜色;
        style.setTopBorderColor(HSSFColor.BLACK.index);
        // 在样式用应用设置的字体;
        style.setFont(font);
        // 设置自动换行;
        style.setWrapText(false);
        // 设置水平对齐的样式为居中对齐;
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        // 设置垂直对齐的样式为居中对齐;
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        return style;
    }

    /**
     * 获取工作薄数据信息单元格样式
     *
     * @param workBook
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年9月13日 下午3:13:20
     */
    public static HSSFCellStyle getStyle(HSSFWorkbook workBook) {

        HSSFFont font = workBook.createFont();
        // 设置样式;
        HSSFCellStyle style = workBook.createCellStyle();
        // 设置底边框;
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        // 设置底边框颜色;
        style.setBottomBorderColor(HSSFColor.BLACK.index);
        // 设置左边框;
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        // 设置左边框颜色;
        style.setLeftBorderColor(HSSFColor.BLACK.index);
        // 设置右边框;
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        // 设置右边框颜色;
        style.setRightBorderColor(HSSFColor.BLACK.index);
        // 设置顶边框;
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        // 设置顶边框颜色;
        style.setTopBorderColor(HSSFColor.BLACK.index);
        // 在样式用应用设置的字体;
        style.setFont(font);
        // 设置自动换行;
        style.setWrapText(false);
        // 设置水平对齐的样式为居中对齐;
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        // 设置垂直对齐的样式为居中对齐;
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

        return style;

    }

    /**
     * Read the Excel 2010
     *
     * @param file
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年8月26日 下午5:52:10
     */
    public static Table readXlsxWithOutSheet(File file) {
        InputStream is;
        try {
            is = new FileInputStream(file);
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
            Table<Integer, Integer, String> table = HashBasedTable.create();
            // Read the Sheet
            XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);
            // Read the Row
            for (int rowNum = 0; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
                XSSFRow xssfRow = xssfSheet.getRow(rowNum);
                if (xssfRow == null) {
                    continue;
                }
                Iterator<Cell> iterator = xssfRow.cellIterator();
                int cellNum = 0;
                while (iterator.hasNext()) {
                    Cell cell = iterator.next();
                    // row,column,value
                    table.put(rowNum, cellNum, getValue(cell));
                    cellNum++;
                }
            }
            return table;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Read the Excel 2003-2007
     *
     * @param path
     * @throws IOException
     * @since v1.0
     * @author Calm
     * @created 2016年8月26日 下午5:52:27
     */
    public static Table readXlsWithOutSheet(File file) throws IOException {

        InputStream is = new FileInputStream(file);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
        Table<Integer, Integer, String> table = HashBasedTable.create();
        // Read the Sheet
        HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(0);
        if (hssfSheet == null) {
            return table;
        }
        // Read the Row
        for (int rowNum = 0; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
            HSSFRow hssfRow = hssfSheet.getRow(rowNum);
            if (hssfRow == null) {
                continue;
            }
            Iterator<Cell> iterator = hssfRow.cellIterator();
            int cellNum = 0;
            while (iterator.hasNext()) {
                Cell cell = iterator.next();
                // row,column,value
                table.put(rowNum, cellNum, getValue(cell));
                cellNum++;
            }
        }
        return table;
    }

    /**
     * 获取
     *
     * @param excel_cell
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年8月26日 下午5:59:03
     */
    private static String getValue(Cell excel_cell) {
        switch (excel_cell.getCellType()) {
        case Cell.CELL_TYPE_BOOLEAN:
            return String.valueOf(excel_cell.getBooleanCellValue() ? "TRUE" : "FALSE");
        case Cell.CELL_TYPE_NUMERIC:
            excel_cell.setCellType(Cell.CELL_TYPE_STRING);
            return excel_cell.getStringCellValue();
        case Cell.CELL_TYPE_STRING:
            return excel_cell.getStringCellValue();
        case Cell.CELL_TYPE_FORMULA:
            return excel_cell.getCellFormula();
        default:
            return "";
        }
    }

    /**
     * ZIP文件后缀名
     */
    public static final String ZIP_FILE_SUFFIX = ".zip";

    /**
     * 对文件列表进行压缩
     * <p/>
     * 注意：<br/>
     * 1、文件如果为目录，会循环递归压缩目录和里面的文件；<br/>
     * 2、如果文件不存在，则直接忽略；
     * 
     * @param zip ZIP包文件路径
     * @param files 文件列表
     */
    public static final void zip(String zip, List<String> files) {
        try {
            // 压缩流
            FileOutputStream fileOutputStream = new FileOutputStream(zip);
            CheckedOutputStream cos = new CheckedOutputStream(fileOutputStream, new CRC32());
            ZipOutputStream out = new ZipOutputStream(cos);

            for (String file : files) {
                // 单个文件或目录
                compress(out, new File(file));
            }

            // 完成
            IOUtils.closeQuietly(out);
        } catch (Exception e) {
            throw new RuntimeException("[压缩文件]-压缩文件异常, ZIP[" + zip + "], Files[" + files + "]！", e);
        }
    }

    /**
     * 压缩文件
     *
     * @param out ZIP输出流
     * @param file 待压缩的文件或是目录
     */
    public static final void compress(ZipOutputStream out, File file) {
        // 目录
        if (file.isDirectory()) {
            compressDirectory(out, file);
        }

        // 文件
        else {
            compressFile(out, file);
        }
    }

    /**
     * 压缩目录
     * <br/>
     * 注意：只压缩目录下所有非ZIP文件
     * 
     * @param out ZIP输出流
     * @param dir 文件目录
     */
    public static final void compressDirectory(ZipOutputStream out, File dir) {
        try {
            // 目录不存在
            if (!dir.exists()) {
                return;
            }

            // 压缩目录下所有的文件
            File[] files = dir.listFiles();
            for (int i = 0; i < files.length; i++) {
                // 遍历压缩所有非ZIP文件
                if (files[i].getName().indexOf(ZIP_FILE_SUFFIX) < 0) {
                    compressFile(out, files[i]);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("[压缩文件]-压缩目录异常, ZIP[" + out + "], DIR[" + dir + "]！", e);
        }
    }

    /**
     * 压缩一个文件，如果文件不存在，则直接忽略
     * 
     * @param out ZIP输出流
     * @param file 待压缩的文件
     */
    public static final void compressFile(ZipOutputStream out, File file) {
        // 文件不存在
        if (!file.exists()) {
            return;
        }

        try {
            // 输入流
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            ZipEntry entry = new ZipEntry(file.getName());
            out.putNextEntry(entry);

            // 复制数据
            IOUtils.copy(bis, out);
            // 关闭输入流
            IOUtils.closeQuietly(bis);
        } catch (Exception e) {
            throw new RuntimeException("[压缩文件]-压缩目录异常, ZIP[" + out + "], File[" + file + "]！", e);
        }
    }
}
