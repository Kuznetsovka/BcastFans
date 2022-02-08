package com.systemair.bcastfans.service;

import com.systemair.bcastfans.domain.FanUnit;
import javafx.scene.control.TableView;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.ArrayList;

public interface ExcelService {
    Workbook loadWorkbook(TableView<FanUnit> table, String path);

    void fillWorksheetFromGUI(Sheet worksheet, TableView<FanUnit> table);

    ArrayList<ArrayList<String>> loadCellsFromWorksheet(Sheet worksheet);

    void setHeader(Sheet worksheet, TableView<FanUnit> table);

    void createCellsInWorksheet(Sheet worksheet, TableView<FanUnit> table);
}
