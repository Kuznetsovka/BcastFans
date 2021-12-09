package com.systemair.bcastfans.controller;

import com.systemair.bcastfans.domain.FanUnit;
import com.systemair.bcastfans.service.BrowserService;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import lombok.SneakyThrows;
import org.apache.log4j.Logger;

import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import static javafx.application.Platform.runLater;

public class BrowserController {
    private final BrowserService browserService = new BrowserService();
    private static boolean isStop = false;
    private static final Logger LOGGER = Logger.getLogger(BrowserController.class.getName());
    private final TableController tableController;

    public BrowserController(TableController tableController) {
        this.tableController = tableController;
    }

    public ObservableList<FanUnit> calculate(TextField fieldNegativeLimit, TextField fieldPositiveLimit, ObservableList<FanUnit> data, ProgressBar pb, Label labelProgressBar) {
        isStop = false;
        initProgressBar(data, pb, labelProgressBar);
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
                                LOGGER.info("Начало расчета вентилятора " + index);
                                u.setFan(browserService.calculate(
                                        u.getAirFlow(),
                                        u.getAirDrop(),
                                        u.getTypeMontage(),
                                        u.getSubType()));
                                tableController.fillFan(data);
                                Thread t2 = new Thread(() -> runLater(() -> progressBar(index, data.size(), pb, labelProgressBar)));
                                t2.start();
                                LOGGER.info("Установка " + u.getName() + " добавлена в прогресс бар!");
                                LOGGER.info("Установка " + u.getName() + " посчитана");
                                String absFileName = getCorrectSavePath(u.getName(), u.getModel());
                                if (!u.getModel().equals("")) {
                                    downloadUsingNIO(u.getFan().getShortLink(), absFileName);
                                    LOGGER.info("Установка " + u.getName() + " выгружена");
                                }
                            });
        return data;
    }

    private void initProgressBar(ObservableList<FanUnit> data, ProgressBar pb, Label labelProgressBar) {
        new Thread(() -> runLater(() -> {
                    pb.setProgress(0.0);
                    pb.setVisible(true);
                    labelProgressBar.setText("Посчитано 0 установок из " + data.size());
                    labelProgressBar.setVisible(true);
                }
        )).start();
    }

    private String getCorrectSavePath(String name, String model) {
        String fileName = name + " " + model + ".pdf";
        fileName = fileName.replaceAll("[^а-яА-Яa-zA-Z0-9 .\\-]", "_");
        String path = tableController.getFieldPathDownloading().getText();
        return path + "/" + fileName;
    }

    private synchronized void progressBar(int index, int size, ProgressBar pb, Label labelProgressBar) {
        pb.setProgress((double) (index) / size);
        labelProgressBar.setText(String.format("Посчитано %d установок из %d", index, size));
    }

    public void initializeBrowser() {
        browserService.initializeBrowser();
    }

    public void stopCalculation() {
        isStop = true;
    }

    // качаем файл с помощью NIO
    @SneakyThrows
    private static void downloadUsingNIO(String urlStr, String file) {
        URL url = new URL(urlStr);
        ReadableByteChannel rbc = Channels.newChannel(url.openStream());
        FileOutputStream fos = new FileOutputStream(file);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();
    }
}
