package com.systemair.bcastfans.service.browser;

import com.systemair.bcastfans.staticClasses.SingletonBrowserClass;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.systemair.bcastfans.staticClasses.SingletonBrowserClass.MAX_LIMIT_TIMEOUT;
import static org.awaitility.Awaitility.with;
import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

public abstract class BrowserServiceImpl implements BrowserService {

    public static final SingletonBrowserClass sbc = SingletonBrowserClass.getInstanceOfSingletonBrowserClass();

    protected boolean isContainsInClass(WebElement webElement, String text) {
        return webElement.getAttribute("class").contains(text);
    }

    protected boolean isWarning() {
        return isExistElementMoreThen(By.xpath(".//span[@type='warning']"), 0);
    }

    protected boolean isExistElementMoreThen(By by, int moreThen) {
        boolean isExists;
        try {
            sbc.getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(1));
            isExists = sbc.getDriver().findElements(by).size() > moreThen;
        } finally {
            sbc.getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(MAX_LIMIT_TIMEOUT));
        }
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
        waitEnableElement(sbc.getDriver().findElement(by));
        sbc.getWait().until(elementToBeClickable(by)).click();
    }

    public static void waitEnableElement(WebElement wb) {
        with().pollDelay(100, TimeUnit.MILLISECONDS).await().atMost
                (10, TimeUnit.SECONDS).until(wb::isEnabled);
    }

    protected void clickElementWithScroll(WebElement webElement) {
        ((JavascriptExecutor) sbc.getDriver()).executeScript("arguments[0].scrollIntoView(true);", webElement);
        sbc.getWait().until(elementToBeClickable(webElement)).click();
    }
}
