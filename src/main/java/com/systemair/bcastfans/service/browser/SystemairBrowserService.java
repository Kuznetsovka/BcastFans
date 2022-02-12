package com.systemair.bcastfans.service.browser;

import com.systemair.bcastfans.domain.Fan;
import com.systemair.bcastfans.domain.SubType;
import com.systemair.bcastfans.domain.TypeMontage;
import com.systemair.bcastfans.staticClasses.SingletonBrowserClass;
import javafx.scene.control.Alert;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

import static com.systemair.bcastfans.domain.TypeMontage.*;
import static com.systemair.bcastfans.staticClasses.UtilClass.showAlert;
import static org.openqa.selenium.support.ui.ExpectedConditions.*;

public class SystemairBrowserService extends BrowserServiceImpl {
    protected String positiveLimit = "100";
    protected String negativeLimit = "0";
    private boolean isClear;
    boolean flagWarning;
    boolean isSorting;
    boolean isHidingDiagram;
    private boolean isGrouping;
    private static final Logger LOGGER = Logger.getLogger(SystemairBrowserService.class.getName());
    private boolean isChangeMeasureValueTable;
    private TypeMontage lastTypeMontage;
    private SubType lastSubMontage;

    public SystemairBrowserService() {
        super();
    }

    @Override
    public void prepareStartPageBeforeCalculation() {
        try {
            // Внесение данных Отрицательный допуск
            inputTextByLabel("Отрицательный допуск", negativeLimit);
            LOGGER.info("Заполнен отрицательный допуск");
            // Внесение данных Положительный допуск
            inputTextByLabel("Положительный допуск", positiveLimit);
            LOGGER.info("Заполнен положительный допуск");
            // Проверка и изменение значения Макс. температура воздуха на 40
            inputTextByLabel("Макс. температура воздуха", "40");
            LOGGER.info("Заполнена макс. температура воздуха");
            // Проверка и изменение единиц измерения Расход воздуха на м³/ч
            changeValueComboBoxByLabel("Расход воздуха", "м³/ч");
            LOGGER.info("Изменены единицы измерения расхода воздуха");
            // Проверка и изменение единиц измерения Внешнее давление на Па
            changeValueComboBoxByLabel("Внешнее давление", "Па");
            LOGGER.info("Изменены единицы измерения внешнего давления");
            // Проверка и изменение значения Частота на 50 Гц
            changeValueComboBoxByLabel("Частота", "50 Гц");
            LOGGER.info("Изменено значение частоты");
            // Проверка и изменение значения Регулятор скорости на По умолчанию
            changeValueComboBoxByLabel("Регулятор скорости", "По умолчанию");
            LOGGER.info("Изменено значение регулятора скорости");
            // Проверка и изменение единиц измерения Макс. температура воздуха на °С
            changeValueComboBoxByLabel("Макс. температура воздуха", "°C");
            LOGGER.info("Изменено единицы измерения макс. температуры воздуха");
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }

    }

    @Override
    public void inputTextByLabel(String findTextLabel, String newValue) throws InterruptedException {
        // "fgkAsr" - без ошибки "lnjRPV" - c ошибкой
        String checkXPath = ".//span[text() = '" + findTextLabel + "']";
        String xpath = checkXPath + "/following::input[1]";
        By by = By.xpath(xpath);
        WebElement wb = sbc.getWait().until(visibilityOfElementLocated(by));
        if (wb.getAttribute("value").equals(newValue)) return;
        LOGGER.info("Заполнено текстовое поле, значение: " + newValue);
        do {
            wb.sendKeys(Keys.CONTROL + "a");
            wb.sendKeys(Keys.DELETE);
        } while (!wb.getAttribute("value").equals(""));
        //sbc.getWait().until(attributeToBe(wb, "value", "")); //Быстрая версия
        sbc.getWait().until(attributeToBe(By.xpath(checkXPath), "class", "sc-jwKygS lnjRPV"));
        do {
            wb.sendKeys(newValue);
        } while (!wb.getAttribute("value").equals(newValue));
        //sleep(300); //Быстрая версия
        sbc.getWait().until(attributeToBe(By.xpath(checkXPath), "class", "sc-jwKygS fgkAsr"));
    }

    @SafeVarargs
    @Override
    public final Fan calculate(String airFlow, String airDrop, TypeMontage typeMontage, SubType subType, String dimension, List<String>... selectedFans) {
        checkAvailableConfiguration(typeMontage, subType, dimension);
        selectSubType(subType);
        selectTypeMontage(typeMontage);
        fillFlowAndDrop(airFlow, airDrop);
        if (flagWarning) {
            flagWarning = false;
            return new Fan();
        }
        prepareListBeforeCalculation();
        Fan fan = findFan(subType, dimension, selectedFans);
        return (fan != null) ? fan : new Fan();
    }

    private void checkAvailableConfiguration(TypeMontage typeMontage, SubType subType, String dimension) {
        if (typeMontage == ROUND && subType == SubType.SMOKE_EXTRACT)
            showAlert(LOGGER, "Не допустимая конфигурация, Круглых + Дымоудаление не существует!", Alert.AlertType.WARNING);
        if (typeMontage == ROUND && subType == SubType.KITCHEN)
            showAlert(LOGGER, "Не допустимая конфигурация, Круглых + Кухоненных не существует!", Alert.AlertType.WARNING);
        if (!(typeMontage == ROUND || typeMontage == RECTANGLE || typeMontage == ROUND_AND_RECTANGLE) && !dimension.isEmpty())
            showAlert(LOGGER, "Не допустимая конфигурация, выбранный тип вентилятора не будет найден согласно заданному размеру!", Alert.AlertType.WARNING);
    }

    private void prepareListBeforeCalculation() {
        if (!isGrouping) grouping();
        if (!isHidingDiagram) hidingDiagram();
        if (!isSorting) sorting();
        if (!isChangeMeasureValueTable) changeMeasureValueOnTableByIndex("Вт", 4);
    }

    @Override
    public void clearTypeMontage() {
        List<WebElement> listTypeMontage = sbc.getDriver().findElements(By.xpath(".//div[contains(@class, 'sc-feJyhm')]"));
        selectTypeFan(-1, listTypeMontage);
        LOGGER.info("Выключаем все вентиляторы...");
        isClear = true;
    }

    @SafeVarargs
    @Override
    public final Fan findFan(SubType subType, String dimension, List<String>... selectedList) {
        By moreFansButtonBy = By.xpath(".//button[@class='sc-bxivhb SWiNZ']");
        boolean isFirst = false;
        Fan firstFan = null;
        Fan result = null;
        List<WebElement> row;
        int countRow = 1;
        int lastRows;
        lastRows = getLastRows();
        if (selectedList.length == 0)
            while (isExistElementMoreThenTwo(moreFansButtonBy) && (subType.equals(SubType.ON_ROOF) || !dimension.isEmpty())) {
                clickButtonMoreFans(moreFansButtonBy);
                lastRows = getLastRows();
            }
        else
            while (isExistElementMoreThenTwo(moreFansButtonBy)) {
                clickButtonMoreFans(moreFansButtonBy);
                lastRows = getLastRows();
            }
        do {
            if (countRow > lastRows && !dimension.isEmpty())
                return firstFan;
            row = sbc.getWait().until(visibilityOfAllElementsLocatedBy(By.xpath(".//table[@class='sc-Rmtcm djcDFD']/tbody/tr[" + countRow + "]/td[contains(@class,'sc-jhAzac')]")));
            String price = row.get(4).getText();
            String model = row.get(2).findElement(By.tagName("a")).getText();
            String phase = row.get(2).findElement(By.tagName("small")).getText();
            if (selectedList.length != 0)
                if (checkAvailableFanModel(model, selectedList[0])) {
                    countRow++;
                    continue;
                }
            if (isContinueFan(price, subType, model, phase)) {
                countRow++;
                continue;
            }
            if (!isFirst) {
                firstFan = getResultFan(row);
                isFirst = true;
            }
            if (!model.contains(dimension)) {
                countRow++;
                continue;
            }
            LOGGER.info("Выбран вентилятор с индексом " + countRow);
            if (countRow == 1 && firstFan != null)
                result = firstFan;
            else
                result = getResultFan(row);
        } while (result == null);
        return result;
    }

    private int getLastRows() {
        return sbc.getWait().until(visibilityOfAllElementsLocatedBy(By.xpath(".//table[@class='sc-Rmtcm djcDFD']/tbody/tr[@class='sc-bRBYWo hmjjYh']"))).size();
    }

    private void clickButtonMoreFans(By moreFansButtonBy) {
        WebElement btnMoreUnit;
        btnMoreUnit = sbc.getWait().until(visibilityOfAllElementsLocatedBy(moreFansButtonBy)).get(2);
        sbc.getWait().until(elementToBeClickable(btnMoreUnit)).click();
        LOGGER.info("Нажата кнопка больше вентиляторов.");
    }

    @Override
    public boolean checkAvailableFanModel(String model, List<String> selectedList) {
        String prefix;
        prefix = getPrefixByModel(model);
        for (String s : selectedList) {
            if (model.startsWith(s + prefix))
                return false;
        }
        return true;
    }

    public String getPrefixByModel(String model) {
        return model.contains("MUB") || model.contains("DVG") ? "" : " ";
    }

    @Override
    public boolean isContinueFan(String price, SubType subType, String model, String phase) {
        return ((price.equals("")) ||
                phase.contains("поворот на 90") || // Подбирать MUB только версии Прямой поток
                (subType == SubType.ON_ROOF && !model.startsWith("K ") && !model.contains("MUB"))) ||
                (model.contains("150"));
    }

    public Fan getResultFan(List<WebElement> row) {
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
        sbc.getWait().until(elementToBeClickable(wb)).click();
        List<WebElement> webLinks = sbc.getWait().until(numberOfElementsToBeMoreThan(By.xpath(".//a[@class='sc-iyvyFf cTzSso']"), 0));
        List<String> links = webLinks.stream().map(l -> l.getAttribute("href")).collect(Collectors.toList());
        clickWithoutTimeOut(By.xpath(".//div[@class = 'sc-dfVpRl cERHhv']"));
        return new Fan(model, article, Double.valueOf(power), phase, Double.valueOf(price), links.get(0), links.get(1));
    }

    @Override
    public void sorting() {
        changeValueComboBoxByLabel("Сортировать по:", "Цена (По возрастающей)");
        isSorting = true;
        LOGGER.info("Сортировка вентиляторов");
    }

    @Override
    public void hidingDiagram() {
        // Скрыть диаграммы
        onCheckboxDiagram(getWebElementByXpath(".//div[contains(@class, 'sc-cMljjf')]"));
        isHidingDiagram = true;
        LOGGER.info("Скрытие диаграмм вентиляторов");
    }

    @Override
    public void grouping() {
        isGrouping = true;
        changeValueComboBoxByLabel("Группировать по:", "Нет");
        LOGGER.info("Группировка вентиляторов");
    }

    @Override
    public void fillFlowAndDrop(String airFlow, String airDrop) {
        try {
            inputTextByLabel("Расход воздуха", airFlow);
            inputTextByLabel("Внешнее давление", airDrop);
            clickElementIfExistsByXpath("(.//button[@class='sc-bxivhb SWiNZ'])[2]");
        } catch (InterruptedException | ElementClickInterceptedException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }
        if (isWarning())
            flagWarning = true;
    }

    @Override
    public void changeMeasureValueOnTableByIndex(String newValue, int index) {
        String xpath = ".//th[@class='sc-hzDkRC kmzkGx'][" + index + "]/div[2]/div[1]";
        String checkingXpath = xpath + "/span[1]";
        WebElement checkingWb = sbc.getWait().until(visibilityOfElementLocated(By.xpath(checkingXpath)));
        if (checkingWb.getText().equals(newValue)) return;
        sbc.getWait().until(elementToBeClickable(By.xpath(xpath))).click();
        List<WebElement> list = sbc.getWait().until(numberOfElementsToBeMoreThan(By.xpath(".//div[@class='sc-EHOje gdmUuL']/following::div[2]/div"), 0));
        WebElement changingElement = list.stream().filter(webElement -> webElement.getText().trim().equals(newValue)).findAny().orElse(null);
        if (changingElement == null)
            showAlert(LOGGER, "Запрос " + xpath + " не дал результата! Значение " + newValue + " не было найдено в списке!", javafx.scene.control.Alert.AlertType.WARNING);
        sbc.getWait().until(elementToBeClickable(changingElement)).click();
        LOGGER.info("Заменили значение изменения на " + newValue);
        isChangeMeasureValueTable = true;
    }

    @Override
    public void changeValueComboBoxByLabel(String findTextLabel, String newValue) {
        String xpath = ".//span[text() = '" + findTextLabel + "']/following::div[1]//span[1]";
        By by = By.xpath(xpath + "/ancestor::div[1]");
        WebElement checkingWb = getWebElementByXpath(xpath);
        if (checkingWb == null) return;
        if (checkingWb.getText().equals(newValue)) return;
        sbc.getWait().until(visibilityOfElementLocated(by));
        sbc.getWait().until(elementToBeClickable(by)).click();
        List<WebElement> list = sbc.getWait().until(visibilityOfAllElementsLocatedBy(By.xpath(".//div[contains(@class, 'sc-gzVnrw')]")));
        WebElement changingElement = list.stream().filter(webElement -> webElement.getText().trim().equals(newValue)).findAny().orElse(null);
        if (changingElement == null)
            showAlert(LOGGER, "Запрос " + xpath + " не дал результата! Значение " + newValue + " не было найдено в списке!", Alert.AlertType.WARNING);
        sbc.getWait().until(elementToBeClickable(changingElement)).click();
    }

    @Override
    public void selectSubType(SubType subType) {
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
        if (subType == lastSubMontage) return;
        WebElement listSubTypeParent = sbc.getDriver().findElement(By.xpath(".//div[@class ='sc-cIShpX htEzPC']"));
        List<WebElement> listSubType = listSubTypeParent.findElements(By.tagName("div"));
        List<WebElement> listSilentEC = sbc.getDriver().findElements(By.xpath(".//div[contains(@class, 'sc-tilXH')]"));
        lastSubMontage = subType;
        clearTypeMontage();
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

    @Override
    public void onCheckbox(boolean onAction, WebElement webElement) {
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

    @Override
    public void onCheckboxDiagram(WebElement webElement) {
        /*
        "ineogT" - выкл.
        "hBEpsK" - вкл.
         */
        if (isContainsInClass(webElement, "hBEpsK")) webElement.click();
    }


    @Override
    public void selectTypeMontage(TypeMontage typeMontage) {
        /*
            Круглые - 0
            Прямоугольные - 1
            Крышные - 2
            Осевые - 3
            Центробежные - 4
        */
        if (typeMontage == lastTypeMontage && !isClear) return;
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
        isClear = false;
        LOGGER.info("Выбран тип монтажа...");
    }

    @Override
    public void selectTwoTypeFan(int i1, int i2, List<WebElement> list) {
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

    @Override
    public void selectTypeFan(int index, List<WebElement> list) {
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

    @Override
    public void initializeBrowser() {
        clickElementIfExistsByXpath(".//*[@id='CybotCookiebotDialogBodyLevelButtonLevelOptinAllowAll']");
        // Нажатие на вкладку  Подбор
        clickElementIfExistsByXpath(".//button[@data-id='2']");
        // Открытие вкладки Дополнительные параметры поиска
        clickElementIfExistsByXpath(".//div[text() = 'Дополнительные параметры поиска']/i[1]", "class", "fa fa-chevron-down");
    }

    @Override
    public void setNegativeLimit(String negativeLimit) {
        this.negativeLimit = negativeLimit;
    }

    @Override
    public void setPositiveLimit(String positiveLimit) {
        this.positiveLimit = positiveLimit;
    }

    @Override
    public SingletonBrowserClass getSbc() {
        return sbc;
    }
}
