package com.systemair.bcastfans.service;

import com.systemair.bcastfans.domain.FanUnit;
import com.systemair.exchangers.domain.exchangers.Exchanger;
import javafx.scene.control.TableView;
import javafx.stage.Window;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.ArrayList;
import java.util.Map;

public interface ExcelService {
    Workbook loadWorkbook(Window table, String path);

    void fillWorksheetFromGUI(Sheet worksheet, TableView<FanUnit> table);

    ArrayList<ArrayList<String>> loadFansWorksheet(Sheet worksheet);

    void setHeader(Sheet worksheet, TableView<FanUnit> table);

    void createCellsInWorksheet(Sheet worksheet, TableView<FanUnit> table);

    Map<Integer, Exchanger> getHeaterExchangers(Sheet worksheet);

    Map<Integer, Exchanger> getCoolerExchangers(Sheet worksheet);

    void fillHeaterFromGUI(Sheet worksheet, Map<Integer, Exchanger> mapHeater);

    void fillCoolerFromGUI(Sheet worksheet, Map<Integer, Exchanger> mapCooler);
}
