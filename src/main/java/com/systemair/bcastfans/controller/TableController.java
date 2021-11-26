package com.systemair.bcastfans.controller;

import com.gembox.spreadsheet.*;
import com.systemair.bcastfans.domain.Fan;
import com.systemair.bcastfans.domain.FanUnit;
import com.systemair.bcastfans.domain.SubType;
import com.systemair.bcastfans.domain.TypeMontage;
import com.systemair.bcastfans.service.TableService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;

@Getter
@Setter
public class TableController implements Initializable {
    static {
        SpreadsheetInfo.setLicense("FREE-LIMITED-KEY");
    }
    private TableService tableService = new TableService();
    private BrowserController browserService = new BrowserController();
    @FXML
    TableView<FanUnit> table;
    @FXML
    private CheckBox checkBox;
    @FXML
    TableColumn<FanUnit, Boolean> columnChoose;
    @FXML
    TableColumn<FanUnit,String> columnNumberSystem;
    @FXML
    TableColumn<FanUnit,String> columnAirFlow;
    @FXML
    TableColumn<FanUnit,String> columnAirDrop;
    @FXML
    TableColumn<FanUnit, TypeMontage> columnTypeMontage;
    @FXML
    TableColumn<FanUnit,SubType> columnSubType;
    @FXML
    TableColumn<FanUnit,String> columnModel;
    @FXML
    TableColumn<FanUnit,String> columnArticle;
    @FXML
    TableColumn<FanUnit,String> columnPower;
    @FXML
    TableColumn<FanUnit,String> columnPhase;
    @FXML
    TableColumn<FanUnit,String> columnPrice;

    private ObservableList<FanUnit> inputData;

    @FXML
    public void checkBoxInitialize() {
        boolean checkBtn = checkBox.isSelected();
        for (FanUnit n : inputData) {
            n.setCheck(checkBtn);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        columnChoose.setCellValueFactory(new PropertyValueFactory<>("check"));
    }

    @SneakyThrows
    public void load() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open file");
        File file = fileChooser.showOpenDialog(table.getScene().getWindow());

        ExcelFile workbook = ExcelFile.load(file.getAbsolutePath());
        ExcelWorksheet worksheet = workbook.getWorksheet(0);
        int lastColumn = worksheet.getUsedCellRange(true).getLastColumnIndex();
        ArrayList<ArrayList<String>> cells = new ArrayList<>();
        ArrayList<String> rows;
        int row = 1;
        while(worksheet.getCell(row++, 0).getValueType() != CellValueType.NULL){
            rows = new ArrayList<>();
            for (int column = 0; column < lastColumn; column++) {
                ExcelCell cell = worksheet.getCell(row, column);
                if (cell.getValueType() != CellValueType.NULL)
                    rows.add(cell.getValue().toString());
            }
            if (!rows.isEmpty())
                cells.add(rows);
        }
        fillTable(cells);
    }

    private void fillTable(@NonNull ArrayList<ArrayList<String>> dataSource) {
        ArrayList<FanUnit> list = new ArrayList<>();
        for (ArrayList<String> row : dataSource) {
            list.add(new FanUnit(row));
        }
        inputData = FXCollections.observableArrayList(list);
        tableService.fillInputData(inputData, table,columnNumberSystem,columnAirFlow,columnAirDrop,columnTypeMontage,columnSubType);
    }

    @SneakyThrows
    public void save() {
        ExcelFile file = new ExcelFile();
        ExcelWorksheet worksheet = file.addWorksheet("sheet");
        setHeader(worksheet);
        for (int row = 0; row < table.getItems().size(); row++) {
            FanUnit cells = table.getItems().get(row);
            for (Map.Entry<Integer, String> entry : cells.getRow().entrySet()) {
                Integer column = entry.getKey();
                String value = entry.getValue();
                if (value != null)
                    worksheet.getCell(row + 1, column).setValue(value);
            }
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XLSX files (*.xlsx)", "*.xlsx"),
                new FileChooser.ExtensionFilter("XLS files (*.xls)", "*.xls"),
                new FileChooser.ExtensionFilter("ODS files (*.ods)", "*.ods"),
                new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv"),
                new FileChooser.ExtensionFilter("HTML files (*.html)", "*.html")
        );
        File saveFile = fileChooser.showSaveDialog(table.getScene().getWindow());
        try {
            file.save(saveFile.getAbsolutePath());
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }

    private void setHeader(ExcelWorksheet worksheet) {
        worksheet.getCell(0, 0).setValue("Считать?");
        worksheet.getCell(0, 1).setValue("N");
        worksheet.getCell(0, 2).setValue("Расход");
        worksheet.getCell(0, 3).setValue("Потери");
        worksheet.getCell(0, 4).setValue("Тип монтажа");
        worksheet.getCell(0, 5).setValue("Тип установки");
        worksheet.getCell(0, 6).setValue("Модель");
        worksheet.getCell(0, 7).setValue("Артикул");
        worksheet.getCell(0, 8).setValue("Мощность");
        worksheet.getCell(0, 9).setValue("Фазность");
        worksheet.getCell(0, 10).setValue("Цена");
    }

    public void calculate() {
        inputData = browserService.calculate(inputData);
        tableService.fillResultData(inputData,table,columnModel,columnArticle,columnPower,columnPhase,columnPrice);
    }

    public void clear() {
        inputData.clear();
    }
}