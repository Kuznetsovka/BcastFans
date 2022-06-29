package com.systemair.bcastfans.staticClasses;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

public final class BorderUtils {

    public static void setBorderRegion(Sheet sheet, BorderStyle borderStyle, CellRangeAddress region) {
        setBorderTopRegion(sheet, borderStyle, region);
        setBorderBottomRegion(sheet, borderStyle, region);
        setBorderLeftRegion(sheet, borderStyle, region);
        setBorderRightRegion(sheet, borderStyle, region);
    }

    public static void setBorderCell(CellStyle cellStyle, BorderStyle borderStyle, Cell cell) {
        setBorderTop(cellStyle, borderStyle, cell);
        setBorderBottom(cellStyle, borderStyle, cell);
        setBorderLeft(cellStyle, borderStyle, cell);
        setBorderRight(cellStyle, borderStyle, cell);
    }

    public static void setBorderTopRegion(Sheet sheet, BorderStyle borderStyle, CellRangeAddress region) {
        Row row = sheet.getRow(region.getFirstRow());
        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
        for (int i=region.getFirstColumn() ; i<=region.getLastColumn() ; i++) {
            Cell cell = row.getCell(i);
            setBorderTop(cellStyle, borderStyle, cell);
        }
    }

    private static void setBorderTop(CellStyle cellStyle, BorderStyle borderStyle, Cell cell) {
        cellStyle.setBorderTop(borderStyle);
        cell.setCellStyle(cellStyle);
    }

    public static void setBorderBottomRegion(Sheet sheet, BorderStyle borderStyle, CellRangeAddress region) {
        Row row = sheet.getRow(region.getLastRow());
        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
        for (int i=region.getFirstColumn() ; i<=region.getLastColumn() ; i++) {
            Cell cell = row.getCell(i);
            setBorderBottom(cellStyle, borderStyle, cell);
        }
    }

    private static void setBorderBottom(CellStyle cellStyle, BorderStyle borderStyle, Cell cell) {
        cellStyle.setBorderBottom(borderStyle);
        cell.setCellStyle(cellStyle);
    }

    public static void setBorderLeftRegion(Sheet sheet, BorderStyle borderStyle, CellRangeAddress region) {
        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
        for (int i=region.getFirstRow() ; i<=region.getLastRow() ; i++) {
            Cell cell = sheet.getRow(i).getCell(region.getFirstColumn());
            setBorderLeft(cellStyle, borderStyle, cell);
        }
    }

    private static void setBorderLeft(CellStyle cellStyle, BorderStyle borderStyle, Cell cell) {
        cellStyle.setBorderLeft(borderStyle);
        cell.setCellStyle(cellStyle);
    }

    public static void setBorderRightRegion(Sheet sheet, BorderStyle borderStyle, CellRangeAddress region) {
        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
        for (int i=region.getFirstRow() ; i<=region.getLastRow() ; i++) {
            Cell cell = sheet.getRow(i).getCell(region.getLastColumn());
            setBorderRight(cellStyle,borderStyle, cell);
        }
    }

    private static void setBorderRight(CellStyle cellStyle, BorderStyle borderStyle, Cell cell) {
        cellStyle.setBorderRight(borderStyle);
        cell.setCellStyle(cellStyle);
    }

}