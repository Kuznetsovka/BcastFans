package com.systemair.bcastfans.service;

import com.systemair.bcastfans.domain.System;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class TableService {
    private final ObservableList<System> inputData =
            FXCollections.observableArrayList(
                    new System("P2", "2000", "300", "RECTANGLE", "EC"),
                    new System("P3", "3000", "400", "ROUND", "SILENT"),
                    new System("P1", "1000", "200", "ROUND", "KITCHEN"),
                    new System("P4", "4000", "500", "RECTANGLE", "KITCHEN"),
                    new System("P5", "5000", "100", "ROUND", "EC")
            );

    public void fillData(ObservableList<System> inputData, TableView<System> tableInputData, TableColumn<System, String> columnNumberSystem, TableColumn<System, Integer> columnAirFlow, TableColumn<System, Integer> columnAirDrop, TableColumn<System, String> columnTypeMontage, TableColumn<System, String> columnSubType) {
        tableInputData.setEditable(true);

        columnNumberSystem.setCellValueFactory(
                new PropertyValueFactory<>("name"));

        columnAirFlow.setCellValueFactory(
                new PropertyValueFactory<>("airFlow"));


        columnAirDrop.setCellValueFactory(
                new PropertyValueFactory<>("airDrop"));


        columnTypeMontage.setCellValueFactory(
                new PropertyValueFactory<>("typeMontage"));

        columnSubType.setCellValueFactory(
                new PropertyValueFactory<>("subType"));

        tableInputData.setItems(inputData);
    }
}
