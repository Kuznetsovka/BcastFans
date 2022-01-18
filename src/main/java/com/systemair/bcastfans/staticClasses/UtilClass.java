package com.systemair.bcastfans.staticClasses;

import com.systemair.bcastfans.domain.FanUnit;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class UtilClass {
    private static final String PROPERTY_FILE = "C:/ProgramData/DriverChrome/config.properties";
    private static final Logger LOGGER = Logger.getLogger(UtilClass.class.getName());
    public static String PATH_WORK;
    public static String PATH_DRIVER;

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
            PATH_DRIVER = properties.getProperty("path.driver");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(LOGGER,"Файл свойств config.property не найдет или не доступен!", Alert.AlertType.WARNING);
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

    public static void showAlert(Logger LOGGER, String alertTxt, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(rightStringCase(type.toString()));
        alert.setHeaderText("Description:");
        alert.setContentText(alertTxt);
        alert.showAndWait();
        if (type.equals(Alert.AlertType.WARNING) || type.equals(Alert.AlertType.ERROR)) {
            LOGGER.error(alertTxt);
            if (SingletonBrowserClass.getInstanceOfSingletonBrowserClass().getDriver() != null)
                SingletonBrowserClass.getInstanceOfSingletonBrowserClass().getDriver().close();
        } else if (type.equals(Alert.AlertType.INFORMATION))
            LOGGER.info(alertTxt);
    }

    private static String rightStringCase(String txt) {
        return txt.substring(0, 1).toUpperCase() + txt.substring(1).toLowerCase();
    }
}