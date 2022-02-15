package com.systemair.bcastfans.staticClasses;

import com.systemair.bcastfans.staticClasses.browsers.Browser;
import com.systemair.bcastfans.staticClasses.browsers.ChromeBrowser;
import com.systemair.bcastfans.staticClasses.browsers.EdgeBrowser;
import com.systemair.bcastfans.staticClasses.browsers.TypeBrowser;
import javafx.scene.control.Alert;
import org.apache.log4j.Logger;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.Wait;

import static com.systemair.bcastfans.staticClasses.UtilClass.showAlert;
import static com.systemair.bcastfans.staticClasses.browsers.TypeBrowser.CHROME;
import static com.systemair.bcastfans.staticClasses.browsers.TypeBrowser.EDGE;
import static java.lang.System.exit;

public class SingletonBrowserClass {

    private static SingletonBrowserClass instanceOfSingletonBrowserClass = null;
    private static final Logger LOGGER = Logger.getLogger(SingletonBrowserClass.class.getName());
    private static final String HOME_URL = "https://www.systemair.com/ru/";
    private static Wait wait;
    private static WebDriver driver;
    private TypeBrowser typeBrowser = EDGE;

    private SingletonBrowserClass() {
        try {
            if (typeBrowser == EDGE) {
                Browser<EdgeDriver> browser = new EdgeBrowser();
                wait = browser.getWait();
                driver = ((EdgeBrowser) browser).getDriver();
            } else if (typeBrowser == CHROME) {
                Browser<ChromeDriver> browser = new ChromeBrowser();
                wait = browser.getWait();
                driver = ((ChromeBrowser) browser).getDriver();
            }
            LOGGER.info("Загрузка страницы!");
            driver.navigate().to(HOME_URL);
            LOGGER.info("Страница загружена!");
        } catch (SessionNotCreatedException e) {
            showAlert(LOGGER, "Обновите драйвер браузера!" + "\n" + e.getRawMessage(), Alert.AlertType.ERROR);
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
}