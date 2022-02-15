package com.systemair.bcastfans.staticClasses.browsers;

import com.systemair.bcastfans.service.browser.JSWaiter;
import javafx.scene.control.Alert;
import org.apache.log4j.Logger;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import java.time.Duration;

import static com.systemair.bcastfans.staticClasses.UtilClass.*;

public class EdgeBrowser implements Browser<EdgeDriver> {
    EdgeDriver driver;
    Wait<EdgeDriver> wait;
    private static final Logger LOGGER = Logger.getLogger(EdgeBrowser.class.getName());
    public EdgeBrowser() {
        try {
            System.setProperty("webdriver.edge.driver", EDGE_DRIVER);
            EdgeOptions chromeOptions = new EdgeOptions();
            chromeOptions.addArguments("start-maximized");
            chromeOptions.addArguments("enable-automation");
            chromeOptions.addArguments("--headless");
            driver = new EdgeDriver(chromeOptions);
            JSWaiter.setDriver(driver);
            // Ожидание 40 секунд, опрос каждые 0.5 секунды
            wait = new FluentWait<>(driver)
                    .withTimeout(Duration.ofSeconds(MAX_LIMIT_TIMEOUT))
                    .pollingEvery(Duration.ofNanos(LIMIT_REPEAT_TIMEOUT))
                    .ignoring(NoSuchElementException.class, ElementClickInterceptedException.class);
        } catch (IllegalArgumentException e) {
            showAlert(LOGGER, "Драйвер не найден по указанному пути!" + "\n" + e.getMessage() + "\r" + "Требуемый путь: " + EDGE_DRIVER, Alert.AlertType.WARNING);
        }
    }

    @Override
    public Wait<EdgeDriver> getWait() {
        return wait;
    }

    public EdgeDriver getDriver() {
        return driver;
    }
}
