package com.systemair.bcastfans.staticClasses.browsers;

import com.systemair.bcastfans.MyCatchException;
import javafx.scene.control.Alert;
import lombok.SneakyThrows;
import org.apache.log4j.Logger;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import java.time.Duration;

import static com.systemair.bcastfans.staticClasses.UtilClass.*;

public class ChromeBrowser {

    private ChromeDriver driver;
    private Wait<ChromeDriver> wait;
    private static final Logger LOGGER = Logger.getLogger(ChromeBrowser.class.getName());
    @SneakyThrows
    public ChromeBrowser() {
        try{
        System.setProperty("webdriver.chrome.driver", CHROME_DRIVER);
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setBrowserVersion("93");
        chromeOptions.addArguments("start-maximized");
        chromeOptions.addArguments("enable-automation");
        chromeOptions.addArguments("--headless");
        chromeOptions.addArguments("--no-sandbox");
        chromeOptions.addArguments("--disable-infobars");
        chromeOptions.addArguments("--disable-dev-shm-usage");
        chromeOptions.addArguments("--disable-browser-side-navigation");
        chromeOptions.addArguments("--disable-gpu");
        driver = new ChromeDriver(chromeOptions);
        // Ожидание 40 секунд, опрос каждые 0.5 секунды
        wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(MAX_LIMIT_TIMEOUT))
                .pollingEvery(Duration.ofNanos(LIMIT_REPEAT_TIMEOUT))
                .ignoring(NoSuchElementException.class, ElementClickInterceptedException.class);
        } catch (IllegalArgumentException e) {
            throw new MyCatchException("Драйвер не найден по указанному пути!" + "\n" + e.getMessage() + "\r" + "Требуемый путь: " + CHROME_DRIVER, Alert.AlertType.WARNING);
        }
    }


    public Wait<ChromeDriver> getWait() {
        return wait;
    }
    public ChromeDriver getDriver() {
        return driver;
    }
}
