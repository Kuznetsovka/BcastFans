package com.systemair.bcastfans.browsers;

import com.systemair.bcastfans.MyCatchException;
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

public class EdgeBrowser {
    private EdgeDriver driver;
    private Wait<EdgeDriver> wait;
    private static final Logger LOGGER = Logger.getLogger(EdgeBrowser.class.getName());

    public EdgeBrowser() {
        try {
            System.setProperty("webdriver.edge.driver", EDGE_DRIVER);
            EdgeOptions edgeOptions = new EdgeOptions();
            edgeOptions.addArguments("start-maximized");
            edgeOptions.addArguments("enable-automation");
            if (!HEAD)
                edgeOptions.addArguments("--headless");
            driver = new EdgeDriver(edgeOptions);
            // Ожидание 40 секунд, опрос каждые 0.5 секунды
            wait = new FluentWait<>(driver)
                    .withTimeout(Duration.ofSeconds(MAX_LIMIT_TIMEOUT))
                    .pollingEvery(Duration.ofNanos(LIMIT_REPEAT_TIMEOUT))
                    .ignoring(NoSuchElementException.class, ElementClickInterceptedException.class);
        } catch (IllegalStateException e) {
            try {
                throw new MyCatchException("Драйвер не найден по указанному пути!" + "\n" + e.getMessage() + "\r" + "Требуемый путь: " + EDGE_DRIVER, Alert.AlertType.WARNING);
            } catch (MyCatchException ex) {
                ex.printStackTrace();
            }
        }
    }

    public Wait<EdgeDriver> getWait() {
        return wait;
    }

    public EdgeDriver getDriver() {
        return driver;
    }
}
