package com.systemair.bcastfans.service.browser;

import com.systemair.bcastfans.staticClasses.SingletonBrowserClass;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.systemair.bcastfans.staticClasses.UtilClass.MAX_LIMIT_TIMEOUT;
import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

public abstract class BrowserServiceImpl implements BrowserService {
    private static final Logger LOGGER = Logger.getLogger(BrowserServiceImpl.class.getName());
    public static final SingletonBrowserClass sbc = SingletonBrowserClass.getInstanceOfSingletonBrowserClass();

    protected boolean isContainsInClass(WebElement webElement, String text) {
        return webElement.getAttribute("class").contains(text);
    }

    protected boolean isWarning() {
        boolean isWarning;
        By byWarning = By.xpath(".//span[@type='warning']");
        try {
            sbc.getDriver().manage().timeouts().implicitlyWait(Duration.ofMillis(1500));
            isWarning = sbc.getDriver().findElements(byWarning).size() > 0;
        } finally {
            sbc.getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(MAX_LIMIT_TIMEOUT));
        }
        LOGGER.info("Warning is " + isWarning);
        return isWarning;
    }

    protected boolean isExistElementMoreThenTwo(By by) {
        boolean isExists;
        isExists = sbc.getDriver().findElements(by).size() > 2;
        return isExists;
    }

    protected void clickWithoutTimeOut(By by) {
        try {
            sbc.getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(1));
            sbc.getDriver().findElement(by).click();
        } finally {
            sbc.getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(MAX_LIMIT_TIMEOUT));
        }
    }

    protected WebElement getWebElementByXpath(String xpath) {
        List<Optional<WebElement>> webElements = sbc.getWait().until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath(xpath), 0)).stream().map(Optional::of).collect(Collectors.toList());
        return webElements.get(0).orElseThrow(IllegalArgumentException::new);
    }

    protected void clickElementIfExistsByXpath(String xpath, String... attributeAndValue) throws ElementClickInterceptedException {
        By by = By.xpath(xpath);
        sbc.getWait().until(visibilityOfElementLocated(by));
        if (attributeAndValue.length > 0) {
            String attribute = attributeAndValue[0];
            String value = attributeAndValue[1];
            if (getWebElementByXpath(xpath).getAttribute(attribute).equals(value)) return;
        }
        LOGGER.info("Кнопка доступна!");
        sbc.getWait().until(elementToBeClickable(by)).click();
        LOGGER.info("Кнопка нажата!");
    }

    protected void clickElementWithScroll(WebElement webElement) {
        ((JavascriptExecutor) sbc.getDriver()).executeScript("arguments[0].scrollIntoView(true);", webElement);
        sbc.getWait().until(elementToBeClickable(webElement)).click();
    }

    public abstract void changeIP();
}
