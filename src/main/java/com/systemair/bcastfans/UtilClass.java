package com.systemair.bcastfans;

import com.systemair.bcastfans.domain.FanUnit;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.Cell;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class UtilClass {
    public static String PATH_TEST;
    public static String PATH_DRIVER;

    public static String parseCell(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case BLANK:
                return "";
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case STRING:
                return cell.getStringCellValue();
            case ERROR:
                return ""; //TODO Вывод ошибки
        }
        return "";
    }

    @NotNull
    public static FileOutputStream getFileOutputStream(TableView<FanUnit> table) throws FileNotFoundException {
        FileChooser fileChooser = new FileChooser();
        Properties property = System.getProperties();

        fileChooser.setInitialDirectory(new File(property.getProperty("path.test")));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XLSX files (*.xlsx)", "*.xlsx"),
                new FileChooser.ExtensionFilter("XLS files (*.xls)", "*.xls"),
                new FileChooser.ExtensionFilter("ODS files (*.ods)", "*.ods"),
                new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv"),
                new FileChooser.ExtensionFilter("HTML files (*.html)", "*.html")
        );
        File saveFile = fileChooser.showSaveDialog(table.getScene().getWindow());
        return new FileOutputStream(saveFile.getAbsoluteFile());
    }

    @SneakyThrows
    public static void initProperties() {
        Properties properties = new Properties();
        String absolutePath = System.getProperty("user.dir") + "/src/main/resources/config.properties";
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(absolutePath),
                StandardCharsets.UTF_8));
        properties.load(in);
        PATH_TEST = properties.getProperty("path.test");
        PATH_DRIVER = properties.getProperty("path.driver");
    }
}
