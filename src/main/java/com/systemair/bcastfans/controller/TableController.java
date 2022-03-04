package com.systemair.bcastfans.controller;

import com.systemair.bcastfans.MyCatchException;
import com.systemair.bcastfans.domain.*;
import com.systemair.bcastfans.service.*;
import com.systemair.bcastfans.staticClasses.UtilClass;
import com.systemair.exchangers.ExchangersApplication;
import com.systemair.exchangers.domain.exchangers.Exchanger;
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
import java.util.*;
import java.util.function.UnaryOperator;

import static com.systemair.bcastfans.staticClasses.UtilClass.PATH_WORK;
import static javafx.application.Platform.runLater;

public class TableController implements Initializable {
    public static final int CELL_SIZE = 20;
    public static final int TABLE_SIZE = 905;
    protected final ExchangersApplication exchangersApplication = new ExchangersApplication();
    private final TableService tableServiceImpl = new TableServiceImpl();
    private final ExcelService excelServiceImpl = new ExcelServiceImpl(exchangersApplication.getExchangersService());
    private CalculationService calculationServiceImpl;
    private Map<Integer, Exchanger> mapHeaters = new HashMap<>();
    private Map<Integer, Exchanger> mapCoolers = new HashMap<>();
    @FXML
    public ToggleGroup methodFillTable;
    @FXML
    public ListView<RoundModels> listRoundFans;
    @FXML
    public ListView<RectangleModels> listRectangleFans;
    @FXML
    public ListView<RoofModels> listRoofFans;
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
    private final int[] width = {34, 50, 75, 50, 120, 120, 75, 140, 60, 70, 60, 50};
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
    public TableColumn<FanUnit, String> columnDimension;
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
        calculationServiceImpl = new CalculationServiceImpl(this);
        initializeListBoxes();
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        configuringDirectoryChooser(directoryChooser);
        fieldPathDownloading.setOnMouseClicked(event -> changeFieldPathDownloading(directoryChooser, event));
        InputStream input = getClass().getResourceAsStream("/logo.png");
        Image image = new Image(Objects.requireNonNull(input));
        idImage.setImage(image);
    }

    private void initializeListBoxes() {
        initializeListBox(listRoundFans, RoundModels.values());
        initializeListBox(listRectangleFans, RectangleModels.values());
        initializeListBox(listRoofFans, RoofModels.values());
    }

    private <T> void initializeListBox(ListView<T> listBox, T[] values) {
        listBox.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listBox.setItems(FXCollections.observableArrayList(values));
        listBox.getSelectionModel().selectAll();
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
        final Workbook[] workbook = {excelServiceImpl.loadWorkbook(table.getScene().getWindow(), PATH_WORK)};
        if (workbook[0] == null) return;
        final Sheet[] worksheet = {workbook[0].getSheetAt(0)};
        new Thread(() -> {
            mapHeaters = excelServiceImpl.getHeaterExchangers(worksheet[0]);
            mapCoolers = excelServiceImpl.getCoolerExchangers(worksheet[0]);
            if (isNotNullValue(mapHeaters) || isNotNullValue(mapCoolers)) {
                worksheet[0] = calculateAndFillingAllExchanger(worksheet[0]);
            }
            ArrayList<ArrayList<String>> cells = excelServiceImpl.loadFansWorksheet(worksheet[0]);
            fillGUITableFromExcel(cells);
        }).start();
    }

    private Sheet calculateAndFillingAllExchanger(Sheet worksheet) {
        mapHeaters = calculationServiceImpl.calculationExchangers(exchangersApplication, mapHeaters, progressIndicator, labelProgressBar);
        mapCoolers = calculationServiceImpl.calculationExchangers(exchangersApplication, mapCoolers, progressIndicator, labelProgressBar);
        excelServiceImpl.fillExchangersFromGUI(worksheet, mapHeaters, mapCoolers);
        Workbook workbook = excelServiceImpl.reOpen();
        worksheet = workbook.getSheetAt(0);
        return worksheet;
    }

    private boolean isNotNullValue(Map<Integer,Exchanger> map) {
        return map.values().stream().anyMatch(Objects::nonNull);
    }

    private void fillGUITableFromExcel(ArrayList<ArrayList<String>> dataSource) {
        ArrayList<FanUnit> list = new ArrayList<>();
        for (ArrayList<String> row : dataSource) {
            list.add(new FanUnit(row));
        }
        data = FXCollections.observableArrayList(list);
        tableServiceImpl.fillInputData(data, table, columnNumberSystem, columnAirFlow, columnAirDrop, columnTypeMontage, columnSubType, columnDimension);
    }

    public void save() {
        Workbook workbook = new XSSFWorkbook();
        Sheet worksheet = workbook.createSheet("sheet");
        excelServiceImpl.createCellsInWorksheet(worksheet, table);
        excelServiceImpl.setHeader(worksheet, table);
        excelServiceImpl.fillWorksheetFromGUI(worksheet, table);
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
            if (data == null) try {
                throw new MyCatchException("Поле данных не заполнено!", Alert.AlertType.INFORMATION);
            } catch (MyCatchException e) {
                e.printStackTrace();
            }
            if (data.isEmpty()) return;
            data = calculationServiceImpl.calculate(
                    fieldNegativeLimit,
                    fieldPositiveLimit,
                    data,
                    progressIndicator,
                    labelProgressBar,
                    radioFillOne.isSelected(),
                    listRectangleFans,
                    listRoundFans,
                    listRoofFans);
            if (radioFillAll.isSelected()) {
                LOGGER.info("Заполнение вентиляторов в таблицу");
                tableServiceImpl.fillResultData(data, table, columnModel, columnArticle, columnPower, columnPhase, columnPrice);
            }
            Instant finish = Instant.now();
            String timeLong = UtilClass.millisToShortDHMS(Duration.between(start, finish).toMillis());
            LOGGER.info("Время выполнения: " + timeLong);
            Thread t2 = new Thread(() -> runLater(() -> {
                try {
                    throw new MyCatchException("Все установки посчитаны!", Alert.AlertType.INFORMATION);
                } catch (MyCatchException e) {
                    e.printStackTrace();
                }
                labelTimeLong.setText("Время выполнения: " + timeLong);
                labelTimeLong.setVisible(true);
            }));
            t2.start();
        });
        thread.start();
    }

    public void fillFan(ObservableList<FanUnit> data) {
        tableServiceImpl.fillResultData(data, table, columnModel, columnArticle, columnPower, columnPhase, columnPrice);
    }

    public void clear() {
        data.clear();
        labelProgressBar.setVisible(false);
        progressIndicator.setProgress(0.0);
        progressIndicator.setVisible(false);
        labelTimeLong.setVisible(false);
    }

    public void stop() {
        calculationServiceImpl.stopCalculation();
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

    public void initProgressBar(long count, ProgressIndicator pi, Label labelProgressBar) {
        Thread t1 = new Thread(() -> runLater(() -> {
                    pi.setProgress(0.0);
                    pi.setVisible(true);
                    labelProgressBar.setText("Посчитано 0 установок из " + count);
                    labelProgressBar.setVisible(true);
                }
        ));
        t1.start();
        t1.interrupt();
    }

    public synchronized void progressBar(int index, long size, ProgressIndicator pi, Label labelProgressBar) {
        pi.setProgress((double) (index) / size);
        labelProgressBar.setText(String.format("Посчитано %d установок из %d", index, size));
    }

    public synchronized void progressBar(int index, long size, ProgressIndicator pi, Label labelProgressBar, String type) {
        pi.setProgress((double) (index) / size);
        labelProgressBar.setText(String.format("Посчитано %d теплообменников (%s) из %d", index, type, size));
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