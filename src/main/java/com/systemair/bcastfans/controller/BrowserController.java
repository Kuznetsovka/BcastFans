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
    private static boolean isStop = false;

    public ObservableList<FanUnit> calculate(TextField fieldNegativeLimit, TextField fieldPositiveLimit, ObservableList<FanUnit> data, ProgressBar pb, Label labelProgressBar) {
        isStop = false;
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
                                int index = data.indexOf(u) + 1;
                                if (isStop) {
                                    return;
                                }
                                System.out.println("Начало расчета вентилятора " + index);
                                u.setFan(browserService.calculate(
                                        u.getAirFlow(),
                                        u.getAirDrop(),
                                        u.getTypeMontage(),
                                        u.getSubType()));
                                System.out.println("Установка " + index + " посчитана!");
                                Thread t2 = new Thread(() -> runLater(() -> progressBar(index, data.size(), pb, labelProgressBar, u)));
                                t2.start();
                                System.out.println("Установка " + index + " поток прогресс бара завершен!");
                                t2.interrupt();
                                System.out.printf("Установка %d посчитана", index);
                                System.out.println();
                            });
        return data;
    }

    private synchronized void progressBar(int index, int size, ProgressBar pb, Label labelProgressBar, FanUnit u) {
        pb.setVisible(true);
        pb.setProgress((double) (index) / size);
        labelProgressBar.setVisible(true);
        labelProgressBar.setText(String.format("Посчитано %d установок из %d", index, size));
        System.out.println("Установка " + (index) + " добавлена в прогресс бар!");
    }

    public void initializeBrowser() {
        browserService.initializeBrowser();
    }

    public void stopCalculation() {
        isStop = false;
    }
}
