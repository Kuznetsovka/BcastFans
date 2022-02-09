package com.systemair.bcastfans.staticClasses;

import com.systemair.bcastfans.service.browser.JSWaiter;
import javafx.scene.control.Alert;
import org.apache.log4j.Logger;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import java.time.Duration;

import static com.systemair.bcastfans.staticClasses.UtilClass.PATH_DRIVER;
import static com.systemair.bcastfans.staticClasses.UtilClass.showAlert;

public class SingletonBrowserClass {

    private static SingletonBrowserClass instanceOfSingletonBrowserClass = null;
    private static final Logger LOGGER = Logger.getLogger(SingletonBrowserClass.class.getName());
    private static final String HOME_URL = "https://www.systemair.com/ru/";
    private static WebDriver driver;
    private static Wait<WebDriver> wait;
    public static final int MAX_LIMIT_TIMEOUT = 20;
    public static final int LIMIT_REPEAT_TIMEOUT = 500;

    private SingletonBrowserClass() {
        try {
            System.setProperty("webdriver.chrome.driver", PATH_DRIVER);
            ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.addArguments("start-maximized");
            chromeOptions.setHeadless(true); //выбор фонового режима true
            driver = new ChromeDriver(chromeOptions);
            JSWaiter.setDriver(driver);
            LOGGER.info("Запуск драйвера!");
            // Ожидание 20 секунд, опрос каждые 2 секунды
            wait = new FluentWait<>(driver)
                    .withTimeout(Duration.ofSeconds(MAX_LIMIT_TIMEOUT))
                    .pollingEvery(Duration.ofNanos(LIMIT_REPEAT_TIMEOUT))
                    .ignoring(NoSuchElementException.class, ElementClickInterceptedException.class);
            LOGGER.info("Загрузка страницы!");
            driver.navigate().to(HOME_URL);
            LOGGER.info("Страница загружена!");
        } catch (SessionNotCreatedException e) {
            showAlert(LOGGER, "Обновите драйвер браузера!" + "\n" + e.getRawMessage(), Alert.AlertType.WARNING);
        } catch (IllegalArgumentException e) {
            showAlert(LOGGER, "Драйвер не найден по указанному пути!" + "\n" + e.getMessage() + "\r" + "Требуемый путь: " + PATH_DRIVER, Alert.AlertType.WARNING);
        }
    }

    public synchronized static SingletonBrowserClass getInstanceOfSingletonBrowserClass() {
        if (instanceOfSingletonBrowserClass == null || driver == null) {
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