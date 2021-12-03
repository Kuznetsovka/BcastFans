package com.systemair.bcastfans;

import com.systemair.bcastfans.domain.FanUnit;
import javafx.scene.control.TableView;
import javafx.scene.control.TextFormatter;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.Cell;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.function.UnaryOperator;
@Getter
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

    public static FileOutputStream getFileOutputStream(TableView<FanUnit> table) throws FileNotFoundException {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setInitialDirectory(new File(PATH_TEST));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XLSX files (*.xlsx)", "*.xlsx"),
                new FileChooser.ExtensionFilter("XLS files (*.xls)", "*.xls")
        );
        File saveFile = fileChooser.showSaveDialog(table.getScene().getWindow());
        if (saveFile == null) return null;
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

    public static UnaryOperator<TextFormatter.Change> filter = change -> {
        if(change.getControlNewText().matches("\\d{0,11}")) {
            return change;
        }

        return null;
    };

    public static StringConverter<String> converter = new StringConverter<>() {
        @Override
        public String toString(String s) {
            if(s == null || s.isBlank()) return "";

            if(s.matches("\\d{3}")) {
                return s;
            }

            return "";
        }

        @Override
        public String fromString(String s) {
            if(s == null || s.isBlank()) return "";

            if(s.matches("\\d{3}")) {
                return s;
            }
            throw new RuntimeException("Converter error");
        }
    };
}
