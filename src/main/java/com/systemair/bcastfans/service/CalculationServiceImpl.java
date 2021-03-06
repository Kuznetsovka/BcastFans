package com.systemair.bcastfans.service;

import com.systemair.bcastfans.controller.TableController;
import com.systemair.bcastfans.domain.*;
import com.systemair.bcastfans.service.browser.BrowserService;
import com.systemair.bcastfans.service.browser.SystemairBrowserService;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import org.apache.log4j.Logger;
import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.TimeoutException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.systemair.bcastfans.staticClasses.UtilClass.*;
import static javafx.application.Platform.runLater;

public class CalculationServiceImpl implements CalculationService {

    private final BrowserService browserService = new SystemairBrowserService();
    private static boolean isStop = false;
    private static final Logger LOGGER = Logger.getLogger(CalculationServiceImpl.class.getName());
    private final TableController tableController;
    private final Map<FanUnit, Fan> hashMap = new HashMap<>();
    private boolean isFilterFans;

    public CalculationServiceImpl(TableController tableController) {
        this.tableController = tableController;
        browserService.initializeBrowser();
    }

    @Override
    public ObservableList<FanUnit> calculate(TextField fieldNegativeLimit, TextField fieldPositiveLimit, ObservableList<FanUnit> data, ProgressIndicator pi, Label labelProgressBar, boolean isFillTableByOne, ListView<RectangleModels> listRectangleFans, ListView<RoundModels> listRoundFans, ListView<RoofModels> listRoofFans) {
        isStop = false;
        AtomicInteger index = new AtomicInteger();
        long count = data.filtered(u -> u.getCheck().isSelected()).size();
        tableController.initProgressBar(count, pi, labelProgressBar);
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
                                LOGGER.info("???????????? ?????????????? ?????????????????????? " + u.getName());
                                List<String> selectedList = getSelectedList(u.getTypeMontage(), listRectangleFans, listRoofFans, listRoundFans);
                                getCurrentFan(u, selectedList);
                                if (isFillTableByOne) tableController.fillFan(data);
                                Thread t2 = new Thread(() -> runLater(() -> tableController.progressBar(index.get(), count, pi, labelProgressBar)));
                                t2.start();
                                t2.interrupt();
                                LOGGER.info("?????????????????? " + u.getName() + " ??????????????????");
                                String absFileName = getCorrectSavePath(tableController.getFieldPathDownloading().getText(), u.getName(), u.getModel());
                                if (!u.getModel().equals("") && tableController.isSaveTechData().isSelected()) {
                                    downloadUsingNIO(u.getFan().getShortLink(), absFileName);
                                    LOGGER.info("?????????????????? " + u.getName() + " ??????????????????");
                                }
                            });
        hashMap.clear();
        return data;
    }

    private List<String> getSelectedList(TypeMontage typeMontage, ListView<RectangleModels> listRectangleFans, ListView<RoofModels> listRoofFans, ListView<RoundModels> listRoundFans) {
        isFilterFans = false;
        switch (typeMontage) {
            case ROOF:
                isFilterFans = notAllSelectedItemsInListBox(listRoofFans);
                return listRoofFans.getSelectionModel().getSelectedItems().stream().map(Enum::toString).collect(Collectors.toList());
            case ROUND:
                isFilterFans = notAllSelectedItemsInListBox(listRoundFans);
                return listRoundFans.getSelectionModel().getSelectedItems().stream().map(Enum::toString).collect(Collectors.toList());
            case RECTANGLE:
                isFilterFans = notAllSelectedItemsInListBox(listRectangleFans);
                return listRectangleFans.getSelectionModel().getSelectedItems().stream().map(Enum::toString).collect(Collectors.toList());
            case ROUND_AND_RECTANGLE:
                isFilterFans = notAllSelectedItemsInListBox(listRoundFans) || notAllSelectedItemsInListBox(listRectangleFans);
                Stream<RectangleModels> s1 = listRectangleFans.getSelectionModel().getSelectedItems().stream();
                Stream<RoundModels> s2 = listRoundFans.getSelectionModel().getSelectedItems().stream();
                return Stream.concat(s1, s2).map(Enum::toString).collect(Collectors.toList());
            default:
                throw new IllegalStateException("Unexpected value: " + typeMontage);
        }
    }

    private void getCurrentFan(FanUnit u, List<String> selectedList) {
        if (!hashMap.containsKey(u)) {
            Fan currentFan = new Fan();
            try {
                currentFan = isFilterFans ?
                        browserService.calculate(u.getAirFlow(), u.getAirDrop(), u.getTypeMontage(), u.getSubType(), u.getDimension(), selectedList) :
                        browserService.calculate(u.getAirFlow(), u.getAirDrop(), u.getTypeMontage(), u.getSubType(), u.getDimension());
            } catch (TimeoutException | NoSuchSessionException e) {
                showAlert(LOGGER, e.getMessage(), Alert.AlertType.WARNING);

            }
            u.setFan(currentFan);
            hashMap.put(u, currentFan);
        } else {
            LOGGER.info("?????????????????? ?????? ???????? ??????????????????!");
            u.setFan(hashMap.get(u));
        }
    }

    private <T> boolean notAllSelectedItemsInListBox(ListView<T> list) {
        return list.getSelectionModel().getSelectedItems().size() != list.getItems().size();
    }

    @Override
    public void stopCalculation() {
        isStop = true;
    }
}
