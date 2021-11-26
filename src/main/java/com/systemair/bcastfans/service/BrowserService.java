package com.systemair.bcastfans.service;

import com.systemair.bcastfans.domain.Fan;
import com.systemair.bcastfans.domain.SubType;
import com.systemair.bcastfans.domain.TypeMontage;
import lombok.Getter;
import lombok.Setter;
import org.openqa.selenium.WebDriver;
@Getter
@Setter
public class BrowserService {
    private WebDriver browser;
    private double positiveLimit;
    private double negativeLimit;
    public Fan calculate(String airFlow, String airDrop, TypeMontage typeMontage, SubType subType) {
        Fan fan = new Fan();
        return fan;
    }

}
