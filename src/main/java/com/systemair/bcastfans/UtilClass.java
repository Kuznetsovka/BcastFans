package com.systemair.bcastfans;

import com.systemair.bcastfans.domain.FanUnit;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import org.apache.poi.ss.usermodel.Cell;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.concurrent.TimeUnit;

import static com.systemair.bcastfans.service.BrowserService.showAlert;

public class UtilClass {
    public static String PATH_WORK;
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
                showAlert("В ячейке " + cell.getAddress() + " найдена ошибка!", Alert.AlertType.ERROR);
                throw new IllegalArgumentException("");
        }
        return "";
    }

    public static FileOutputStream getFileOutputStream(TableView<FanUnit> table,String path) throws FileNotFoundException {
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
        PATH_WORK = System.getProperty("user.dir");
        PATH_DRIVER = "C:/ProgramData/DriverChrome/chromedriver_win32_93/chromedriver.exe";
    }

    public static String millisToShortDHMS(long duration) {
        String res;
        long days       = TimeUnit.MILLISECONDS.toDays(duration);
        long hours      = TimeUnit.MILLISECONDS.toHours(duration) -
                TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(duration));
        long minutes    = TimeUnit.MILLISECONDS.toMinutes(duration) -
                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration));
        long seconds    = TimeUnit.MILLISECONDS.toSeconds(duration) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration));

        if (days == 0)
            res = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        else
            res = String.format("%dd %02d:%02d:%02d", days, hours, minutes, seconds);
        return res;
    }
}
