package com.systemair.bcastfans.service;

import com.systemair.bcastfans.domain.Fan;
import com.systemair.bcastfans.domain.SubType;
import com.systemair.bcastfans.domain.TypeMontage;
import javafx.scene.control.Alert;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.openqa.selenium.By;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static com.systemair.bcastfans.UtilClass.PATH_DRIVER;

@Getter
@Setter
public class BrowserService {
    private static final String HOME_URL = "https://www.systemair.com/ru/";
    private WebDriver driver;
    private String positiveLimit;
    private String negativeLimit;
    boolean flagWarning;

    @SneakyThrows
    public void initializeBrowser() {
        System.setProperty("webdriver.chrome.driver", PATH_DRIVER);
        try {
            ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.setHeadless(false);//выбор фонового режима true
            driver = new ChromeDriver(chromeOptions);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            driver.navigate().to(HOME_URL);
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath(".//*[@id='CybotCookiebotDialogBodyLevelButtonLevelOptinAllowAll']"))).click();
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath(".//button[@data-id='2']"))).click();
            WebElement wb = driver.findElement(By.xpath(".//span[text() = 'Отрицательный допуск']/following::input[1]"));
            wb.sendKeys("-5");
            driver.findElement(By.xpath(".//span[text() = 'Отрицательный допуск']/following::input[1]")).sendKeys("-10");
            driver.findElement(By.xpath(".//span[text() = 'Положительный допуск']/following::input[1]"));
        } catch (SessionNotCreatedException e) {
            showAlert("Обновите драйвер браузера!" + "\n" + e.getRawMessage());
        } catch (IllegalArgumentException e) {
            showAlert("Драйвер не найден по уазанному пути!" + "\n" + e.getMessage());
        }
    }

    public Fan calculate(String airFlow, String airDrop, TypeMontage typeMontage, SubType subType) {
        if (typeMontage == TypeMontage.ROUND && subType == SubType.SMOKE_EXTRACT)
            showAlert("Не допустимая конфигурация, Круглых + Дымоудаление не существует!");
        if (typeMontage == TypeMontage.ROUND && subType == SubType.KITCHEN)
            showAlert("Не допустимая конфигурация, Круглых + Кухоненных не существует!");
        //selectTypeMontage(typeMontage);
//        selectSubType(subType);
//        fillingFlowAndDrop(G, P);
//        pauseWhileLoadWithWarning("sc-eXEjpC exqJWP");
//        if (flagWarning) {
//            showAlert("Не допустимая конфигурация, Круглых + Кухоненных не существует!");
//            flagWarning = false;
//            return new Fan();
//        }
//        grouping();
//        sorted();
//        fillTableUnit(currentRow, subType, typeMontage);
//        if (isDowmloadFile)
//            saveFile();
//        model = "";
        Fan fan = new Fan("","",10.0,"",100.0);
        return fan;
    }

    private void selectTypeMontage(TypeMontage typeMontage) {
        List<WebElement> listTypeMontage = driver.findElements(By.xpath(".//div[contains(@class, 'sc-ktHwxA')]"));
    }

    private void showAlert(String alertTxt) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Error");
        alert.setHeaderText("Description:");
        alert.setContentText(alertTxt);
        alert.showAndWait();
        if (driver!= null)
            driver.close();
    }
}
