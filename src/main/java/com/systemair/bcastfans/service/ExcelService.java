package com.systemair.bcastfans.service;

import com.systemair.bcastfans.domain.FanUnit;
import javafx.scene.control.TableView;
import javafx.stage.Window;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.ArrayList;

public interface ExcelService {
    Workbook loadWorkbook(Window table, String path);

    ArrayList<ArrayList<String>> loadCellsFromWorksheet(Sheet worksheet);

    void fillWorksheetFromGUI(Sheet worksheet, TableView<FanUnit> table);

    void setHeader(Sheet worksheet, TableView<FanUnit> table);

    void createCellsInWorksheet(Sheet worksheet, TableView<FanUnit> table);
}
