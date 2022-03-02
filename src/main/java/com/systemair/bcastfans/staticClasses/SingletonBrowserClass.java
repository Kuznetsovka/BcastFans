package com.systemair.bcastfans.staticClasses;

import com.systemair.bcastfans.MyCatchException;
import com.systemair.bcastfans.staticClasses.browsers.ChromeBrowser;
import com.systemair.bcastfans.staticClasses.browsers.EdgeBrowser;
import javafx.scene.control.Alert;
import lombok.SneakyThrows;
import org.apache.log4j.Logger;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Wait;

import static com.systemair.bcastfans.staticClasses.UtilClass.BROWSER;
import static com.systemair.bcastfans.staticClasses.UtilClass.showAlert;
import static java.lang.System.exit;

public class SingletonBrowserClass {

    private static SingletonBrowserClass instanceOfSingletonBrowserClass = null;
    private static final Logger LOGGER = Logger.getLogger(SingletonBrowserClass.class.getName());
    private static final String HOME_URL = "https://www.systemair.com/ru/";
    private static Wait wait;
    private static WebDriver driver;
    private String originalWindow = "";

    private SingletonBrowserClass() {
        try {
            if (BROWSER.isEmpty() || BROWSER.equals("CHROME")) {
                ChromeBrowser browser = new ChromeBrowser();
                wait = browser.getWait();
                driver = browser.getDriver();
            } else if (BROWSER.equals("EDGE")) {
                EdgeBrowser browser = new EdgeBrowser();
                wait = browser.getWait();
                driver = browser.getDriver();
            }
            LOGGER.info("Загрузка страницы!");
            driver.navigate().to(HOME_URL);
            originalWindow = driver.getWindowHandle();
            LOGGER.info("Страница загружена!");
        } catch (SessionNotCreatedException e) {
            try {
                throw new MyCatchException("Обновите драйвер браузера!" + "\n" + e.getRawMessage(), Alert.AlertType.ERROR);
            } catch (MyCatchException ex) {
                ex.printStackTrace();
            }
            if (driver != null) driver.quit();
            exit(0);
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

    public Wait<WebDriver> getWait() {
        return wait;
    }

    public boolean isOriginTab() {
        return driver.getWindowHandle().equals(originalWindow);
    }

    public void switchToOrigin() {
        driver.switchTo().window(originalWindow);
    }
}