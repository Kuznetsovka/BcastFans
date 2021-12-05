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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
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
import java.util.function.UnaryOperator;
import java.util.logging.Logger;

import static com.systemair.bcastfans.service.BrowserService.showAlert;
import static javafx.application.Platform.runLater;

@Getter
@Setter
public class TableController implements Initializable {

    private TableService tableService = new TableService();
    private ExcelService excelService = new ExcelService();
    private BrowserController browserController = new BrowserController();
    @FXML
    public VBox vb;
    @FXML
    public Label labelProgressBar;
    @FXML
    public ProgressBar progressBar;
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

    private static final Logger LOGGER = Logger.getLogger(BrowserController.class.getName());

    UnaryOperator<TextFormatter.Change> formatter = change -> {
        if (change.getText().matches("^[0-9]$|^[0-9][0-9]$|^(100)$")) {
            return change; //if change is a number
        } else {
            change.setText(""); //else make no change
            return change;
        }
    };

//    UnaryOperator<TextFormatter.Change> negativeFormatter = change -> {
//        if(change.getText().matches("/-^[0-9]$|^[0-9][0-9]$|^(-50)$")){
//        } else if (!fieldNegativeLimit.getText().contains("-")) {
//            fieldNegativeLimit.setText("-");
//        }else {
//            change.setText("");
//        }
//        return change;
//    };

    private ObservableList<FanUnit> data;
    private Workbook workbook;

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
        //fieldNegativeLimit.setTextFormatter(new TextFormatter<>(negativeFormatter));
        fieldPositiveLimit.setTextFormatter(new TextFormatter<>(formatter));
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

    @SneakyThrows
    public void calculate() {
        Thread thread = new Thread(()-> {
            data = browserController.calculate(fieldNegativeLimit, fieldPositiveLimit, data, progressBar, labelProgressBar);
            System.out.println("Заполнение вентиляторов в таблицу");
            tableService.fillResultData(data, table, columnModel, columnArticle, columnPower, columnPhase, columnPrice);
            Thread t2 = new Thread(() -> runLater(() -> showAlert("Все установки посчитаны!", Alert.AlertType.INFORMATION)));
            t2.start();
            t2.interrupt();
        });
        thread.start();
        thread.interrupt();
    }

    public void clear() {
        data.clear();
        labelProgressBar.setVisible(false);
        progressBar.setProgress(0.0);
        progressBar.setVisible(false);
    }

    public void stop() {
        browserController.stopCalculation();
    }
}