package com.systemair.bcastfans.service;

import com.systemair.bcastfans.domain.FanUnit;
import com.systemair.exchangers.domain.exchangers.Cooler;
import com.systemair.exchangers.domain.exchangers.Exchanger;
import com.systemair.exchangers.domain.exchangers.Heater;
import javafx.scene.control.TableView;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.ArrayList;
import java.util.Map;

public interface ExcelService {
    Workbook loadWorkbook(TableView<FanUnit> table, String path);

    void fillWorksheetFromGUI(Sheet worksheet, TableView<FanUnit> table);

    ArrayList<ArrayList<String>> loadFansWorksheet(Sheet worksheet);

    void setHeader(Sheet worksheet, TableView<FanUnit> table);

    void createCellsInWorksheet(Sheet worksheet, TableView<FanUnit> table);

    Map<Integer, Exchanger> getHeaterExchangers(Sheet worksheet);

    Map<Integer, Exchanger> getCoolerExchangers(Sheet worksheet);

    void fillHeaterFromGUI(Sheet worksheet, Map<Integer, Exchanger> mapHeater);

    void fillCoolerFromGUI(Sheet worksheet, Map<Integer, Exchanger> mapCooler);
}
