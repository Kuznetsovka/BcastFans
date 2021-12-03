package com.systemair.bcastfans.service;

import com.systemair.bcastfans.SingletonBrowserClass;
import com.systemair.bcastfans.domain.Fan;
import com.systemair.bcastfans.domain.SubType;
import com.systemair.bcastfans.domain.TypeMontage;
import javafx.scene.control.Alert;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.systemair.bcastfans.SingletonBrowserClass.MAX_LIMIT_TIMEOUT;
import static com.systemair.bcastfans.domain.TypeMontage.ROUND;
import static java.lang.Thread.sleep;
import static org.openqa.selenium.support.ui.ExpectedConditions.*;

@Getter
@Setter
public class BrowserService {
    private static final SingletonBrowserClass sbc = SingletonBrowserClass.getInstanceOfSingletonBrowserClass();
    private String positiveLimit;
    private String negativeLimit;
    boolean flagWarning;

    public synchronized void prepareStartPageBeforeCalculation() {
        try {
            // Внесение данных Отрицательный допуск
            inputTextByLabel("Отрицательный допуск", negativeLimit);
            // Внесение данных Положительный допуск
            inputTextByLabel("Положительный допуск", positiveLimit);
            // Проверка и изменение значения Макс. температура воздуха на 40
            inputTextByLabel("Макс. температура воздуха", "40");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Проверка и изменение единиц измерения Расход воздуха на м³/ч
        changeValueComboBoxByLabel("Расход воздуха", "м³/ч");
        // Проверка и изменение единиц измерения Внешнее давление на Па
        changeValueComboBoxByLabel("Внешнее давление", "Па");
        // Проверка и изменение значения Частота на 50 Гц
        changeValueComboBoxByLabel("Частота", "50 Гц");
        // Проверка и изменение значения Регулятор скорости на По умолчанию
        changeValueComboBoxByLabel("Регулятор скорости", "По умолчанию");
        // Проверка и изменение единиц измерения Макс. температура воздуха на °С
        changeValueComboBoxByLabel("Макс. температура воздуха", "°C");

    }

    private synchronized void clickElementIfExistsByXpath(String xpath, String... attributeAndValue) {
        try {
            By by = By.xpath(xpath);
            sbc.getWait().until(visibilityOfElementLocated(by));
            if (attributeAndValue.length > 0) {
                String attribute = attributeAndValue[0];
                String value = attributeAndValue[1];
                if (getWebElementByXpath(xpath).getAttribute(attribute).equals(value)) return;
            }
            sbc.getWait().until(elementToBeClickable(by)).click();
        } catch (ElementClickInterceptedException e) {
            e.printStackTrace();
        }
    }
    private synchronized void clickElementWithScroll(WebElement webElement) {
        ((JavascriptExecutor) sbc.getDriver()).executeScript("arguments[0].scrollIntoView(true);", webElement);
        sbc.getWait().until(elementToBeClickable(webElement)).click();
    }

    private synchronized void inputTextByLabel(String findTextLabel, String newValue) throws InterruptedException {
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
            showAlert("Не допустимая конфигурация, Круглых + Дымоудаление не существует!", Alert.AlertType.WARNING);
        if (typeMontage == ROUND && subType == SubType.KITCHEN)
            showAlert("Не допустимая конфигурация, Круглых + Кухоненных не существует!", Alert.AlertType.WARNING);
        selectTypeMontage(typeMontage);
        selectSubType(subType);
        fillFlowAndDrop(airFlow, airDrop);
        if (flagWarning) {
            //showAlert("Не допустимая конфигурация!"); //TODO Подумать может не кидать Warning, а идти дальше
            flagWarning = false;
            return new Fan();
        }
        grouping();
        hidingDiagram();
        sorting();
        Fan fan = fillTableUnit(subType);
        return (fan != null) ? fan : new Fan();
    }

    private Fan fillTableUnit(SubType subType) {
        By moreFansButtonBy = By.xpath(".//button[@class='sc-bxivhb SWiNZ']");
        WebElement btnMoreUnit;
        Fan result = null;
        List<WebElement> row;
        //changeValueComboBoxByVerticalLabel("sc-htoDjs cnEpLn", "Вт");
        int countRow = 1;
        int lastRows = 0;
        do {
            if (countRow > lastRows)
                lastRows = sbc.getDriver().findElements(By.xpath(".//table[@class='sc-Rmtcm djcDFD']/tbody/tr[@class='sc-bRBYWo hmjjYh']")).size();
            row = sbc.getDriver().findElements(By.xpath(".//table[@class='sc-Rmtcm djcDFD']/tbody/tr[" + countRow + "]/td[contains(@class,'sc-jhAzac')]"));
            String price = row.get(4).getText();
            String model = row.get(2).findElement(By.tagName("a")).getText();

            if (isContinueFan(price,subType,model)){
                countRow++;
                continue;
            }

            if (isExistElementMoreThen(moreFansButtonBy, 2) && countRow == lastRows) {
                btnMoreUnit = sbc.getWait().until(visibilityOfAllElementsLocatedBy(moreFansButtonBy)).get(2);
                sbc.getWait().until(elementToBeClickable(btnMoreUnit)).click();
                countRow += lastRows;
            }
            result = getResultFan(row, countRow);
        } while (result == null);
        return result;
    }

    private boolean isContinueFan(String price, SubType subType, String model) {
        return ( (price.equals("") ) ||
                ( subType == SubType.ON_ROOF && (!model.contains("RVK") && !model.contains("MUB")) ) ||
                ( model.contains("150") ) );
    }

    @SneakyThrows
    private Fan getResultFan(List<WebElement> row, int index) {
        String model = row.get(2).findElement(By.tagName("a")).getText();
        String phase = row.get(2).findElement(By.tagName("small")).getText();
        String article = row.get(3).getText();
        String price = row.get(4).getText();
        String power = row.get(7).getText();
        //WebElement wb = row.get(1).findElement(By.tagName("button"));
        By by = By.xpath(".//button[@class = 'sc-bxivhb kcrVkO']");
//        WebElement wb = sbc1.getDriver().findElements(by).get(0).findElements(By.tagName("i")).get(1);
//        sbc1.getWait().until(visibilityOfElementLocated(by));
//        try {
//            sbc1.getWait().until(elementToBeClickable(by)).click();
//            } catch (ElementClickInterceptedException ignored){}
//        List<WebElement> webLinks = sbc1.getWait().until(numberOfElementsToBeMoreThan(By.xpath(".//a[@class='sc-iyvyFf cTzSso']"), 0));
        return new Fan(model, article, Double.valueOf(power), phase, Double.valueOf(price), "webLinks.get(0).getAttribute(href)", "webLinks.get(1).getAttribute(href)");
    }

    private void sorting() {
        changeValueComboBoxByLabel("Сортировать по:", "Цена (По возрастающей)");
    }

    private void hidingDiagram() {
        // Скрыть диаграммы
        onCheckboxDiagram(getWebElementByXpath(".//div[contains(@class, 'sc-cMljjf')]"));
    }

    private void grouping() {
        changeValueComboBoxByLabel("Группировать по:", "Нет");
    }

    private void fillFlowAndDrop(String airFlow, String airDrop) {
        try {
            inputTextByLabel("Расход воздуха", airFlow);
            inputTextByLabel("Внешнее давление", airDrop);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        clickElementIfExistsByXpath("(.//button[@class='sc-bxivhb SWiNZ'])[2]");
        if (isWarning())
            flagWarning = true;
    }

    @SneakyThrows
    private boolean isWarning() {
        return isExistElementMoreThen(By.xpath(".//span[@type='warning']"), 0);
    }

    private boolean isExistElementMoreThen(By by, int moreThen) {
        boolean isExists;
        try {
            sbc.getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(1));
            isExists = sbc.getDriver().findElements(by).size() > moreThen;
        } finally {
            sbc.getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(MAX_LIMIT_TIMEOUT));
        }
        return isExists;
    }

    private WebElement getWebElementByXpath(String xpath) {
        List<Optional<WebElement>> webElements = sbc.getWait().until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath(xpath), 0)).stream().map(Optional::of).collect(Collectors.toList());
        return webElements.get(0).orElseThrow(IllegalArgumentException::new);
    }


    private void changeValueComboBoxByVerticalLabel(String findTextLabel, String newValue) {
        String checkXpath = ".//div[text() = '" + findTextLabel + "']/following::div[1]/div[1]/span[1]";
        String xpath = checkXpath + "/following::i[1]";
        By by = By.xpath(xpath);
        WebElement checkingWb = getWebElementByXpath(checkXpath);
        if (checkingWb == null) return;
        if (checkingWb.getText().equals(newValue)) return;
        sbc.getWait().until(visibilityOfElementLocated(by));
        sbc.getWait().until(elementToBeClickable(by)).click();
        List<WebElement> list = getListDivsByNameClass("sc-bZQynM");
        WebElement changingElement = list.stream().filter(webElement -> webElement.getText().trim().equals(newValue)).findAny().orElse(null);
        if (changingElement == null)
            showAlert("Запрос " + xpath + " не дал результата! Значение " + newValue + " не было найдено в списке!", Alert.AlertType.WARNING);
        sbc.getWait().until(elementToBeClickable(changingElement)).click();
    }

    private void changeValueComboBoxByLabel(String findTextLabel, String newValue) {
        String xpath = ".//span[text() = '" + findTextLabel + "']/following::div[1]//span[1]";
        By by = By.xpath(xpath + "/ancestor::div[1]");
        WebElement checkingWb = getWebElementByXpath(xpath);
        if (checkingWb == null) return;
        if (checkingWb.getText().equals(newValue)) return;
        sbc.getWait().until(visibilityOfElementLocated(by));
        sbc.getWait().until(elementToBeClickable(by)).click();
        List<WebElement> list = getListDivsByNameClass("sc-gzVnrw");
        WebElement changingElement = list.stream().filter(webElement -> webElement.getText().trim().equals(newValue)).findAny().orElse(null);
        if (changingElement == null)
            showAlert("Запрос " + xpath + " не дал результата! Значение " + newValue + " не было найдено в списке!", Alert.AlertType.WARNING);
        sbc.getWait().until(elementToBeClickable(changingElement)).click();
    }

    private List<WebElement> getListDivsByNameClass(String xpathLists) {
        return sbc.getDriver().findElements(By.xpath(".//div[contains(@class, '" + xpathLists + "')]"));
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
        WebElement listSubTypeParent = sbc.getDriver().findElement(By.xpath(".//div[@class ='sc-cIShpX htEzPC']"));
        List<WebElement> listSubType = listSubTypeParent.findElements(By.tagName("div"));
        List<WebElement> listSilentEC = sbc.getDriver().findElements(By.xpath(".//div[contains(@class, 'sc-tilXH')]"));
        switch (subType) {
            case NONE:
            case ON_ROOF:
                clickElementWithScroll(listSubType.get(1));
                onCheckbox(false, listSilentEC.get(0));
                onCheckbox(false, listSilentEC.get(1));
                break;
            case KITCHEN:
                clickElementWithScroll(listSubType.get(2));
                sbc.getWait().until(elementToBeClickable(listSubType.get(2))).click();
                onCheckbox(false, listSilentEC.get(0));
                onCheckbox(false, listSilentEC.get(1));
                break;
            case KITCHEN_AND_EC:
                clickElementWithScroll(listSubType.get(2));
                onCheckbox(false, listSilentEC.get(0));
                onCheckbox(true, listSilentEC.get(1));
                break;
            case EC:
                clickElementWithScroll(listSubType.get(1));
                onCheckbox(false, listSilentEC.get(0));
                onCheckbox(true, listSilentEC.get(1));
                break;
            case SILENT:
                clickElementWithScroll(listSubType.get(1));
                onCheckbox(true, listSilentEC.get(0));
                onCheckbox(false, listSilentEC.get(1));
                break;
            case SILENT_AND_EC:
                clickElementWithScroll(listSubType.get(1));
                onCheckbox(true, listSilentEC.get(0));
                onCheckbox(true, listSilentEC.get(1));
                break;
            case SMOKE_EXTRACT:
                clickElementWithScroll(listSubType.get(5));
                onCheckbox(false, listSilentEC.get(0));
                onCheckbox(false, listSilentEC.get(1));
                break;
        }

    }

    private void onCheckbox(boolean onAction, WebElement webElement) {
        /*
        "kClLXW" - выкл.
        "eITjnS" - вкл.
         */
        if (onAction) {
            if (isContainsInClass(webElement, "kClLXW")) webElement.click();
        } else {
            if (isContainsInClass(webElement, "eITjnS")) webElement.click();
        }
    }

    private void onCheckboxDiagram(WebElement webElement) {
        /*
        "ineogT" - выкл.
        "hBEpsK" - вкл.
         */
        if (isContainsInClass(webElement, "hBEpsK")) webElement.click();
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
        List<WebElement> listTypeMontage = sbc.getDriver().findElements(By.xpath(".//div[contains(@class, 'sc-feJyhm')]"));
        switch (typeMontage) {
            case ROUND:
                selectTypeFan(0, listTypeMontage);
                break;
            case RECTANGLE:
                selectTypeFan(1, listTypeMontage);
                break;
            case ROOF:
                selectTypeFan(2, listTypeMontage);
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
        for (int i = 0; i < list.size(); i++) {
            if (i == i1 || i == i2) {
                if (list.get(i).getAttribute("class").contains("cxjQFd"))
                    sbc.getWait().until(elementToBeClickable(list.get(i))).click();
            } else {
                if (list.get(i).getAttribute("class").contains("gHdNtY"))
                    sbc.getWait().until(elementToBeClickable(list.get(i))).click();
            }
        }
    }

    private void selectTypeFan(int index, List<WebElement> list) {
        /*
        "gHdNtY" - вкл.
        "cxjQFd" - выкл.
         */
        for (int i = 0; i < list.size(); i++) {
            if (i == index) {
                if (list.get(i).getAttribute("class").contains("cxjQFd"))
                    sbc.getWait().until(elementToBeClickable(list.get(i))).click();
            } else {
                if (list.get(i).getAttribute("class").contains("gHdNtY"))
                    sbc.getWait().until(elementToBeClickable(list.get(i))).click();
            }
        }
    }

    public static void showAlert(String alertTxt, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(type.toString());
        alert.setHeaderText("Description:");
        alert.setContentText(alertTxt);
        alert.showAndWait();
        if (sbc.getDriver() != null)
            sbc.getDriver().close();
    }

    public void initializeBrowser() {
        clickElementIfExistsByXpath(".//*[@id='CybotCookiebotDialogBodyLevelButtonLevelOptinAllowAll']");
        // Нажатие на вкладку  Подбор
        clickElementIfExistsByXpath(".//button[@data-id='2']");
        // Открытие вкладки Дополнительные параметры поиска
        clickElementIfExistsByXpath(".//div[text() = 'Дополнительные параметры поиска']/i[1]", "class", "fa fa-chevron-down");
    }
}
