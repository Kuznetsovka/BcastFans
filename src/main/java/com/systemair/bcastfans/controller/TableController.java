package com.systemair.bcastfans.controller;

import com.systemair.bcastfans.UtilClass;
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
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
    private XSSFWorkbook workbook;

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
            workbook = new XSSFWorkbook(inputStream);
            XSSFSheet worksheet = workbook.getSheetAt(0);
            int lastColumn = worksheet.getRow(0).getLastCellNum() - 1;
            ArrayList<ArrayList<String>> cells = new ArrayList<>();
            ArrayList<String> rows;
            int row = 1;
            while (worksheet.getLastRowNum()!=row++) {
                rows = new ArrayList<>();
                for (int column = 0; column < lastColumn; column++) {
                    XSSFCell cell = worksheet.getRow(row).getCell(column);
                    if (cell != null)
                        rows.add(UtilClass.parseCell(cell));
                }
                if (!rows.isEmpty())
                    cells.add(rows);
            }
            fillTable(cells);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        int lastRow = table.getItems().size();
        int lastColumn = table.getColumns().size();
        XSSFWorkbook  workbook = new XSSFWorkbook ();
        XSSFSheet worksheet = workbook.createSheet("sheet");
        initializeWorksheet(worksheet, lastRow);
        setHeader(worksheet, lastColumn);
        XSSFCell[] cell = new XSSFCell[lastColumn];
        for (int i = 0; i < lastRow; i++) {
            FanUnit cells = table.getItems().get(i);
            for (Map.Entry<Integer, String> entry : cells.getRow().entrySet()) {
                Integer column = entry.getKey();
                String value = entry.getValue();
                cell[column] = worksheet.getRow(i + 1).createCell(column, CellType.STRING);
                if (value != null)
                    try {
                        cell[column].setCellValue(value);
                    } catch (Exception e) {
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
        FileOutputStream outFile = new FileOutputStream(saveFile.getAbsoluteFile());
        workbook.write(outFile);
        workbook.close();
    }

    private void initializeWorksheet(XSSFSheet worksheet, int lastRow) {
        for (int i = 0; i < lastRow + 1; i++) {
            worksheet.createRow(i);
        }
    }

    private void setHeader(XSSFSheet worksheet, int lastColumn) {
        XSSFCell[] cell = new XSSFCell[lastColumn];
        //XSSFCellStyle style = createStyleForTitle(workbook);
        for (int i = 0; i < lastColumn; i++) {
            cell[i] = worksheet.getRow(0).createCell(i, CellType.STRING);
           // cell[i].setCellStyle(style);
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

//    private static XSSFCellStyle createStyleForTitle(XSSFWorkbook workbook) {
//        XSSFFont font = workbook.createFont();
//        font.setBold(true);
//        XSSFCellStyle style = workbook.createCellStyle();
//        style.setFont(font);
//        return style;
//    }
}