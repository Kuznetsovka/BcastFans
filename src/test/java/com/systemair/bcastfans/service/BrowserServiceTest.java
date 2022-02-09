package com.systemair.bcastfans.service;

import com.systemair.bcastfans.domain.Fan;
import com.systemair.bcastfans.domain.SubType;
import com.systemair.bcastfans.domain.TypeMontage;
import com.systemair.bcastfans.service.browser.BrowserService;
import com.systemair.bcastfans.service.browser.SystemairBrowserService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static com.systemair.bcastfans.staticClasses.UtilClass.PATH_DRIVER;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

class BrowserServiceTest {
    static{
        PATH_DRIVER ="C:\\ProgramData\\DriverChrome\\chromedriver\\chromedriver.exe";
    }
    private static final BrowserService browserService = new SystemairBrowserService();
    private int negativeLimit = 0;
    private int positiveLimit = 100;
    @BeforeAll
    public static void init(){
        browserService.setNegativeLimit("0");
        browserService.setPositiveLimit("100");
        browserService.initializeBrowser();
        browserService.prepareStartPageBeforeCalculation();
    }


    @ParameterizedTest
    @MethodSource("com.systemair.bcastfans.MethodSources#argumentOfTests")
    void test_MethodSource_MultipleParams(Integer airflow, Integer airDrop,TypeMontage typeMontage, SubType subType, String dimension, Integer resultArticle) {
        Fan fan = browserService.calculate(String.valueOf(airflow), String.valueOf(airDrop), typeMontage, subType, dimension);
        String airFlowXPath = ".//span[text() = 'Расход воздуха']/following::input[1]";
        String airDropXPath = ".//span[text() = 'Внешнее давление']/following::input[1]";
        String negativeLimitXPath = ".//span[text() = 'Отрицательный допуск']/following::input[1]";
        String positiveLimitXPath = ".//span[text() = 'Положительный допуск']/following::input[1]";
        WebElement wbFlow = browserService.getSbc().getWait().until(visibilityOfElementLocated(By.xpath(airFlowXPath)));
        WebElement wbDrop = browserService.getSbc().getWait().until(visibilityOfElementLocated(By.xpath(airDropXPath)));
        WebElement wbNegativeLimit = browserService.getSbc().getWait().until(visibilityOfElementLocated(By.xpath(negativeLimitXPath)));
        WebElement wbPositiveLimit = browserService.getSbc().getWait().until(visibilityOfElementLocated(By.xpath(positiveLimitXPath)));
        Assertions.assertEquals(String.valueOf(airflow), wbFlow.getAttribute("value"));
        Assertions.assertEquals(airDrop + ",00", wbDrop.getAttribute("value"));
        Assertions.assertEquals(String.valueOf(negativeLimit), wbNegativeLimit.getAttribute("value"));
        Assertions.assertEquals(String.valueOf(positiveLimit), wbPositiveLimit.getAttribute("value"));
        Assertions.assertEquals(resultArticle, Integer.valueOf(fan.getArticle()));
    }

    //@RepeatedTest(50)
    public void calculationOneFan(){
        for (int i = 0; i < 2; i++) {
            Fan fan = browserService.calculate(String.valueOf(100 + i), String.valueOf(101 + i), TypeMontage.ROUND, SubType.NONE, "");
            String airFlowXPath = ".//span[text() = 'Расход воздуха']/following::input[1]";
            String airDropXPath = ".//span[text() = 'Внешнее давление']/following::input[1]";
            WebElement wbFlow = browserService.getSbc().getWait().until(visibilityOfElementLocated(By.xpath(airFlowXPath)));
            WebElement wbDrop = browserService.getSbc().getWait().until(visibilityOfElementLocated(By.xpath(airDropXPath)));
            Assertions.assertEquals(String.valueOf(100 + i), wbFlow.getAttribute("value"));
            Assertions.assertEquals((101 + i) +",00", wbDrop.getAttribute("value"));
            Assertions.assertEquals(5756, Integer.valueOf(fan.getArticle()));
        }
    }

    //@RepeatedTest(50)
    public void calculationTwoFan(){
        for (int i = 0; i < 2; i++) {
            Fan fan = browserService.calculate(String.valueOf(2000 + i), String.valueOf(500 + i), TypeMontage.RECTANGLE, SubType.NONE, "");
            String airFlowXPath = ".//span[text() = 'Расход воздуха']/following::input[1]";
            String airDropXPath = ".//span[text() = 'Внешнее давление']/following::input[1]";
            WebElement wbFlow = browserService.getSbc().getWait().until(visibilityOfElementLocated(By.xpath(airFlowXPath)));
            WebElement wbDrop = browserService.getSbc().getWait().until(visibilityOfElementLocated(By.xpath(airDropXPath)));
            String button = "(.//button[@class='sc-bxivhb SWiNZ'])[2]";
            Assertions.assertEquals(String.valueOf(2000 + i), wbFlow.getAttribute("value"));
            Assertions.assertEquals((500 + i) +",00", wbDrop.getAttribute("value"));
            Assertions.assertEquals(93098, Integer.valueOf(fan.getArticle()));
        }
    }


    @AfterAll
    public static void close(){
        browserService.getSbc().getDriver().quit();
    }


}