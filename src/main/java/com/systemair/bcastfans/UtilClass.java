package com.systemair.bcastfans;

import org.apache.poi.xssf.usermodel.XSSFCell;

public class UtilClass {

    public static String parseCell(XSSFCell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case BLANK:
                return "";
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case STRING:
                return cell.getStringCellValue();
            case ERROR:
                return ""; //TODO Вывод ошибки
        }
        return "";
    }
}
