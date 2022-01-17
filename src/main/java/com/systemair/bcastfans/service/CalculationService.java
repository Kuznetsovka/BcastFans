package com.systemair.bcastfans.service;

import com.systemair.bcastfans.controller.TableController;
import com.systemair.bcastfans.domain.*;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import org.apache.log4j.Logger;
import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.TimeoutException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.systemair.bcastfans.staticClasses.UtilClass.showAlert;
import static javafx.application.Platform.runLater;

public class CalculationService {
    private final BrowserService browserService = new BrowserService();
    private static boolean isStop = false;
    private static final Logger LOGGER = Logger.getLogger(CalculationService.class.getName());
    private final TableController tableController;
    private final Map<FanUnit, Fan> hashMap = new HashMap<>();
    private boolean isFilterFans;

    public CalculationService(TableController tableController) {
        this.tableController = tableController;
    }

    public ObservableList<FanUnit> calculate(TextField fieldNegativeLimit, TextField fieldPositiveLimit, ObservableList<FanUnit> data, ProgressIndicator pi, Label labelProgressBar, boolean isFillTableByOne, ListView<RectangleModels> listRectangleFans, ListView<RoundModels> listRoundFans, ListView<RoofModels> listRoofFans) {
        isStop = false;
        AtomicInteger index = new AtomicInteger();
        long count = data.filtered(u -> u.getCheck().isSelected()).size();
        initProgressBar(count, pi, labelProgressBar);
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
                                index.getAndIncrement();
                                if (isStop) return;
                                LOGGER.info("Начало расчета вентилятора " + u.getName());
                                List<String> selectedList = getSelectedList(u.getTypeMontage(), listRectangleFans, listRoofFans, listRoundFans);
                                getCurrentFan(u, selectedList);
                                if (isFillTableByOne) tableController.fillFan(data);
                                Thread t2 = new Thread(() -> runLater(() -> progressBar(index.get(), count, pi, labelProgressBar)));
                                t2.start();
                                t2.interrupt();
                                LOGGER.info("Установка " + u.getName() + " посчитана");
                                String absFileName = getCorrectSavePath(u.getName(), u.getModel());
                                if (!u.getModel().equals("") && tableController.isSaveTechData().isSelected()) {
                                    downloadUsingNIO(u.getFan().getShortLink(), absFileName);
                                    LOGGER.info("Установка " + u.getName() + " выгружена");
                                }
                            });
        hashMap.clear();
        return data;
    } //TODO Проверить, сейчас фильтрует только первую позицию

    private List<String> getSelectedList(TypeMontage typeMontage, ListView<RectangleModels> listRectangleFans, ListView<RoofModels> listRoofFans, ListView<RoundModels> listRoundFans) {
        isFilterFans = true;
        switch (typeMontage) {
            case ROOF:
                isFilterFans = !isAllSelectedItemsInListBox(listRoundFans);
                return listRoofFans.getItems().stream().map(Enum::toString).collect(Collectors.toList());
            case ROUND:
                isFilterFans = !isAllSelectedItemsInListBox(listRectangleFans);
                return listRoundFans.getItems().stream().map(Enum::toString).collect(Collectors.toList());
            case RECTANGLE:
                isFilterFans = !isAllSelectedItemsInListBox(listRoofFans);
                return listRectangleFans.getItems().stream().map(Enum::toString).collect(Collectors.toList());
            case ROUND_AND_RECTANGLE:
                isFilterFans = !isAllSelectedItemsInListBox(listRoundFans) || !isAllSelectedItemsInListBox(listRectangleFans);
                Stream<RectangleModels> s1 = listRectangleFans.getItems().stream();
                Stream<RoundModels> s2 = listRoundFans.getItems().stream();
                return Stream.concat(s1, s2).map(Enum::toString).collect(Collectors.toList());
            default:
                throw new IllegalStateException("Unexpected value: " + typeMontage);
        }
    }

    private <T> boolean isAllSelectedItemsInListBox(ListView<T> list) {
        return list.getSelectionModel().getSelectedItems().size() == list.getItems().size();
    }

    private void getCurrentFan(FanUnit u, List<String> selectedList) {
        if (!hashMap.containsKey(u)) {
            Fan currentFan = new Fan();
            try {
                currentFan = isFilterFans ?
                        browserService.calculate(u.getAirFlow(), u.getAirDrop(), u.getTypeMontage(), u.getSubType(), u.getDimension(),selectedList) :
                        browserService.calculate(u.getAirFlow(), u.getAirDrop(), u.getTypeMontage(), u.getSubType(), u.getDimension());
            } catch (TimeoutException | NoSuchSessionException e) {
                Thread t = new Thread(() -> runLater(() -> showAlert(LOGGER, e.getMessage(), Alert.AlertType.WARNING)));
                t.start();
            }
            u.setFan(currentFan);
            hashMap.put(u, currentFan);
        } else {
            LOGGER.info("Установка уже была посчитана!");
            u.setFan(hashMap.get(u));
        }
    }

    private void initProgressBar(long count, ProgressIndicator pi, Label labelProgressBar) {
        Thread t1 = new Thread(() -> runLater(() -> {
                    pi.setProgress(0.0);
                    pi.setVisible(true);
                    labelProgressBar.setText("Посчитано 0 установок из " + count);
                    labelProgressBar.setVisible(true);
                }
        ));
        t1.start();
        t1.interrupt();
    }

    private String getCorrectSavePath(String name, String model) {
        String fileName = name + " " + model + ".pdf";
        fileName = fileName.replaceAll("[^а-яА-Яa-zA-Z0-9 .\\-]", "_");
        String path = tableController.getFieldPathDownloading().getText();
        return path + "/" + fileName;
    }

    private synchronized void progressBar(int index, long size, ProgressIndicator pi, Label labelProgressBar) {
        pi.setProgress((double) (index) / size);
        labelProgressBar.setText(String.format("Посчитано %d установок из %d", index, size));
    }

    public void initializeBrowser() {
        browserService.initializeBrowser();
    }

    public void stopCalculation() {
        isStop = true;
    }

    private static void downloadUsingNIO(String urlStr, String file) {
        try {
            URL url = new URL(urlStr);
            ReadableByteChannel rbc = Channels.newChannel(url.openStream());
            FileOutputStream fos = new FileOutputStream(file);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
            rbc.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }
    }
}
