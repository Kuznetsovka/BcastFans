package com.systemair.bcastfans.service;

import com.systemair.bcastfans.domain.Fan;
import com.systemair.bcastfans.domain.SubType;
import com.systemair.bcastfans.domain.TypeMontage;
import com.systemair.bcastfans.service.browser.BrowserService;
import com.systemair.bcastfans.service.browser.SystemairBrowserService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static com.systemair.bcastfans.staticClasses.UtilClass.PATH_DRIVER;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

class BrowserServiceTest {
    static{
        PATH_DRIVER ="C:\\ProgramData\\DriverChrome\\chromedriver\\chromedriver.exe";
    }
    private static final BrowserService browserService = new SystemairBrowserService();

    @BeforeAll
    public static void init(){
        browserService.setNegativeLimit("0");
        browserService.setPositiveLimit("100");
        browserService.initializeBrowser();
        browserService.prepareStartPageBeforeCalculation();
    }

    @RepeatedTest(5)
    public void calculationTwoFan(){
        Fan fan = browserService.calculate("2000","500", TypeMontage.RECTANGLE, SubType.NONE,"");
        String airFlowXPath = ".//span[text() = 'Расход воздуха']/following::input[1]";
        String airDropXPath = ".//span[text() = 'Внешнее давление']/following::input[1]";
        WebElement wbFlow = browserService.getSbc().getWait().until(visibilityOfElementLocated(By.xpath(airFlowXPath)));
        WebElement wbDrop = browserService.getSbc().getWait().until(visibilityOfElementLocated(By.xpath(airDropXPath)));
        String button = "(.//button[@class='sc-bxivhb SWiNZ'])[2]";
        Assertions.assertEquals("2000",wbFlow.getAttribute("value"));
        Assertions.assertEquals("500,00",wbDrop.getAttribute("value"));
        Assertions.assertEquals(93098,Integer.valueOf(fan.getArticle()));
    }

    @RepeatedTest(5)
    public void calculationOneFan(){
        Fan fan = browserService.calculate("100","101", TypeMontage.ROUND, SubType.NONE,"");
        String airFlowXPath = ".//span[text() = 'Расход воздуха']/following::input[1]";
        String airDropXPath = ".//span[text() = 'Внешнее давление']/following::input[1]";
        WebElement wbFlow = browserService.getSbc().getWait().until(visibilityOfElementLocated(By.xpath(airFlowXPath)));
        WebElement wbDrop = browserService.getSbc().getWait().until(visibilityOfElementLocated(By.xpath(airDropXPath)));
        Assertions.assertEquals("100",wbFlow.getAttribute("value"));
        Assertions.assertEquals("101,00",wbDrop.getAttribute("value"));
        Assertions.assertEquals(5756,Integer.valueOf(fan.getArticle()));
    }


    @AfterAll
    public static void close(){
        browserService.getSbc().getDriver().quit();
    }


}