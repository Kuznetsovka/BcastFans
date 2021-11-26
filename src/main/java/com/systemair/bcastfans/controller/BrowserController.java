package com.systemair.bcastfans.controller;

import com.systemair.bcastfans.domain.Fan;
import com.systemair.bcastfans.domain.FanUnit;
import com.systemair.bcastfans.service.BrowserService;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.net.URL;
import java.util.ResourceBundle;

public class BrowserController implements Initializable {
    private static final String PATH_TO_DRIVER = "C:\\ProgramData\\DriverChrome\\chromedriver.exe";
    private WebDriver driver;
    private static final String HOME_URL = "https://www.systemair.com/ru/";
    private final BrowserService browserService = new BrowserService();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeBrowser();
    }

    private void initializeBrowser() {
        System.setProperty("webdriver.chrome.driver", PATH_TO_DRIVER);
        try {
            ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.setHeadless(false);//выбор фонового режима true
            driver = new ChromeDriver(chromeOptions);
            driver.get(HOME_URL);
        } catch(SessionNotCreatedException e) {
            showAlert("Обновите драйвер браузера!" + "\n" + e.getRawMessage());
        } catch(IllegalArgumentException e) {
            showAlert("Драйвер не найден по уазанному пути!" + "\n" + e.getMessage());
        }
        System.exit(0);
        driver.close();
    }

    private void showAlert(String alertTxt) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Error");
        alert.setHeaderText("Description:");
        alert.setContentText(alertTxt);
        alert.showAndWait();
    }

    public ObservableList<FanUnit> calculate(ObservableList<FanUnit> inputData) {
        inputData.get(0).setFan(new Fan("name","model",20.0,"3",100.0));
        return inputData;
    }
}
