package com.systemair.bcastfans.service;

import com.systemair.bcastfans.SingletonBrowserClass;
import com.systemair.bcastfans.domain.Fan;
import com.systemair.bcastfans.domain.SubType;
import com.systemair.bcastfans.domain.TypeMontage;
import javafx.scene.control.Alert;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import static com.systemair.bcastfans.PrepareBrowserClass.*;
import static com.systemair.bcastfans.SingletonBrowserClass.MAX_LIMIT_TIMEOUT;
import static com.systemair.bcastfans.UtilClass.showAlert;
import static com.systemair.bcastfans.domain.TypeMontage.ROUND;
import static java.lang.Thread.sleep;
import static org.openqa.selenium.support.ui.ExpectedConditions.*;

public class BrowserService {
    private static final SingletonBrowserClass sbc = SingletonBrowserClass.getInstanceOfSingletonBrowserClass();
    boolean flagWarning;
    boolean isSorting;
    boolean isHidingDiagram;
    private boolean isGrouping;
    private static final Logger LOGGER = Logger.getLogger(BrowserService.class.getName());
    private boolean isChangeMeasureValueTable;
    private TypeMontage lastTypeMontage;

    public void prepareLimits(String negativeLimit, String positiveLimit) {
        try {
            // Внесение данных Отрицательный допуск
            inputTextByLabel(sbc.getWait(), "Отрицательный допуск", negativeLimit);
            LOGGER.info("Заполнен отрицательный допуск");
            // Внесение данных Положительный допуск
            inputTextByLabel(sbc.getWait(), "Положительный допуск", positiveLimit);
            LOGGER.info("Заполнен положительный допуск");
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }
    }

    private void clickElementWithScroll(WebElement webElement) {
        ((JavascriptExecutor) sbc.getDriver()).executeScript("arguments[0].scrollIntoView(true);", webElement);
        sbc.getWait().until(elementToBeClickable(webElement)).click();
    }

    public Fan calculate(String airFlow, String airDrop, TypeMontage typeMontage, SubType subType) {
        if (typeMontage == ROUND && subType == SubType.SMOKE_EXTRACT)
            showAlert(LOGGER,"Не допустимая конфигурация, Круглых + Дымоудаление не существует!", Alert.AlertType.WARNING);
        if (typeMontage == ROUND && subType == SubType.KITCHEN)
            showAlert(LOGGER,"Не допустимая конфигурация, Круглых + Кухоненных не существует!", Alert.AlertType.WARNING);
        selectTypeMontage(typeMontage);
        selectSubType(subType);
        fillFlowAndDrop(airFlow, airDrop);
        if (flagWarning) {
            flagWarning = false;
            return new Fan();
        }
        if (!isGrouping) grouping();
        if (!isHidingDiagram) hidingDiagram();
        if (!isSorting) sorting();
        if (!isChangeMeasureValueTable) changeMeasureValueTable();
        Fan fan = fillTableUnit(subType);
        return (fan != null) ? fan : new Fan();
    }

    private void changeMeasureValueTable() {
        changeMeasureValueOnTableByIndex("Вт", 4); //Мощность
//        changeMeasureValueOnTableByIndex("А", 5); //Ток
//        changeMeasureValueOnTableByIndex("об/мин", 6); //Частота вращения
        isChangeMeasureValueTable = true;
    }

    private Fan fillTableUnit(SubType subType) {
        By moreFansButtonBy = By.xpath(".//button[@class='sc-bxivhb SWiNZ']");
        WebElement btnMoreUnit;
        Fan result = null;
        List<WebElement> row;
        int countRow = 1;
        int lastRows;
        while (isExistElementMoreThen(moreFansButtonBy, 2) && subType.equals(SubType.ON_ROOF)) {
            btnMoreUnit = sbc.getWait().until(visibilityOfAllElementsLocatedBy(moreFansButtonBy)).get(2);
            sbc.getWait().until(elementToBeClickable(btnMoreUnit)).click();
            LOGGER.info("Нажата кнопка больше вентиляторов.");
        }
        lastRows = sbc.getWait().until(visibilityOfAllElementsLocatedBy(By.xpath(".//table[@class='sc-Rmtcm djcDFD']/tbody/tr[@class='sc-bRBYWo hmjjYh']"))).size();
        do {
            if (countRow > lastRows)
                return new Fan();
            row = sbc.getWait().until(visibilityOfAllElementsLocatedBy(By.xpath(".//table[@class='sc-Rmtcm djcDFD']/tbody/tr[" + countRow + "]/td[contains(@class,'sc-jhAzac')]")));
            String price = row.get(4).getText();
            String model = row.get(2).findElement(By.tagName("a")).getText();
            if (isContinueFan(price, subType, model)) {
                countRow++;
                continue;
            }
            LOGGER.info("Выбран вентилятор с индексом " + countRow);
            result = getResultFan(row);
        } while (result == null);
        return result;
    }

    private boolean isContinueFan(String price, SubType subType, String model) {
        return ((price.equals("")) ||
                (subType == SubType.ON_ROOF && ((model.contains("RVK") || model.contains("prio")) && !model.contains("MUB"))) ||
                (model.contains("150")));
    }

    private Fan getResultFan(List<WebElement> row) {
        WebElement modelCell = sbc.getWait().until(visibilityOf(row.get(2).findElement(By.tagName("a"))));
        String phase = "";
        if (!row.get(2).findElement(By.tagName("small")).getText().equals("")) {
            WebElement phaseCell = sbc.getWait().until(visibilityOf(row.get(2).findElement(By.tagName("small"))));
            phase = phaseCell.getText();
        }
        String model = modelCell.getText();
        String article = row.get(3).getText();
        String price = row.get(4).getText();
        String power = row.get(7).getText();
        WebElement wb = row.get(1).findElement(By.tagName("button"));
//        String shortLink = getLink(modelCell,true);
//        String fullLink = getLink(modelCell,false);
        sbc.getWait().until(elementToBeClickable(wb)).click();
        List<WebElement> webLinks = sbc.getWait().until(numberOfElementsToBeMoreThan(By.xpath(".//a[@class='sc-iyvyFf cTzSso']"), 0));
        List<String> links = webLinks.stream().map(l -> l.getAttribute("href")).collect(Collectors.toList());
        clickWithoutTimeOut(By.xpath(".//div[@class = 'sc-dfVpRl cERHhv']"));
        return new Fan(model, article, Double.valueOf(power), phase, Double.valueOf(price), links.get(0), links.get(1));
    }

//    private String getLink(WebElement modelCell,boolean compact) {
//        String type = "";
//        if (compact) type = "-compact";
//        String firstPartLink = "https://shop.systemair.com/ru-RU/api/product/pdf" + type + "/externalId/";
//        String link = modelCell.getAttribute("href");
//        return firstPartLink + link.substring(link.indexOf("?p=") + 3).replace("&", "?");
//    }

    private void sorting() {
        changeValueComboBoxByLabel("Сортировать по:", "Цена (По возрастающей)");
        isSorting = true;
        LOGGER.info("Сортировка вентиляторов");
    }

    private void hidingDiagram() {
        // Скрыть диаграммы
        onCheckboxDiagram(getWebElementByXpath(sbc.getWait(), ".//div[contains(@class, 'sc-cMljjf')]"));
        isHidingDiagram = true;
        LOGGER.info("Скрытие диаграмм вентиляторов");
    }

    private void grouping() {
        isGrouping = true;
        changeValueComboBoxByLabel("Группировать по:", "Нет");
        LOGGER.info("Группировка вентиляторов");
    }

    private void fillFlowAndDrop(String airFlow, String airDrop) {
        try {
            inputTextByLabel(sbc.getWait(), "Расход воздуха", airFlow);
            inputTextByLabel(sbc.getWait(), "Внешнее давление", airDrop);
            sleep(500);
            clickElementIfExistsByXpath(sbc.getWait(),"(.//button[@class='sc-bxivhb SWiNZ'])[2]");
            sleep(500);
            //sbc.getWait().until(or(visibilityOfElementLocated(By.xpath(".//span[@type='warning']")),numberOfElementsToBeMoreThan(By.xpath(".//table[@class='sc-Rmtcm djcDFD']/tbody/tr[@class='sc-bRBYWo hmjjYh']"),0)));
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }
        if (isWarning())
            flagWarning = true;
    }

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

    private void clickWithoutTimeOut(By by) {
        try {
            sbc.getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(1));
            sbc.getDriver().findElement(by).click();
        } finally {
            sbc.getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(MAX_LIMIT_TIMEOUT));
        }
    }


    private void changeMeasureValueOnTableByIndex(String newValue, int index) {
        String xpath = ".//th[@class='sc-hzDkRC kmzkGx'][" + index + "]/div[2]/div[1]";
        String checkingXpath = xpath + "/span[1]";
        WebElement checkingWb = sbc.getWait().until(visibilityOfElementLocated(By.xpath(checkingXpath)));
        if (checkingWb.getText().equals(newValue)) return;
        sbc.getWait().until(elementToBeClickable(By.xpath(xpath))).click();
        List<WebElement> list = sbc.getWait().until(numberOfElementsToBeMoreThan(By.xpath(".//div[@class='sc-EHOje gdmUuL']/following::div[2]/div"), 0));
        WebElement changingElement = list.stream().filter(webElement -> webElement.getText().trim().equals(newValue)).findAny().orElse(null);
        if (changingElement == null)
            showAlert(LOGGER,"Запрос " + xpath + " не дал результата! Значение " + newValue + " не было найдено в списке!", Alert.AlertType.WARNING);
        sbc.getWait().until(elementToBeClickable(changingElement)).click();
        LOGGER.info("Заменили значение изменения на " + newValue);
    }

    private static void changeValueComboBoxByLabel(String findTextLabel, String newValue) {
        String xpath = ".//span[text() = '" + findTextLabel + "']/following::div[1]//span[1]";
        By by = By.xpath(xpath + "/ancestor::div[1]");
        WebElement checkingWb = getWebElementByXpath(sbc.getWait(),xpath);
        if (checkingWb == null) return;
        if (checkingWb.getText().equals(newValue)) return;
        sbc.getWait().until(visibilityOfElementLocated(by));
        sbc.getWait().until(elementToBeClickable(by)).click();
        List<WebElement> list = sbc.getWait().until(visibilityOfAllElementsLocatedBy(By.xpath(".//div[contains(@class, 'sc-gzVnrw')]")));
        WebElement changingElement = list.stream().filter(webElement -> webElement.getText().trim().equals(newValue)).findAny().orElse(null);
        if (changingElement == null)
            showAlert(LOGGER,"Запрос " + xpath + " не дал результата! Значение " + newValue + " не было найдено в списке!", Alert.AlertType.WARNING);
        sbc.getWait().until(elementToBeClickable(changingElement)).click();
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
        LOGGER.info("Выбран подтип вентилятора...");
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
        if (typeMontage == lastTypeMontage) return;
        List<WebElement> listTypeMontage = sbc.getDriver().findElements(By.xpath(".//div[contains(@class, 'sc-feJyhm')]"));
        lastTypeMontage = typeMontage;
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
        LOGGER.info("Выбран тип монтажа...");

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
}
