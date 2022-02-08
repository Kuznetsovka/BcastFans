package com.systemair.bcastfans;

import com.systemair.bcastfans.domain.Fan;
import com.systemair.bcastfans.domain.SubType;
import com.systemair.bcastfans.domain.TypeMontage;
import com.systemair.bcastfans.service.BrowserService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static com.systemair.bcastfans.service.BrowserService.sbc;
import static com.systemair.bcastfans.staticClasses.UtilClass.PATH_DRIVER;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

class BrowserServiceTest {
    static {
        PATH_DRIVER = "C:\\ProgramData\\DriverChrome\\chromedriver\\chromedriver.exe";
    }

    private static final BrowserService browserService = new BrowserService();

    @BeforeAll
    public static void init() {
        browserService.setNegativeLimit("0");
        browserService.setPositiveLimit("100");
        browserService.initializeBrowser();
        browserService.prepareStartPageBeforeCalculation();
    }

    @RepeatedTest(50)
    public void calculationOneFan() {
        for (int i = 0; i < 2; i++) {
            Fan fan = browserService.calculate(String.valueOf(100 + i), String.valueOf(101 + i), TypeMontage.ROUND, SubType.NONE, "");
            String airFlowXPath = ".//span[text() = 'Расход воздуха']/following::input[1]";
            String airDropXPath = ".//span[text() = 'Внешнее давление']/following::input[1]";
            WebElement wbFlow = sbc.getWait().until(visibilityOfElementLocated(By.xpath(airFlowXPath)));
            WebElement wbDrop = sbc.getWait().until(visibilityOfElementLocated(By.xpath(airDropXPath)));
            Assertions.assertEquals(String.valueOf(100 + i), wbFlow.getAttribute("value"));
            Assertions.assertEquals((101 + i) + ",00", wbDrop.getAttribute("value"));
            Assertions.assertEquals(5756, Integer.valueOf(fan.getArticle()));
        }
    }

    @RepeatedTest(50)
    public void calculationTwoFan() {
        for (int i = 0; i < 2; i++) {
            Fan fan = browserService.calculate(String.valueOf(2000 + i), String.valueOf(500 + i), TypeMontage.RECTANGLE, SubType.NONE, "");
            String airFlowXPath = ".//span[text() = 'Расход воздуха']/following::input[1]";
            String airDropXPath = ".//span[text() = 'Внешнее давление']/following::input[1]";
            WebElement wbFlow = sbc.getWait().until(visibilityOfElementLocated(By.xpath(airFlowXPath)));
            WebElement wbDrop = sbc.getWait().until(visibilityOfElementLocated(By.xpath(airDropXPath)));
            Assertions.assertEquals(String.valueOf(2000 + i), wbFlow.getAttribute("value"));
            Assertions.assertEquals((500 + i) + ",00", wbDrop.getAttribute("value"));
            Assertions.assertEquals(93098, Integer.valueOf(fan.getArticle()));
        }
    }

    @AfterAll
    public static void close() {
        sbc.getDriver().quit();
    }


}