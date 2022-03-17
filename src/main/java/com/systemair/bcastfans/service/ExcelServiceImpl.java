package com.systemair.bcastfans.service;

import com.systemair.bcastfans.MyCatchException;
import com.systemair.bcastfans.domain.FanUnit;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import static com.systemair.bcastfans.staticClasses.UtilClass.parseCell;

public class ExcelServiceImpl implements ExcelService {

    public static final int START_CELL_RESULT_HEATER = 40;
    public static final int START_CELL_RESULT_COOLER = 53;
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
    public Workbook reOpen() {
        return readWorkbook(fileExcelOLPath);
    }

    @Override
    public ArrayList<ArrayList<String>> loadCellsFromWorksheet(Sheet worksheet) {
        int lastColumn = 7;
        int row = 0;
        ArrayList<ArrayList<String>> cells = new ArrayList<>();
        ArrayList<String> rows;
        try {
            while (worksheet.getRow(++row).getCell(1).getCellType() != CellType.BLANK) {
                rows = new ArrayList<>();
                for (int column = 0; column < lastColumn; column++) {
                    Cell cell = worksheet.getRow(row).getCell(column);
                    if (cell != null)
                        rows.add(parseCell(cell));
                }
                if (!rows.isEmpty())
                    cells.add(rows);
            }
        } catch (Exception e) {
            try {
                throw new MyCatchException("Ошибка считывания данных, не должно быть формул", Alert.AlertType.ERROR);
            } catch (MyCatchException ex) {
                ex.printStackTrace();
            }
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
        int lastColumn = table.getColumns().size();
        int countSystems = table.getItems().size();
        Cell[] cell = new XSSFCell[lastColumn];
        for (int count = 0; count < countSystems; count++) {
            FanUnit cells = table.getItems().get(count);
            if (cells.getFan() != null)
                url = cells.getFan().getShortLink();
            else
                url = "";
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
            }
        }
        worksheet.autoSizeColumn(1);
    }

    @Override
    public void createCellsInWorksheet(Sheet worksheet, TableView<FanUnit> table) {
        int countSystems = table.getItems().size();
        for (int i = 0; i < countSystems + 1; i++) {
            worksheet.createRow(i);
        }
    }

    @Override
    public ArrayList<ArrayList<String>> loadFansWorksheet(Sheet worksheet) {
        int lastColumn = 7;
        int row = 0;
        ArrayList<ArrayList<String>> cells = new ArrayList<>();
        ArrayList<String> rows;
        Cell cell = null;
        try {
            while (worksheet.getRow(++row).getCell(1).getCellType() != CellType.BLANK) {
                rows = new ArrayList<>();
                for (int column = 0; column < lastColumn; column++) {
                    cell = worksheet.getRow(row).getCell(column);
                    if (cell != null)
                        rows.add(parseCell(cell));
                }
                if (!rows.isEmpty())
                    cells.add(rows);
            }
        } catch (Exception e) {
            try {
                throw new MyCatchException("Ошибка считывания данных, не должно быть формул, ячейка:" + Objects.requireNonNull(cell).getAddress().formatAsString(), Alert.AlertType.WARNING);
            } catch (MyCatchException ex) {
                ex.printStackTrace();
            }
        }
        return cells;
    }

    @Override
    public void setHeader(Sheet worksheet, TableView<FanUnit> table) {
        int lastColumn = table.getColumns().size();
        Cell[] cell = new XSSFCell[lastColumn];
        for (int i = 0; i < lastColumn; i++) {
            cell[i] = worksheet.getRow(0).createCell(i, CellType.STRING);
        }
        cell[0].setCellValue("Считать?");
        cell[1].setCellValue("N");
        cell[2].setCellValue("Расход");
        cell[3].setCellValue("Потери");
        cell[4].setCellValue("Тип монтажа");
        cell[5].setCellValue("Тип установки");
        cell[6].setCellValue("Типоразмер");
        cell[7].setCellValue("Модель");
        cell[8].setCellValue("Артикул");
        cell[9].setCellValue("Мощность");
        cell[10].setCellValue("Фазность");
        cell[11].setCellValue("Цена");
    }

}
