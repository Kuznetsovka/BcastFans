package com.systemair.bcastfans.controller;

import com.systemair.bcastfans.domain.FanUnit;
import com.systemair.bcastfans.service.BrowserService;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;

public class BrowserController {
    private final BrowserService browserService = new BrowserService();

    public ObservableList<FanUnit> calculate(TextField fieldNegativeLimit, TextField fieldPositiveLimit, ObservableList<FanUnit> data) {
        int negativeLimit = getDoubleValueFromField(fieldNegativeLimit);
        int positiveLimit = getDoubleValueFromField(fieldPositiveLimit);
        browserService.initializeBrowser();
        browserService.setNegativeLimit(negativeLimit);
        browserService.setPositiveLimit(positiveLimit);
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
}
