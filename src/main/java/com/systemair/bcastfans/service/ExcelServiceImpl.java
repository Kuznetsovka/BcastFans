package com.systemair.bcastfans.service;

import com.systemair.bcastfans.MyCatchException;
import com.systemair.bcastfans.domain.FanUnit;
import com.systemair.bcastfans.staticClasses.BorderUtils;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Map;

import static com.systemair.bcastfans.staticClasses.UtilClass.parseCell;

public class ExcelServiceImpl implements ExcelService {
    private String fileExcelOLPath;

    @Override
    public Workbook loadWorkbook(Window window, String path) {
        Workbook workbook = null;
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open file");
            fileChooser.setInitialDirectory(new File(path));
            File fileExcel = fileChooser.showOpenDialog(window);
            if (fileExcel != null) {
                fileExcelOLPath = fileExcel.getAbsolutePath();
                workbook = readWorkbook(fileExcelOLPath);
            }
        } catch (IllegalArgumentException e) {
            try {
                throw new MyCatchException(e.getMessage(), Alert.AlertType.ERROR);
            } catch (MyCatchException ex) {
                ex.printStackTrace();
            }
        }
        return workbook;
    }


    @Override
    public ArrayList<ArrayList<String>> loadCellsFromWorksheet(Sheet worksheet) {
        int lastColumn = 58;
        int row = 0;
        ArrayList<ArrayList<String>> cells = new ArrayList<>();
        ArrayList<String> rows;
        while (worksheet.getRow(++row).getCell(1).getCellType() != CellType.BLANK) {
            rows = new ArrayList<>();
            for (int column = 0; column < lastColumn; column++) {
                Cell cell = worksheet.getRow(row).getCell(column);
                if (cell != null)
                    rows.add(parseCell(cell));
                else
                    rows.add("");
            }
            if (!rows.isEmpty())
                cells.add(rows);
            if (worksheet.getRow(row + 1).getCell(1) == null) break;
        }
        return cells;
    }

    public static Workbook readWorkbook(String filename) {
        try {
            if (filename.contains(".xlsx")) {
                return new XSSFWorkbook(filename);
            } else {
                return new HSSFWorkbook(new FileInputStream(filename));
            }
        } catch (Exception e) {
            try {
                throw new MyCatchException(e.getMessage(), Alert.AlertType.ERROR);
            } catch (MyCatchException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void fillWorksheetFromGUI(Sheet worksheet, TableView<FanUnit> table) {
        String url;
        int lastColumn = 26;
        int countSystems = table.getItems().size();
        Cell[] cell = new XSSFCell[lastColumn];
        for (int count = 0; count < countSystems; count++) {
            FanUnit cells = table.getItems().get(count);
            url = (cells.getFan() != null) ? cells.getFan().getShortLink() : "";
            for (Map.Entry<Integer, String> entry : cells.getRow().entrySet()) {
                Integer column = entry.getKey();
                String value = entry.getValue();
                cell[column] = worksheet.getRow(count + 1).createCell(column, CellType.STRING);
                if (value != null)
                    cell[column].setCellValue(value);
                if (column == 7 && !url.isEmpty()) {
                    Hyperlink link = worksheet.getWorkbook().getCreationHelper().createHyperlink(HyperlinkType.URL);
                    link.setAddress(url);
                    cell[column].setHyperlink(link);
                }
                worksheet.autoSizeColumn(column);
                BorderUtils.setBorderCell(worksheet.getWorkbook().createCellStyle(),BorderStyle.THIN,cell[column]);
            }
        }
    }

    @Override
    public void createCellsInWorksheet(Sheet worksheet, TableView<FanUnit> table) {
        int countSystems = table.getItems().size();
        for (int i = 0; i < countSystems + 1; i++) {
            worksheet.createRow(i);
        }
    }

    @Override
    public void setHeader(Sheet worksheet, TableView<FanUnit> table) {
        int lastColumn = 26;
        Cell[] cells = new XSSFCell[lastColumn];
        for (int i = 0; i < lastColumn; i++) {
            cells[i] = worksheet.getRow(0).createCell(i, CellType.STRING);
        }
        cells[0].setCellValue("Считать?");
        cells[1].setCellValue("N");
        cells[2].setCellValue("Расход");
        cells[3].setCellValue("Потери");
        cells[4].setCellValue("Тип монтажа");
        cells[5].setCellValue("Тип установки");
        cells[6].setCellValue("Типоразмер");
        cells[7].setCellValue("Модель");
        cells[8].setCellValue("Артикул");
        cells[9].setCellValue("Мощность");
        cells[10].setCellValue("Фазность");
        cells[11].setCellValue("Цена");
        cells[12].setCellValue("Клапан");
        cells[13].setCellValue("Потери");
        cells[14].setCellValue("Фильтр1");
        cells[15].setCellValue("Потери");
        cells[16].setCellValue("Фильтр2");
        cells[17].setCellValue("Потери");
        cells[18].setCellValue("Шумоглушитель");
        cells[19].setCellValue("Потери");
        cells[20].setCellValue("Эл. нагреватель");
        cells[21].setCellValue("Потери");
        cells[22].setCellValue("Нагреватель");
        cells[23].setCellValue("Потери");
        cells[24].setCellValue("Охладитель");
        cells[25].setCellValue("Потери");
        for (Cell cell : cells) {
            BorderUtils.setBorderCell(worksheet.getWorkbook().createCellStyle(),BorderStyle.MEDIUM,cell);
        }
    }

}
