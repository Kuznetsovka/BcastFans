package com.systemair.bcastfans.controller;

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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;

@Getter
@Setter
public class TableController implements Initializable {
    private TableService tableService = new TableService();
    private BrowserController browserService = new BrowserController();
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
    private HSSFWorkbook workbook;

    @FXML
    public void checkBoxInitialize() {
        boolean checkBtn = checkBox.isSelected();
        for (FanUnit f : data) {
            f.setCheck(checkBtn);
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
        try (FileInputStream inputStream = new FileInputStream(file)) {
            workbook = new HSSFWorkbook(inputStream);
            HSSFSheet worksheet = workbook.getSheetAt(0);
            int lastColumn = worksheet.getRow(0).getLastCellNum()-1;
            ArrayList<ArrayList<String>> cells = new ArrayList<>();
            ArrayList<String> rows;
            int row = 0;
            while (worksheet.getRow(row++).getCell(0).getCellType() != CellType.BLANK) {
                rows = new ArrayList<>();
                for (int column = 0; column < lastColumn; column++) {
                    Cell cell = worksheet.getRow(row).getCell(column);
                    if (cell.getCellType() != CellType.BLANK)
                        rows.add(parseCell(cell));
                }
                if (!rows.isEmpty())
                    cells.add(rows);
            }
            fillTable(cells);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String parseCell(Cell cell) {
        if (cell == null) return "";
        CellType cellType = cell.getCellType();
        switch (cellType) {
            case _NONE:
            case BLANK:
                return "";
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                cell.getCellFormula();
                FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                return String.valueOf(evaluator.evaluate(cell).getNumberValue());
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case STRING:
                return cell.getStringCellValue();
            case ERROR:
                return ""; //TODO Вывод ошибки
        }
        return "";
    }

    private void fillTable(@NonNull ArrayList<ArrayList<String>> dataSource) {
        ArrayList<FanUnit> list = new ArrayList<>();
        for (ArrayList<String> row : dataSource) {
            list.add(new FanUnit(row));
        }
        data = FXCollections.observableArrayList(list);
        tableService.fillInputData(data, table, columnNumberSystem, columnAirFlow, columnAirDrop, columnTypeMontage, columnSubType);
    }

    @SneakyThrows
    public void save() {
        HSSFWorkbook workbook = new HSSFWorkbook();
        int lastRow = table.getItems().size();
        int lastColumn = table.getColumns().size();
        HSSFSheet worksheet = initializeWorksheet(workbook,lastRow);
        setHeader(worksheet,lastColumn);

            for (int i = 1; i < lastRow; i++) {
                FanUnit cells = table.getItems().get(i);
                for (Map.Entry<Integer, String> entry : cells.getRow().entrySet()) {
                    Integer column = entry.getKey();
                    String value = entry.getValue();
                    if (value != null)
                        try {
                            Cell[] cell = new Cell[lastColumn];
                            for (int j = 0; j < lastColumn; j++) {
                                cell[j] = worksheet.getRow(i).createCell(0, CellType.STRING);
                            }
                        worksheet.getRow(i).getCell(column).setCellValue(value);
                        } catch(Exception e){
                            System.out.println("row " + i + "column " + column);
                        }
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
        saveFile.getParentFile().mkdirs();
        try (FileOutputStream outFile = new FileOutputStream(saveFile)) {
            workbook.write(outFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private HSSFSheet initializeWorksheet(HSSFWorkbook workbook,int lastRow) {
        HSSFSheet worksheet = workbook.createSheet("sheet");
        for (int i = 0; i < lastRow; i++) {
            worksheet.createRow(i);
        }
        return worksheet;
    }

    private void setHeader(HSSFSheet worksheet, int lastColumn) {
        Cell[] cell = new Cell[lastColumn];
        for (int i = 0; i < lastColumn; i++) {
            cell[i] = worksheet.getRow(0).createCell(0, CellType.STRING);
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

    public void calculate() {
        data = browserService.calculate(fieldNegativeLimit, fieldPositiveLimit, data);
        tableService.fillResultData(data, table, columnModel, columnArticle, columnPower, columnPhase, columnPrice);
    }

    public void clear() {
        data.clear();
    }
}