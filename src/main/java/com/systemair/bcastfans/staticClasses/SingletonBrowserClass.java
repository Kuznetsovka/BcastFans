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
import static java.lang.System.exit;

public class SingletonBrowserClass {

    private static SingletonBrowserClass instanceOfSingletonBrowserClass = null;
    private static final Logger LOGGER = Logger.getLogger(SingletonBrowserClass.class.getName());
    private static final String HOME_URL = "https://www.systemair.com/ru/";
    private static WebDriver driver;
    private static Wait<WebDriver> wait;
    public static final int MAX_LIMIT_TIMEOUT = 40;
    public static final int LIMIT_REPEAT_TIMEOUT = 500;

    private SingletonBrowserClass() {
        try {
            System.setProperty("webdriver.chrome.driver", PATH_DRIVER);
            ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.addArguments("start-maximized");
            chromeOptions.addArguments("enable-automation"); // https://stackoverflow.com/a/43840128/1689770
            chromeOptions.addArguments("--headless"); // only if you are ACTUALLY running headless
            chromeOptions.addArguments("--no-sandbox"); //https://stackoverflow.com/a/50725918/1689770
            chromeOptions.addArguments("--disable-infobars"); //https://stackoverflow.com/a/43840128/1689770
            chromeOptions.addArguments("--disable-dev-shm-usage"); //https://stackoverflow.com/a/50725918/1689770
            chromeOptions.addArguments("--disable-browser-side-navigation"); //https://stackoverflow.com/a/49123152/1689770
            chromeOptions.addArguments("--disable-gpu"); //https://stackoverflow.com/questions/51959986/how-to-solve-selenium-chromedriver-timed-out-receiving-message-from-renderer-exc
            //chromeOptions.addArguments("enable-features=NetworkServiceInProcess");
            //chromeOptions.addArguments("disable-features=NetworkService");
            driver = new ChromeDriver(chromeOptions);
            JSWaiter.setDriver(driver);
            LOGGER.info("Запуск драйвера!");
            // Ожидание 40 секунд, опрос каждые 0.5 секунды
            wait = new FluentWait<>(driver)
                    .withTimeout(Duration.ofSeconds(MAX_LIMIT_TIMEOUT))
                    .pollingEvery(Duration.ofNanos(LIMIT_REPEAT_TIMEOUT))
                    .ignoring(NoSuchElementException.class, ElementClickInterceptedException.class);
            LOGGER.info("Загрузка страницы!");
            driver.navigate().to(HOME_URL);
            LOGGER.info("Страница загружена!");
        } catch (SessionNotCreatedException e) {
            showAlert(LOGGER, "Обновите драйвер браузера!" + "\n" + e.getRawMessage(), Alert.AlertType.ERROR);
            if (driver != null) driver.quit();
            exit(0);
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