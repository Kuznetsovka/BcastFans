package com.systemair.bcastfans;

import com.systemair.bcastfans.domain.System;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import static com.systemair.bcastfans.domain.SubType.*;
import static com.systemair.bcastfans.domain.TypeMontage.*;

public class TableController {
    @FXML
    TableView tableInputData;
    @FXML
    TableView tableResult;
    @FXML
    TableColumn columnChoose;
    @FXML
    TableColumn columnNumberSystem;
    @FXML
    TableColumn columnAirFlow;
    @FXML
    TableColumn columnAirDrop;
    @FXML
    TableColumn columnTypeMontage;
    @FXML
    TableColumn columnSubType;
    @FXML
    TableColumn columnModel;
    @FXML
    TableColumn columnArticle;
    @FXML
    TableColumn columnPower;
    @FXML
    TableColumn columnPhase;
    @FXML
    TableColumn columnPrice;

    private final ObservableList<System> inputData =
            FXCollections.observableArrayList(
                    new System("P1", 1000, 200, ROUND, KITCHEN),
                    new System("P2", 2000, 300, RECTANGLE, EC),
                    new System("P3", 3000, 400, ROUND, SILENT),
                    new System("P4", 4000, 500, RECTANGLE, KITCHEN),
                    new System("P5", 5000, 100, ROUND, EC)
            );

    @FXML
    protected void onHelloButtonClick() {
    }
}