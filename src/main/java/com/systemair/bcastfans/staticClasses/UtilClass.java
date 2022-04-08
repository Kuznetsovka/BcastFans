package com.systemair.bcastfans.staticClasses;

import com.systemair.bcastfans.MyCatchException;
import com.systemair.bcastfans.domain.FanUnit;
import com.systemair.bcastfans.macroses.FindByTwoCriteria;
import com.systemair.bcastfans.macroses.Polinom;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import org.apache.log4j.Logger;
import org.apache.poi.ss.formula.functions.FreeRefFunction;
import org.apache.poi.ss.formula.udf.AggregatingUDFFinder;
import org.apache.poi.ss.formula.udf.DefaultUDFFinder;
import org.apache.poi.ss.formula.udf.UDFFinder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.round;
import static javafx.application.Platform.runLater;

public class UtilClass {
    private static final String PROPERTY_FILE = "C:/ProgramData/DriverChrome/config.properties";
    private static final Logger LOGGER = Logger.getLogger(UtilClass.class.getName());
    public static String PATH_WORK;
    public static String CHROME_DRIVER;
    public static String EDGE_DRIVER;
    public static String SECURITY_CODE;
    public static String BROWSER;
    public static final int MAX_LIMIT_TIMEOUT = 40;
    public static final int LIMIT_REPEAT_TIMEOUT = 500;

    public static FileOutputStream getFileOutputStream(TableView<FanUnit> table, String path) throws FileNotFoundException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(path));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XLSX files (*.xlsx)", "*.xlsx"),
                new FileChooser.ExtensionFilter("XLS files (*.xls)", "*.xls")
        );
        File saveFile = fileChooser.showSaveDialog(table.getScene().getWindow());
        if (saveFile == null) return null;
        return new FileOutputStream(saveFile.getAbsoluteFile());
    }


    public static void initProperties() {
        Properties properties = new Properties();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(PROPERTY_FILE),
                    StandardCharsets.UTF_8));
            properties.load(in);
            PATH_WORK = properties.getProperty("path.work");
            CHROME_DRIVER = properties.getProperty("path.driver");
            EDGE_DRIVER = properties.getProperty("path.edge.driver");
            BROWSER = properties.getProperty("type.browser");
            SECURITY_CODE = properties.getProperty("security.password");
        } catch (IOException e) {
            e.printStackTrace();
            try {
                throw new MyCatchException("Файл свойств config.property не найдет или не доступен!", Alert.AlertType.WARNING);
            } catch (MyCatchException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static String millisToShortDHMS(long duration) {
        String res;
        long days = TimeUnit.MILLISECONDS.toDays(duration);
        long hours = TimeUnit.MILLISECONDS.toHours(duration) -
                TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(duration));
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration) -
                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration));
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration));

        if (days == 0)
            res = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        else
            res = String.format("%dd %02d:%02d:%02d", days, hours, minutes, seconds);
        return res;
    }


    public static String getCorrectSavePath(String path, String name, String model) {
        String fileName = name + " " + model + ".pdf";
        fileName = fileName.replaceAll("[^а-яА-Яa-zA-Z0-9 .\\-]", "_");
        return path + "/" + fileName;
    }

    public static void downloadUsingNIO(String urlStr, String file) {
        try {
            URL url = new URL(urlStr);
            ReadableByteChannel rbc = Channels.newChannel(url.openStream());
            FileOutputStream fos = new FileOutputStream(file);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
            rbc.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }
    }

    public static String parseCell(Cell cell) {
        try {
            if (cell == null) return "";
            switch (cell.getCellType()) {
                case BLANK:
                    return "";
                case BOOLEAN:
                    return String.valueOf(cell.getBooleanCellValue());
                case NUMERIC:
                    return String.valueOf(round(cell.getNumericCellValue()));
                case STRING:
                    return cell.getStringCellValue();
                case FORMULA:
                    FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
                    evaluator.evaluateFormulaCell(cell);

                    evaluator.evaluate(cell);
                    return String.valueOf(round(cell.getNumericCellValue()));
                case ERROR:
                    throw new MyCatchException("В ячейке " + cell.getAddress() + " найдена ошибка!", Alert.AlertType.WARNING);
            }
        } catch (Exception e) {
            try {
                throw new MyCatchException("Ошибка считывания данных, не должно быть формул" + "В ячейке " + cell.getAddress() + " найдена ошибка!", Alert.AlertType.ERROR);
            } catch (MyCatchException ex) {
                ex.printStackTrace();
            }
        }
        return "";
    }

    public static void registryCustomFunction(Workbook workbook) {
        String[] functionNames = {"findByTwoCriteria","polinom"};
        FreeRefFunction[] functionImpls = {new FindByTwoCriteria(),new Polinom()};
        UDFFinder udfs = new DefaultUDFFinder(functionNames, functionImpls);
        UDFFinder udfToolpack = new AggregatingUDFFinder(udfs);
        workbook.addToolPack(udfToolpack);
    }

    public static void showAlert(String alertTxt, Alert.AlertType type) {
        new Thread(() -> runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(rightStringCase(type.toString()));
            alert.setHeaderText("Description:");
            alert.setContentText(alertTxt);
            alert.showAndWait();
        })).start();
    }

    private static String rightStringCase(String txt) {
        return txt.substring(0, 1).toUpperCase() + txt.substring(1).toLowerCase();
    }
}
