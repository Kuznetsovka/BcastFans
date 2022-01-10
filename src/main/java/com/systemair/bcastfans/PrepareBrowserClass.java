package com.systemair.bcastfans;

import javafx.scene.control.Alert;
import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.systemair.bcastfans.UtilClass.showAlert;
import static java.lang.Thread.sleep;
import static org.openqa.selenium.support.ui.ExpectedConditions.*;

public class PrepareBrowserClass {
    private static final Logger LOGGER = Logger.getLogger(PrepareBrowserClass.class.getName());
    public static void prepareStartPageBeforeCalculation(Wait<WebDriver> wait) {
        try {
            // Проверка и изменение значения Макс. температура воздуха на 40
            inputTextByLabel(wait,"Макс. температура воздуха", "40");
            LOGGER.info("Заполнена макс. температура воздуха");
            // Проверка и изменение единиц измерения Расход воздуха на м³/ч
            changeValueComboBoxByLabel(wait,"Расход воздуха", "м³/ч");
            LOGGER.info("Изменены единицы измерения расхода воздуха");
            // Проверка и изменение единиц измерения Внешнее давление на Па
            changeValueComboBoxByLabel(wait,"Внешнее давление", "Па");
            LOGGER.info("Изменены единицы измерения внешнего давления");
            // Проверка и изменение значения Частота на 50 Гц
            changeValueComboBoxByLabel(wait,"Частота", "50 Гц");
            LOGGER.info("Изменено значение частоты");
            // Проверка и изменение значения Регулятор скорости на По умолчанию
            changeValueComboBoxByLabel(wait,"Регулятор скорости", "По умолчанию");
            LOGGER.info("Изменено значение регулятора скорости");
            // Проверка и изменение единиц измерения Макс. температура воздуха на °С
            changeValueComboBoxByLabel(wait,"Макс. температура воздуха", "°C");
            LOGGER.info("Изменено единицы измерения макс. температуры воздуха");
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }
    }

    public static WebElement getWebElementByXpath(Wait<WebDriver> wait, String xpath) {
        List<Optional<WebElement>> webElements = wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath(xpath), 0)).stream().map(Optional::of).collect(Collectors.toList());
        return webElements.get(0).orElseThrow(IllegalArgumentException::new);
    }

    public static void inputTextByLabel(Wait<WebDriver> wait,String findTextLabel, String newValue) throws InterruptedException {
        String xpath = ".//span[text() = '" + findTextLabel + "']/following::input[1]";
        By by = By.xpath(xpath);
        WebElement wb = wait.until(visibilityOfElementLocated(by));
        if (wb.getText().equals(newValue)) return;
        LOGGER.info("Заполнено текстовое поле, значение: " + newValue);
        wb.sendKeys(Keys.CONTROL + "a");
        sleep(300);
        wb.sendKeys(Keys.DELETE);
        sleep(300);
        if (wait.until(textToBePresentInElement(wb, "")))
            wb.sendKeys(newValue);
    }

    private static void changeValueComboBoxByLabel(Wait<WebDriver> wait, String findTextLabel, String newValue) {
        String xpath = ".//span[text() = '" + findTextLabel + "']/following::div[1]//span[1]";
        By by = By.xpath(xpath + "/ancestor::div[1]");
        WebElement checkingWb = getWebElementByXpath(wait,xpath);
        if (checkingWb == null) return;
        if (checkingWb.getText().equals(newValue)) return;
        wait.until(visibilityOfElementLocated(by));
        wait.until(elementToBeClickable(by)).click();
        List<WebElement> list = wait.until(visibilityOfAllElementsLocatedBy(By.xpath(".//div[contains(@class, 'sc-gzVnrw')]")));
        WebElement changingElement = list.stream().filter(webElement -> webElement.getText().trim().equals(newValue)).findAny().orElse(null);
        if (changingElement == null)
            showAlert(LOGGER,"Запрос " + xpath + " не дал результата! Значение " + newValue + " не было найдено в списке!", Alert.AlertType.WARNING);
        wait.until(elementToBeClickable(changingElement)).click();
    }

    public static void initializeBrowser(Wait<WebDriver> wait) {
        clickElementIfExistsByXpath(wait,".//*[@id='CybotCookiebotDialogBodyLevelButtonLevelOptinAllowAll']");
        // Нажатие на вкладку  Подбор
        clickElementIfExistsByXpath(wait,".//button[@data-id='2']");
        // Открытие вкладки Дополнительные параметры поиска
        clickElementIfExistsByXpath(wait,".//div[text() = 'Дополнительные параметры поиска']/i[1]", "class", "fa fa-chevron-down");
    }

    public static void clickElementIfExistsByXpath(Wait<WebDriver> wait,String xpath, String... attributeAndValue) {
        try {
            By by = By.xpath(xpath);
            wait.until(visibilityOfElementLocated(by));
            if (attributeAndValue.length > 0) {
                String attribute = attributeAndValue[0];
                String value = attributeAndValue[1];
                if (getWebElementByXpath(wait, xpath).getAttribute(attribute).equals(value)) return;
            }
            wait.until(elementToBeClickable(by)).click();
        } catch (ElementClickInterceptedException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }
    }

}
