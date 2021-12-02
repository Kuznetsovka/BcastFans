package com.systemair.bcastfans.controller;

import com.systemair.bcastfans.domain.FanUnit;
import com.systemair.bcastfans.service.BrowserService;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;

public class BrowserController {
    private final BrowserService browserService = new BrowserService();

    public ObservableList<FanUnit> calculate(TextField fieldNegativeLimit, TextField fieldPositiveLimit, ObservableList<FanUnit> data) {
        String negativeLimit = fieldNegativeLimit.getText();
        String positiveLimit = fieldPositiveLimit.getText();
        browserService.setNegativeLimit(negativeLimit);
        browserService.setPositiveLimit(positiveLimit);
        browserService.prepareStartPageBeforeCalculation();
        if (!data.isEmpty())
            data.forEach(u -> u.setFan(
                    browserService.calculate(
                            u.getAirFlow(),
                            u.getAirDrop(),
                            u.getTypeMontage(),
                            u.getSubType()
                    )
            ));
        //data.get(0).setFan(new Fan("name","model",20.0,"3",100.0));
        return data;
    }

    private int getDoubleValueFromField(TextField value) {
        return !value.getText().isEmpty() ? Integer.parseInt(value.getText()) : 0;
    }

    public void initializeBrowser() {
        browserService.initializeBrowser();
    }
}
