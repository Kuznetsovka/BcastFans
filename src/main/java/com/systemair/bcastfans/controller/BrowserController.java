package com.systemair.bcastfans.controller;

import com.systemair.bcastfans.domain.FanUnit;
import com.systemair.bcastfans.service.BrowserService;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import lombok.SneakyThrows;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.logging.Logger;

import static com.systemair.bcastfans.UtilClass.PATH_TEST;
import static javafx.application.Platform.runLater;

public class BrowserController {
    private final BrowserService browserService = new BrowserService();
    private static boolean isStop = false;
    private static final Logger LOGGER = Logger.getLogger(BrowserController.class.getName());

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
                                LOGGER.info("Начало расчета вентилятора " + index);
                                u.setFan(browserService.calculate(
                                        u.getAirFlow(),
                                        u.getAirDrop(),
                                        u.getTypeMontage(),
                                        u.getSubType()));
                                Thread t2 = new Thread(() -> runLater(() -> progressBar(index, data.size(), pb, labelProgressBar, u)));
                                t2.start();
                                LOGGER.info("Установка " + index + " поток прогресс бара завершен!");
                                t2.interrupt();
                                LOGGER.info("Установка " + index + " посчитана");
                                String fileName = PATH_TEST + "/" + u.getName() + " " + u.getModel() + ".pdf";
                                downloadUsingNIO(u.getFan().getShortLink(), fileName);
                            });
        return data;
    }

    private synchronized void progressBar(int index, int size, ProgressBar pb, Label labelProgressBar, FanUnit u) {
        pb.setVisible(true);
        pb.setProgress((double) (index) / size);
        labelProgressBar.setVisible(true);
        labelProgressBar.setText(String.format("Посчитано %d установок из %d", index, size));
        LOGGER.info("Установка " + (index) + " добавлена в прогресс бар!");
    }

    public void initializeBrowser() {
        browserService.initializeBrowser();
    }

    public void stopCalculation() {
        isStop = true;
    }

    // качаем файл с помощью Stream
    private static void downloadUsingStream(String urlStr, String file) throws IOException {
        URL url = new URL(urlStr);
        BufferedInputStream bis = new BufferedInputStream(url.openStream());
        FileOutputStream fis = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int count = 0;
        while((count = bis.read(buffer,0,1024)) != -1)
        {
            fis.write(buffer, 0, count);
        }
        fis.close();
        bis.close();
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
