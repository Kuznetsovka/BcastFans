package com.systemair.bcastfans.controller;

import com.systemair.bcastfans.UtilClass;
import com.systemair.bcastfans.domain.FanUnit;
import com.systemair.bcastfans.domain.SubType;
import com.systemair.bcastfans.domain.TypeMontage;
import com.systemair.bcastfans.service.CalculationService;
import com.systemair.bcastfans.service.ExcelService;
import com.systemair.bcastfans.service.TableService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

import static com.systemair.bcastfans.UtilClass.PATH_WORK;
import static com.systemair.bcastfans.service.BrowserService.showAlert;
import static javafx.application.Platform.runLater;

public class TableController implements Initializable {
    private final TableService tableService = new TableService();
    private final ExcelService excelService = new ExcelService();
    private final CalculationService calculationService = new CalculationService(this);
    @FXML
    public ToggleGroup methodFillTable;
    @FXML
    private CheckBox isSaveTechData;
    @FXML
    private RadioButton radioFillOne;
    @FXML
    private RadioButton radioFillAll;
    @FXML
    private ImageView idImage;
    @FXML
    private TextField fieldPathDownloading;
    @FXML
    private CheckBox checkboxCustomPath;
    @FXML
    private Label labelProgressBar;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private TextField fieldNegativeLimit;
    @FXML
    private TextField fieldPositiveLimit;
    @FXML
    private Label labelTimeLong;
    @FXML
    private TableView<FanUnit> table;
    @FXML
    private CheckBox checkBox;
    @FXML
    private TableColumn<FanUnit, Boolean> columnChoose;
    @FXML
    private TableColumn<FanUnit, String> columnNumberSystem;
    @FXML
    private TableColumn<FanUnit, String> columnAirFlow;
    @FXML
    private TableColumn<FanUnit, String> columnAirDrop;
    @FXML
    private TableColumn<FanUnit, TypeMontage> columnTypeMontage;
    @FXML
    private TableColumn<FanUnit, SubType> columnSubType;
    @FXML
    private TableColumn<FanUnit, String> columnModel;
    @FXML
    private TableColumn<FanUnit, String> columnArticle;
    @FXML
    private TableColumn<FanUnit, String> columnPower;
    @FXML
    private TableColumn<FanUnit, String> columnPhase;
    @FXML
    private TableColumn<FanUnit, String> columnPrice;

    private static final Logger LOGGER = Logger.getLogger(TableController.class.getName());

    UnaryOperator<TextFormatter.Change> formatter = change -> {
        if (change.getText().matches("^[0-9]$|^[0-9][0-9]$|^(100)$")) {
            return change; //else make no change
        }
        change.setText("");
        return change; //if change is a number
    };

    UnaryOperator<TextFormatter.Change> negativeFormatter = change -> {
        if (change.getText().matches("^[-]|[0]?[0-4]?[0-9]$|^(-50)$")) {
            return change;
        } else {
            change.setText("");
        }
        return change;
    };

    private ObservableList<FanUnit> data;

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
        fieldNegativeLimit.setTextFormatter(new TextFormatter<>(negativeFormatter));
        fieldPositiveLimit.setTextFormatter(new TextFormatter<>(formatter));
        fieldPathDownloading.setText(PATH_WORK);
        calculationService.initializeBrowser();
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        configuringDirectoryChooser(directoryChooser);
        fieldPathDownloading.setOnMouseClicked(event -> changeFieldPathDownloading(directoryChooser, event));
        InputStream input = getClass().getResourceAsStream("/logo.png");
        Image image = new Image(Objects.requireNonNull(input));
        idImage.setImage(image);
    }

    private void changeFieldPathDownloading(DirectoryChooser directoryChooser, MouseEvent event) {
        Node source = (Node) event.getSource();
        Window stage = source.getScene().getWindow();
        File dir = directoryChooser.showDialog(stage);
        if (dir != null) {
            fieldPathDownloading.setText(dir.getAbsolutePath());
            PATH_WORK = dir.getAbsolutePath();
        } else {
            fieldPathDownloading.setText(null);
        }
    }

    public void load() {
        Workbook workbook = excelService.loadWorkbook(table, PATH_WORK);
        if (workbook == null) return;
        Sheet worksheet = workbook.getSheetAt(0);
        ArrayList<ArrayList<String>> cells = excelService.loadCellsFromWorksheet(worksheet);
        fillGUITableFromExcel(cells);
    }

    private void fillGUITableFromExcel(ArrayList<ArrayList<String>> dataSource) {
        ArrayList<FanUnit> list = new ArrayList<>();
        for (ArrayList<String> row : dataSource) {
            list.add(new FanUnit(row));
        }
        data = FXCollections.observableArrayList(list);
        tableService.fillInputData(data, table, columnNumberSystem, columnAirFlow, columnAirDrop, columnTypeMontage, columnSubType);
    }

    public void save() {
        Workbook workbook = new XSSFWorkbook();
        Sheet worksheet = workbook.createSheet("sheet");
        excelService.createCellsInWorksheet(worksheet, table);
        excelService.setHeader(worksheet, table);
        excelService.fillWorksheetFromGUI(worksheet, table);
        try {
            FileOutputStream outFile = UtilClass.getFileOutputStream(table, PATH_WORK);
            if (outFile == null) return;
            workbook.write(outFile);
            outFile.close();
            workbook.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }
    }

    public void calculate() {
        Thread thread = new Thread(() -> {
            Instant start = Instant.now();
            if (data.isEmpty()) return;

            data = calculationService.calculate(fieldNegativeLimit, fieldPositiveLimit, data, progressIndicator, labelProgressBar, radioFillOne.isSelected());
            if (radioFillAll.isSelected()) {
                LOGGER.info("Заполнение вентиляторов в таблицу");
                tableService.fillResultData(data, table, columnModel, columnArticle, columnPower, columnPhase, columnPrice);
            }
            Instant finish = Instant.now();
            String timeLong = UtilClass.millisToShortDHMS(Duration.between(start, finish).toMillis());
            LOGGER.info("Время выполнения: " + timeLong);
            Thread t2 = new Thread(() -> runLater(() -> {
                showAlert("Все установки посчитаны!", Alert.AlertType.INFORMATION);
                labelTimeLong.setText("Время выполнения: " + timeLong);
                labelTimeLong.setVisible(true);
            }));
            t2.start();
        });
        thread.start();
    }

    public void fillFan(ObservableList<FanUnit> data) {
        tableService.fillResultData(data, table, columnModel, columnArticle, columnPower, columnPhase, columnPrice);
    }

    public void clear() {
        data.clear();
        labelProgressBar.setVisible(false);
        progressIndicator.setProgress(0.0);
        progressIndicator.setVisible(false);
        labelTimeLong.setVisible(false);
    }

    public void stop() {
        calculationService.stopCalculation();
    }

    public void customPath() {
        if (checkboxCustomPath.isSelected()) {
            fieldPathDownloading.setEditable(false);
            fieldPathDownloading.setDisable(true);
            fieldPathDownloading.setText(PATH_WORK);
        } else {
            fieldPathDownloading.setDisable(false);
            fieldPathDownloading.setEditable(true);
            fieldPathDownloading.setText("");
        }

    }

    private void configuringDirectoryChooser(DirectoryChooser directoryChooser) {
        directoryChooser.setTitle("Выберите папку для сохранения файлов");
        directoryChooser.setInitialDirectory(new File(PATH_WORK));
    }

    public TextField getFieldPathDownloading() {
        return fieldPathDownloading;
    }

    public CheckBox isSaveTechData() {
        return isSaveTechData;
    }
}