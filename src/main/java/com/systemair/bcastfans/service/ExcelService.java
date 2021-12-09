package com.systemair.bcastfans.service;

import com.systemair.bcastfans.UtilClass;
import com.systemair.bcastfans.domain.FanUnit;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class ExcelService {

    public Workbook loadWorkbook(TableView<FanUnit> table,String path) throws IOException {
        Workbook workbook = null;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open file");
        fileChooser.setInitialDirectory(new File(path));
        File file = fileChooser.showOpenDialog(table.getScene().getWindow());
        if (file != null) {
            FileInputStream inputStream = new FileInputStream(file);
            if (file.getName().contains(".xlsx")) {
                workbook = new XSSFWorkbook(inputStream);
            } else {
                workbook = new HSSFWorkbook(inputStream);
            }
            inputStream.close();
        }
        return workbook;
    }

    public void fillWorksheetFromGUI(Sheet worksheet, TableView<FanUnit> table) {
        int lastColumn = table.getColumns().size();
        int countSystems = table.getItems().size();
        Cell[] cell = new XSSFCell[lastColumn];
        for (int count = 0; count < countSystems; count++) {
            FanUnit cells = table.getItems().get(count);
            for (Map.Entry<Integer, String> entry : cells.getRow().entrySet()) {
                Integer column = entry.getKey();
                String value = entry.getValue();
                cell[column] = worksheet.getRow(count + 1).createCell(column, CellType.STRING);
                if (value != null)
                    cell[column].setCellValue(value);
            }
        }
        worksheet.autoSizeColumn(1);
    }

    public void createCellsInWorksheet(Sheet worksheet, TableView<FanUnit> table) {
        int countSystems = table.getItems().size();
        for (int i = 0; i < countSystems + 1; i++) {
            worksheet.createRow(i);
        }
    }

    public ArrayList<ArrayList<String>> loadCellsFromWorksheet(Sheet worksheet) {
        int lastColumn = worksheet.getRow(0).getLastCellNum() - 1;
        int row = 0;
        ArrayList<ArrayList<String>> cells = new ArrayList<>();
        ArrayList<String> rows;
        while (worksheet.getRow(++row).getCell(1).getCellType() != CellType.BLANK) {
            rows = new ArrayList<>();
            for (int column = 0; column < lastColumn; column++) {
                Cell cell = worksheet.getRow(row).getCell(column);
                if (cell != null)
                    rows.add(UtilClass.parseCell(cell));
            }
            if (!rows.isEmpty())
                cells.add(rows);
        }
        return cells;
    }

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
        cell[6].setCellValue("Модель");
        cell[7].setCellValue("Артикул");
        cell[8].setCellValue("Мощность");
        cell[9].setCellValue("Фазность");
        cell[10].setCellValue("Цена");
    }
}
