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
import static java.lang.Thread.sleep;
import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

@Getter
@Setter
public class BrowserService {
    private static final String HOME_URL = "https://www.systemair.com/ru/";
    private WebDriver driver;
    private Wait<WebDriver> wait;
    private static final int MAX_LIMIT_TIMEOUT = 30;
    private static final int LIMIT_REPEAT_TIMEOUT = 2;
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
            // Ожидание 30 секунд, опрос каждые 5 секунд
            wait = new FluentWait<>(driver)
                    .withTimeout(Duration.ofSeconds(MAX_LIMIT_TIMEOUT))
                    .pollingEvery(Duration.ofSeconds(LIMIT_REPEAT_TIMEOUT))
                    .ignoring(NoSuchElementException.class,ElementClickInterceptedException.class);
            driver.navigate().to(HOME_URL);
            prepareStartPageBeforeCalculation();
        } catch (SessionNotCreatedException e) {
            showAlert("Обновите драйвер браузера!" + "\n" + e.getRawMessage());
        } catch (IllegalArgumentException e) {
            showAlert("Драйвер не найден по указанному пути!" + "\n" + e.getMessage());
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
        inputTextByLabel("Отрицательный допуск", negativeLimit);
        // Внесение данных Положительный допуск
        inputTextByLabel("Положительный допуск", positiveLimit);
        // Проверка и изменение единиц измерения Расход воздуха на м³/ч
        changeValueComboBox("Расход воздуха","м³/ч");
        // Проверка и изменение единиц измерения Внешнее давление на Па
        changeValueComboBox("Внешнее давление","Па");
        // Проверка и изменение значения Частота на 50 Гц
        changeValueComboBox("Частота","50 Гц");
        // Проверка и изменение значения Регулятор скорости на По умолчанию
        changeValueComboBox("Регулятор скорости","По умолчанию");
        // Проверка и изменение единиц измерения Макс. температура воздуха на °С
        changeValueComboBox("Макс. температура воздуха","°C");
        // Проверка и изменение значения Макс. температура воздуха на 40
        inputTextByLabel("Макс. температура воздуха","40");
    }

    private void clickElementIfExistsByXpath(String xpath,String ... attributeAndValue) {
        By by = By.xpath(xpath);
        wait.until(visibilityOfElementLocated(by));
        if (attributeAndValue.length > 0) {
            String attribute = attributeAndValue[0];
            String value = attributeAndValue[1];
            if (getWebElementByXpath(xpath).getAttribute(attribute).equals(value)) return;
        }
        wait.until(elementToBeClickable(by)).click();
    }

    @SneakyThrows
    private void inputTextByLabel(String findTextLabel, String newValue) {
        String xpath = ".//span[text() = '" + findTextLabel + "']/following::input[1]";
        WebElement wb = getWebElementByXpath(xpath);
        if (wb.getText().equals(newValue)) return;
        wb.sendKeys(Keys.CONTROL + "a");
        wb.sendKeys(Keys.DELETE);
        wb.sendKeys(newValue);
        sleep(500);
    }

    public Fan calculate(String airFlow, String airDrop, TypeMontage typeMontage, SubType subType) {
        if (typeMontage == ROUND && subType == SubType.SMOKE_EXTRACT)
            showAlert("Не допустимая конфигурация, Круглых + Дымоудаление не существует!");
        if (typeMontage == ROUND && subType == SubType.KITCHEN)
            showAlert("Не допустимая конфигурация, Круглых + Кухоненных не существует!");
        selectTypeMontage(typeMontage);
        selectSubType(subType);
        fillFlowAndDrop(airFlow, airDrop);
        if (flagWarning) {
            //showAlert("Не допустимая конфигурация!"); //TODO Подумать может не кидать Warning, а идти дайльше
            flagWarning = false;
            return new Fan();
        }
        grouping();
        hidingDiagram();
        sorting();
//        fillTableUnit(currentRow, subType, typeMontage);
//        if (isDownloadFile)
//            saveFile();
        Fan fan = new Fan();
        return fan;
    }

    private void sorting() {
        changeValueComboBox("Сортировать по:","Цена (По возрастающей)");
    }

    private void hidingDiagram() {
        // Скрыть диаграммы
        onCheckboxDiagram(getWebElementByXpath(".//div[contains(@class, 'sc-cMljjf')]"));
    }

    private void grouping() {
        changeValueComboBox("Группировать по:","Нет");
    }

    private void fillFlowAndDrop(String airFlow, String airDrop) {
        inputTextByLabel("Расход воздуха", airFlow);
        inputTextByLabel("Внешнее давление", airDrop);
        clickElementIfExistsByXpath("(.//button[@class='sc-bxivhb SWiNZ'])[2]");
        if(isWarning())
            flagWarning = true;
    }

    @SneakyThrows
    private boolean isWarning() {
        return isExist(By.xpath(".//span[@type='warning']"));
    }

    private boolean isExist(By by) {
        boolean isExists;
        try {
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(1));
            isExists = driver.findElements(by).size() > 0;
        } finally {
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(MAX_LIMIT_TIMEOUT));
        }
        return isExists;
    }

    private WebElement getWebElementByXpath(String xpath){
        List<WebElement> webElements = wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath(xpath),0));
        if (webElements.size() == 0) return null;
        return webElements.get(0);
    }

    private void changeValueComboBox(String findTextLabel, String newValue) {
        String xpath = ".//span[text() = '" + findTextLabel + "']/following::div[1]//span[1]";
        By by = By.xpath(xpath + "/ancestor::div[1]");
        WebElement checkingWb = getWebElementByXpath(xpath);
        if (checkingWb == null) return;
        if (checkingWb.getText().equals(newValue)) return;
        wait.until(visibilityOfElementLocated(by));
        wait.until(elementToBeClickable(by)).click();
        List<WebElement> list = getListDivsByNameClass("sc-gzVnrw");
        WebElement changingElement = list.stream().filter(webElement -> webElement.getText().trim().equals(newValue)).findAny().orElse(null);
        if (changingElement == null) showAlert("Запрос " + xpath + " не дал результата! Значение " + newValue + " не было найдено в списке!");
        wait.until(elementToBeClickable(changingElement)).click();
    }

    private List<WebElement> getListDivsByNameClass(String xpathLists) {
        return driver.findElements(By.xpath(".//div[contains(@class, '" + xpathLists + "')]"));
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
            9 - Агрессивная среда + Взрывозащита
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
                onCheckbox(false,listSilentEC.get(0));
                onCheckbox(true,listSilentEC.get(1));
                break;
            case EC:
                listSubType.get(1).click();
                onCheckbox(false,listSilentEC.get(0));
                onCheckbox(true,listSilentEC.get(1));
                break;
            case SILENT:
                listSubType.get(1).click();
                onCheckbox(true,listSilentEC.get(0));
                onCheckbox(false,listSilentEC.get(1));
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
            if (isContainsInClass(webElement,"kClLXW")) webElement.click();
        } else {
            if (isContainsInClass(webElement,"eITjnS")) webElement.click();
        }
    }

    private void onCheckboxDiagram(WebElement webElement) {
        /*
        "ineogT" - выкл.
        "hBEpsK" - вкл.
         */
        if (isContainsInClass(webElement,"hBEpsK")) webElement.click();
    }

    private boolean isContainsInClass(WebElement webElement, String text) {
        return webElement.getAttribute("class").contains(text);
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
                    wait.until(elementToBeClickable(list.get(i))).click();
            } else {
                if (list.get(i).getAttribute("class").contains("gHdNtY"))
                    wait.until(elementToBeClickable(list.get(i))).click();
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
                    wait.until(elementToBeClickable(list.get(i))).click();
            } else {
                if (list.get(i).getAttribute("class").contains("gHdNtY"))
                    wait.until(elementToBeClickable(list.get(i))).click();
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
