package com.systemair.bcastfans.controller;

import com.systemair.bcastfans.UtilClass;
import com.systemair.bcastfans.domain.FanUnit;
import com.systemair.bcastfans.domain.SubType;
import com.systemair.bcastfans.domain.TypeMontage;
import com.systemair.bcastfans.service.ExcelService;
import com.systemair.bcastfans.service.TableService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

@Getter
@Setter
public class TableController implements Initializable {
    private TableService tableService = new TableService();
    private ExcelService excelService = new ExcelService();
    private BrowserController browserController = new BrowserController();
    @FXML
    public TextField fieldNegativeLimit;
    @FXML
    public TextField fieldPositiveLimit;
    @FXML
    TableView<FanUnit> table;
    @FXML
    private CheckBox checkBox;
    @FXML
    TableColumn<FanUnit, Boolean> columnChoose;
    @FXML
    TableColumn<FanUnit, String> columnNumberSystem;
    @FXML
    TableColumn<FanUnit, String> columnAirFlow;
    @FXML
    TableColumn<FanUnit, String> columnAirDrop;
    @FXML
    TableColumn<FanUnit, TypeMontage> columnTypeMontage;
    @FXML
    TableColumn<FanUnit, SubType> columnSubType;
    @FXML
    TableColumn<FanUnit, String> columnModel;
    @FXML
    TableColumn<FanUnit, String> columnArticle;
    @FXML
    TableColumn<FanUnit, String> columnPower;
    @FXML
    TableColumn<FanUnit, String> columnPhase;
    @FXML
    TableColumn<FanUnit, String> columnPrice;

    private ObservableList<FanUnit> data;
    private Workbook workbook;

    @FXML
    public void checkBoxInitialize() {
        boolean checkBtn = checkBox.isSelected();
        for (FanUnit f : data) {
            f.setCheck(checkBtn);
        }
        fieldNegativeLimit.setOnKeyPressed(event -> putMinusBeforeValue());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        columnChoose.setCellValueFactory(new PropertyValueFactory<>("check"));
        browserController.initializeBrowser();
    }

    @SneakyThrows
    public void load() {
        workbook = excelService.loadWorkbook(table);
        if (workbook == null) return;
        Sheet worksheet = workbook.getSheetAt(0);
        ArrayList<ArrayList<String>> cells = excelService.loadCellsFromWorksheet(worksheet);
        fillGUITableFromExcel(cells);
    }

    private void fillGUITableFromExcel(@NonNull ArrayList<ArrayList<String>> dataSource) {
        ArrayList<FanUnit> list = new ArrayList<>();
        for (ArrayList<String> row : dataSource) {
            list.add(new FanUnit(row));
        }
        data = FXCollections.observableArrayList(list);
        tableService.fillInputData(data, table, columnNumberSystem, columnAirFlow, columnAirDrop, columnTypeMontage, columnSubType);
    }

    @SneakyThrows
    public void save() {
        Workbook workbook = new XSSFWorkbook();
        Sheet worksheet = workbook.createSheet("sheet");
        excelService.createCellsInWorksheet(worksheet, table);
        excelService.setHeader(worksheet, table);
        excelService.fillWorksheetFromGUI(worksheet, table);
        FileOutputStream outFile = UtilClass.getFileOutputStream(table);
        if (outFile == null) return;
        workbook.write(outFile);
        outFile.close();
        workbook.close();
    }

    public void calculate() {
        data = browserController.calculate(fieldNegativeLimit, fieldPositiveLimit, data);
        tableService.fillResultData(data, table, columnModel, columnArticle, columnPower, columnPhase, columnPrice);
    }

    public void clear() {
        data.clear();
    }

    public void putMinusBeforeValue() {
        String text = fieldNegativeLimit.getText();
        if (!text.contains("-"))
            fieldNegativeLimit.setText("-" + text);
    }
}