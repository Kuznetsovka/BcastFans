package com.systemair.bcastfans;

import javafx.scene.control.Alert;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import java.time.Duration;
import java.util.logging.Logger;

import static com.systemair.bcastfans.UtilClass.PATH_DRIVER;
import static com.systemair.bcastfans.service.BrowserService.showAlert;

public class SingletonBrowserClass {

    private static SingletonBrowserClass instanceOfSingletonBrowserClass = null;
    private static final Logger LOGGER = Logger.getLogger(SingletonBrowserClass.class.getName());
    private static final String HOME_URL = "https://www.systemair.com/ru/";
    private static WebDriver driver;
    private static Wait<WebDriver> wait;
    public static final int MAX_LIMIT_TIMEOUT = 30;
    public static final int LIMIT_REPEAT_TIMEOUT = 2;

    private SingletonBrowserClass() {

        try {
            UtilClass.initProperties();
            System.setProperty("webdriver.chrome.driver", PATH_DRIVER);
            ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.setHeadless(true);//выбор фонового режима true
            chromeOptions.addArguments("start-maximized");
//            chromeOptions.addArguments("--enable-extensions");
//            //chromeOptions.addArguments("--enable-plugins");
//            chromeOptions.addArguments("--window-size=1920,1080");
//            chromeOptions.addArguments("--enable-precise-memory-info");
//            chromeOptions.addArguments("--disable-popup-blocking");
//            chromeOptions.addArguments("--disable-default-apps");
            driver = new ChromeDriver(chromeOptions);
            LOGGER.info("Запуст драйвера!");
            // Ожидание 30 секунд, опрос каждые 5 секунд
            wait = new FluentWait<>(driver)
                    .withTimeout(Duration.ofSeconds(MAX_LIMIT_TIMEOUT))
                    .pollingEvery(Duration.ofSeconds(LIMIT_REPEAT_TIMEOUT))
                    .ignoring(NoSuchElementException.class, ElementClickInterceptedException.class);
            LOGGER.info("Загрузка страницы");
            driver.navigate().to(HOME_URL);
            LOGGER.info("Страница загружена");
            Actions builder = new Actions(driver);
            for (int i = 0; i < 4; i++) {
                builder.sendKeys(Keys.chord(Keys.CONTROL, "-")).perform();
            }

        } catch (
                SessionNotCreatedException e) {
            showAlert("Обновите драйвер браузера!" + "\n" + e.getRawMessage(), Alert.AlertType.WARNING);
        } catch (IllegalArgumentException e) {
            showAlert("Драйвер не найден по указанному пути!" + "\n" + e.getMessage(), Alert.AlertType.WARNING);
        }
    }

    public synchronized static SingletonBrowserClass getInstanceOfSingletonBrowserClass() {
        if (instanceOfSingletonBrowserClass == null) {
            instanceOfSingletonBrowserClass = new SingletonBrowserClass();
        }
        return instanceOfSingletonBrowserClass;
    }

    public synchronized WebDriver getDriver() {
        return driver;
    }

    public synchronized Wait<WebDriver> getWait() {
        return wait;
    }

}