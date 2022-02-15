package com.systemair.bcastfans.staticClasses.browsers;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Wait;

public interface Browser<T> {
    Wait<T> getWait();
    WebDriver getDriver();
}
