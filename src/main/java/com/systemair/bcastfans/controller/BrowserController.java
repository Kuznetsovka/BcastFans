package com.systemair.bcastfans.controller;

import com.systemair.bcastfans.domain.FanUnit;
import com.systemair.bcastfans.service.BrowserService;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;

public class BrowserController {
    private final BrowserService browserService = new BrowserService();

    public ObservableList<FanUnit> calculate(TextField fieldNegativeLimit, TextField fieldPositiveLimit, ObservableList<FanUnit> data, ProgressBar pb, Label labelProgressBar, boolean isStop) {
        String negativeLimit = fieldNegativeLimit.getText();
        String positiveLimit = fieldPositiveLimit.getText();
        browserService.setNegativeLimit(negativeLimit);
        browserService.setPositiveLimit(positiveLimit);
        browserService.prepareStartPageBeforeCalculation();
        if (!data.isEmpty())
            data.forEach(u -> {
                if (isStop) {
                    return;
                }
                u.setFan(browserService.calculate(
                        u.getAirFlow(),
                        u.getAirDrop(),
                        u.getTypeMontage(),
                        u.getSubType()));
                progressBar(data, pb, labelProgressBar, u);
            });
        return data;
    }

    private void progressBar(ObservableList<FanUnit> data, ProgressBar pb, Label labelProgressBar, FanUnit u) {
        pb.setProgress((double) (data.indexOf(u) + 1) / data.size());
        labelProgressBar.setVisible(true);
        labelProgressBar.setText(String.format("Посчитано %d установок из %d", data.indexOf(u) + 1, data.size()));
    }

    private int getDoubleValueFromField(TextField value) {
        return !value.getText().isEmpty() ? Integer.parseInt(value.getText()) : 0;
    }

    public void initializeBrowser() {
        browserService.initializeBrowser();
    }
}
