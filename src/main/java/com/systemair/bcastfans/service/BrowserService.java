package com.systemair.bcastfans.service;

import com.systemair.bcastfans.domain.Fan;
import com.systemair.bcastfans.domain.SubType;
import com.systemair.bcastfans.domain.TypeMontage;
import javafx.scene.control.Alert;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import java.time.Duration;
import java.util.List;

import static com.systemair.bcastfans.UtilClass.PATH_DRIVER;
import static org.openqa.selenium.PageLoadStrategy.EAGER;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

@Getter
@Setter
public class BrowserService {
    private static final String HOME_URL = "https://www.systemair.com/ru/";
    private WebDriver driver;
    private Wait<WebDriver> wait;
    private String positiveLimit;
    private String negativeLimit;
    boolean flagWarning;

    @SneakyThrows
    public void initializeBrowser() {
        System.setProperty("webdriver.chrome.driver", PATH_DRIVER);
        try {
            ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.setHeadless(false);//выбор фонового режима true
            chromeOptions.setPageLoadStrategy(EAGER);
            driver = new ChromeDriver(chromeOptions);
            // Ожидание 30 секунд, опрос каждые 5 секунд
            wait = new FluentWait<>(driver)
                    .withTimeout(Duration.ofSeconds(30))
                    .pollingEvery(Duration.ofSeconds(2))
                    .ignoring(NoSuchElementException.class);
            driver.navigate().to(HOME_URL);
            clickElementIfExistsByXpath(".//*[@id='CybotCookiebotDialogBodyLevelButtonLevelOptinAllowAll']");
            clickElementIfExistsByXpath(".//button[@data-id='2']");
            clickElementIfExistsByXpath(".//div[text() = 'Дополнительные параметры поиска']/i[1]", "class","fa fa-chevron-down");
            inputTextByXpath(".//span[text() = 'Отрицательный допуск']/following::input[1]", negativeLimit);
            inputTextByXpath(".//span[text() = 'Положительный допуск']/following::input[1]", positiveLimit);
        } catch (SessionNotCreatedException e) {
            showAlert("Обновите драйвер браузера!" + "\n" + e.getRawMessage());
        } catch (IllegalArgumentException e) {
            showAlert("Драйвер не найден по уазанному пути!" + "\n" + e.getMessage());
        }
    }

    private void clickElementIfExistsByXpath(String xpath,String ... attributeAndValue) {
        By by = By.xpath(xpath);
        wait.until(visibilityOfElementLocated(by));
        if (attributeAndValue.length > 0) {
            String attribute = attributeAndValue[0];
            String value = attributeAndValue[1];
            if (driver.findElement(by).getAttribute(attribute).equals(value)) return;
        }
        wait.until(ExpectedConditions.elementToBeClickable(by)).click();
    }

    private void inputTextByXpath(String xpath,String newValue) {
        WebElement wb = driver.findElement(By.xpath(xpath));
        if (wb.getText().equals(newValue)) return;
        wb.sendKeys(Keys.CONTROL + "a");
        wb.sendKeys(Keys.DELETE);
        wb.sendKeys(newValue);
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
