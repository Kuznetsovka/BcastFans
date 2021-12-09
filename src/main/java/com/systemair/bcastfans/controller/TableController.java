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
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
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

@Getter
@Setter
public class TableController implements Initializable {
    @FXML
    private ImageView idImage;
    @FXML
    public TextField fieldPathDownloading;
    @FXML
    public CheckBox checkboxCustomPath;
    private TableService tableService = new TableService();
    private ExcelService excelService = new ExcelService();
    private BrowserController browserController = new BrowserController(this);
    @FXML
    public Label labelProgressBar;
    @FXML
    public ProgressBar progressBar;
    @FXML
    public TextField fieldNegativeLimit;
    @FXML
    public TextField fieldPositiveLimit;
    @FXML
    public Label labelTimeLong;
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

    private static final Logger LOGGER = Logger.getLogger(TableController.class.getName());

    UnaryOperator<TextFormatter.Change> formatter = change -> {
        if (change.getText().matches("^[0-9]$|^[0-9][0-9]$|^(100)$")) {
            return change; //else make no change
        }
        change.setText("");
        return change; //if change is a number
    };

    UnaryOperator<TextFormatter.Change> negativeFormatter = change -> {
        if(change.getText().matches("^[-]|[0]?[0-4]?[0-9]$|^(-50)$")) {
            return change;
        } else {
            change.setText("");
        }
        return change;
    };

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
        fieldNegativeLimit.setTextFormatter(new TextFormatter<>(negativeFormatter));
        fieldPositiveLimit.setTextFormatter(new TextFormatter<>(formatter));
        fieldPathDownloading.setText(PATH_WORK);
        browserController.initializeBrowser();
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        configuringDirectoryChooser(directoryChooser);
        fieldPathDownloading.setOnMouseClicked(event -> {
            Node source = (Node) event.getSource();
            Window stage = source.getScene().getWindow();
            File dir = directoryChooser.showDialog(stage);
            if (dir != null) {
                fieldPathDownloading.setText(dir.getAbsolutePath());
                PATH_WORK = dir.getAbsolutePath();
            } else {
                fieldPathDownloading.setText(null);
            }
        });
        InputStream input = getClass().getResourceAsStream("/logo.png");
        Image image = new Image(Objects.requireNonNull(input));
        idImage.setImage(image);
    }

    @SneakyThrows
    public void load() {
        workbook = excelService.loadWorkbook(table, PATH_WORK);
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
        FileOutputStream outFile = UtilClass.getFileOutputStream(table, PATH_WORK);
        if (outFile == null) return;
        workbook.write(outFile);
        outFile.close();
        workbook.close();
    }

    @SneakyThrows
    public void calculate() {
        Thread thread = new Thread(()-> {
            Instant start = Instant.now();
            data = browserController.calculate(fieldNegativeLimit, fieldPositiveLimit, data, progressBar, labelProgressBar);
            LOGGER.info("Заполнение вентиляторов в таблицу");
            tableService.fillResultData(data, table, columnModel, columnArticle, columnPower, columnPhase, columnPrice);
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

    public void fillFan(ObservableList<FanUnit> data){
        tableService.fillResultData(this.data, table, columnModel, columnArticle, columnPower, columnPhase, columnPrice);
    }

    public void clear() {
        data.clear();
        labelProgressBar.setVisible(false);
        progressBar.setProgress(0.0);
        progressBar.setVisible(false);
        labelTimeLong.setVisible(false);
    }

    public void stop() {
        browserController.stopCalculation();
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
        // Set title for DirectoryChooser
        directoryChooser.setTitle("Выберите папку для сохранения файлов");
        // Set Initial Directory
        directoryChooser.setInitialDirectory(new File(PATH_WORK));
    }
}