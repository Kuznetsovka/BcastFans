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
import static com.systemair.bcastfans.domain.TypeMontage.ROUND;
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
            prepareStartPageBeforeCalculation();
        } catch (SessionNotCreatedException e) {
            showAlert("Обновите драйвер браузера!" + "\n" + e.getRawMessage());
        } catch (IllegalArgumentException e) {
            showAlert("Драйвер не найден по уазанному пути!" + "\n" + e.getMessage());
        }
    }

    private void prepareStartPageBeforeCalculation() {
        // Согласие на добавление Cookies
        clickElementIfExistsByXpath(".//*[@id='CybotCookiebotDialogBodyLevelButtonLevelOptinAllowAll']");
        // Нажатие на вкладку  Подбор
        clickElementIfExistsByXpath(".//button[@data-id='2']");
        // Открытие вкладки Дополнительные параметры поиска
        clickElementIfExistsByXpath(".//div[text() = 'Дополнительные параметры поиска']/i[1]", "class","fa fa-chevron-down");
        // Внесение данных Отрицательный допуск
        inputTextByXpath(".//span[text() = 'Отрицательный допуск']/following::input[1]", negativeLimit);
        // Внесение данных Положительный допуск
        inputTextByXpath(".//span[text() = 'Положительный допуск']/following::input[1]", positiveLimit);
        // Проверка и изменение единиц измерения Расход воздуха на м³/ч
        changeValueComboBox(".//span[text() = 'Расход воздуха']/following::div[1]/div[1]","//span[1]","м³/ч");
        // Проверка и изменение единиц измерения Внешнее давление на Па
        changeValueComboBox(".//span[text() = 'Внешнее давление']/following::div[1]/div[1]","//span[1]","Па");
        // Проверка и изменение значения Частота на 50 Гц
        changeValueComboBox(".//span[text() = 'Частота']/following::div[1]/div[1]","//span[1]","50 Гц ");
        // Проверка и изменение значения Регулятор скорости на По умолчанию
        changeValueComboBox(".//span[text() = 'Регулятор скорости']/following::div[1]/*/div[1]","//span[1]","По умолчанию ");
        // Проверка и изменение единиц измерения Макс. температура воздуха на °С
        changeValueComboBox(".//span[text() = 'Макс. температура воздуха']/following::div[1]/*/div[1]","//span[1]","°C");
        // Проверка и изменение значения Макс. температура воздуха на 40
        inputTextByXpath(".//span[text() = 'Макс. температура воздуха']/following::input[1]","40");
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
        if (typeMontage == ROUND && subType == SubType.SMOKE_EXTRACT)
            showAlert("Не допустимая конфигурация, Круглых + Дымоудаление не существует!");
        if (typeMontage == ROUND && subType == SubType.KITCHEN)
            showAlert("Не допустимая конфигурация, Круглых + Кухоненных не существует!");
        selectTypeMontage(typeMontage);
        selectSubType(subType);
        fillFlowAndDrop(airFlow, airDrop);
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

    private void fillFlowAndDrop(String airFlow, String airDrop) {
        inputTextByXpath(".//span[text() = 'Расход воздуха']/following::input[1]", airFlow);
        inputTextByXpath(".//span[text() = 'Внешнее давление']/following::input[1]", airDrop);
        clickElementIfExistsByXpath("(.//button[@class='sc-bxivhb SWiNZ'])[2]");
    }

    private void changeValueComboBox(String xpath, String checkingXpath ,String newValue){
        By by = By.xpath(xpath);
        WebElement checkingWb = driver.findElement(By.xpath(xpath + checkingXpath));
        if (checkingWb.getText().equals(newValue)) return;
        wait.until(ExpectedConditions.elementToBeClickable(by)).click();
        List<WebElement> list = driver.findElements(By.xpath(".//div[@class='sc-bZQynM eMtmfk']//div"));
        WebElement changingElement = list.stream().filter(webElement -> webElement.getText().equals(newValue)).findFirst().get();
        wait.until(ExpectedConditions.elementToBeClickable(changingElement)).click();
    }

    private void selectSubType(SubType subType) {
        /*
            0 - все
            1 - стандартные
            2 - 120°С Кухонные
            3 - 200°С Кухонные
            4 - 300°С Дымоудаление
            5 - 400°С Дымоудаление
            6 - 600°С Дымоудаление
            7 - Взывозащита
            8 - Агрессивная среда
            9 - Агрессивная среда + Взывозащита
         */
        List<WebElement> listSubType = driver.findElements(By.xpath(".//div[contains(@class, 'sc-ktHwxA')]"));
        List<WebElement> listSilentEC = driver.findElements(By.xpath("//div[contains(@class, 'sc-tilXH')]"));
        switch (subType){
            case NONE:
                onCheckbox(false,listSilentEC.get(0));
                onCheckbox(false,listSilentEC.get(1));
                break;
            case KITCHEN:
                listSubType.get(2).click();
                onCheckbox(false,listSilentEC.get(0));
                onCheckbox(false,listSilentEC.get(1));
                break;
            case KITCHEN_AND_EC:
                listSubType.get(2).click();
                onCheckbox(true,listSilentEC.get(0));
                onCheckbox(false,listSilentEC.get(1));
                break;
            case EC:
                listSubType.get(1).click();
                onCheckbox(true,listSilentEC.get(0));
                onCheckbox(false,listSilentEC.get(1));
                break;
            case SILENT:
                listSubType.get(1).click();
                onCheckbox(false,listSilentEC.get(0));
                onCheckbox(true,listSilentEC.get(1));
                break;
            case SILENT_AND_EC:
                listSubType.get(1).click();
                onCheckbox(true,listSilentEC.get(0));
                onCheckbox(true,listSilentEC.get(1));
                break;
            case ON_ROOF:
                listSubType.get(1).click();
                onCheckbox(false,listSilentEC.get(0));
                onCheckbox(false,listSilentEC.get(1));
                break;
            case SMOKE_EXTRACT:
                listSubType.get(5).click();
                onCheckbox(false,listSilentEC.get(0));
                onCheckbox(false,listSilentEC.get(1));
                break;
        }

    }

    private void onCheckbox(boolean onAction, WebElement webElement) {
        /*
        "kClLXW" - выкл.
        "eITjnS" - вкл.
         */
        if (onAction) {
            if (webElement.getAttribute("class").contains("kClLXW")) webElement.click();
        } else {
            if (webElement.getAttribute("class").contains("eITjnS")) webElement.click();
        }
    }

    private void selectTypeMontage(TypeMontage typeMontage) {
        /*
            Круглые - 0
            Прямоугольные - 1
            Крышные - 2
            Осевые - 3
            Центробежные - 4
        */
        List<WebElement> listTypeMontage = driver.findElements(By.xpath(".//div[contains(@class, 'sc-feJyhm')]"));
        switch (typeMontage) {
            case ROUND:
                selectTypeFan(0,listTypeMontage);
                break;
            case RECTANGLE:
                selectTypeFan(1,listTypeMontage);
                break;
            case ROOF:
                selectTypeFan(2,listTypeMontage);
                break;
            case ROUND_AND_RECTANGLE:
                selectTwoTypeFan(0, 1, listTypeMontage);
                break;
        }

    }

    private void selectTwoTypeFan(int i1, int i2, List<WebElement> list) {
        /*
            "gHdNtY" - вкл.
            "cxjQFd" - выкл.
         */
        for (int i = 0; i < list.size() ; i++) {
            if (i == i1 || i == i2) {
                if (list.get(i).getAttribute("class").contains("cxjQFd"))
                    list.get(i).click();
            } else {
                if (list.get(i).getAttribute("class").contains("gHdNtY"))
                    list.get(i).click();
            }
        }
    }

    private void selectTypeFan(int index, List<WebElement> list) {
        /*
        "gHdNtY" - вкл.
        "cxjQFd" - выкл.
         */
        for (int i = 0; i < list.size() ; i++) {
            if (i == index) {
                if (list.get(i).getAttribute("class").contains("cxjQFd"))
                    list.get(i).click();
            } else {
                if (list.get(i).getAttribute("class").contains("gHdNtY"))
                    list.get(i).click();
            }
        }
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
