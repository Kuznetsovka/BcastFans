package com.systemair.bcastfans.controller;

import com.systemair.bcastfans.domain.FanUnit;
import com.systemair.bcastfans.service.BrowserService;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;

import static javafx.application.Platform.runLater;

public class BrowserController {
    private final BrowserService browserService = new BrowserService();

    public ObservableList<FanUnit> calculate(TextField fieldNegativeLimit, TextField fieldPositiveLimit, ObservableList<FanUnit> data, ProgressBar pb, Label labelProgressBar, boolean isStop) {
        String negativeLimit = fieldNegativeLimit.getText();
        String positiveLimit = fieldPositiveLimit.getText();
        browserService.setNegativeLimit(negativeLimit);
        browserService.setPositiveLimit(positiveLimit);
        browserService.prepareStartPageBeforeCalculation();
        if (!data.isEmpty())
            data.stream().
                    filter(u -> u.getCheck().isSelected()).
                    forEach(
                            u -> {
                                {
                                    if (isStop) {
                                        return;
                                    }
                                    u.setFan(browserService.calculate(
                                            u.getAirFlow(),
                                            u.getAirDrop(),
                                            u.getTypeMontage(),
                                            u.getSubType()));
                                    new Thread(() -> runLater(() -> progressBar(data.indexOf(u), data.size(), pb, labelProgressBar, u))).start();
                                }
                                System.out.printf("Установка %d посчитана", data.indexOf(u));
                                ;
                            });
        return data;
    }

    private synchronized void progressBar(int index, int size, ProgressBar pb, Label labelProgressBar, FanUnit u) {
        pb.setVisible(true);
        pb.setProgress((double) (index + 1) / size);
        labelProgressBar.setVisible(true);
        labelProgressBar.setText(String.format("Посчитано %d установок из %d", index + 1, size));
    }

    public void initializeBrowser() {
        browserService.initializeBrowser();
    }
}
