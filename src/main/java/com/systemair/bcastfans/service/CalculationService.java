package com.systemair.bcastfans.service;

import com.systemair.bcastfans.domain.FanUnit;
import com.systemair.bcastfans.domain.RectangleModels;
import com.systemair.bcastfans.domain.RoofModels;
import com.systemair.bcastfans.domain.RoundModels;
import com.systemair.exchangers.ExchangersApplication;
import com.systemair.exchangers.domain.exchangers.Exchanger;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;

import java.util.Map;

public interface CalculationService {
    ObservableList<FanUnit> calculate(TextField fieldNegativeLimit, TextField fieldPositiveLimit, ObservableList<FanUnit> data, ProgressIndicator pi, Label labelProgressBar, boolean isFillTableByOne, ListView<RectangleModels> listRectangleFans, ListView<RoundModels> listRoundFans, ListView<RoofModels> listRoofFans);

    void stopCalculation();

    Map<Integer, Exchanger> calculationExchangers(ExchangersApplication exchangersApplication, Map<Integer, Exchanger> exchangerMap);
}
