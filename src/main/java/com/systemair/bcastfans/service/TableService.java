package com.systemair.bcastfans.service;

import com.systemair.bcastfans.domain.System;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

public class TableService {
    public void fillData(ObservableList<System> inputData, TableView<System> tableInputData, TableColumn<System, String> columnNumberSystem, TableColumn<System, String> columnAirFlow, TableColumn<System, String> columnAirDrop, TableColumn<System, String> columnTypeMontage, TableColumn<System, String> columnSubType) {
        tableInputData.setEditable(true);
        columnNumberSystem.setEditable(true);
        columnAirFlow.setEditable(true);
        columnNumberSystem.setCellValueFactory(new PropertyValueFactory<>("name"));

        columnNumberSystem.setCellFactory(TextFieldTableCell.forTableColumn());
        columnNumberSystem.setOnEditCommit(
                (TableColumn.CellEditEvent<System, String> t) -> {
                    (t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                    ).setName(t.getNewValue());
                });

        columnAirFlow.setCellValueFactory(
                new PropertyValueFactory<>("airFlow"));
        columnAirFlow.setCellFactory(TextFieldTableCell.forTableColumn());
        columnAirFlow.setOnEditCommit(
                (TableColumn.CellEditEvent<System, String> t) -> {
                    (t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                    ).setAirFlow(t.getNewValue());
                });


        columnAirDrop.setCellValueFactory(
                new PropertyValueFactory<>("airDrop"));
        columnAirDrop.setCellFactory(TextFieldTableCell.forTableColumn());
        columnAirDrop.setOnEditCommit(
                (TableColumn.CellEditEvent<System, String> t) -> {
                    (t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                    ).setAirDrop(t.getNewValue());
                });

        columnTypeMontage.setCellValueFactory(
                new PropertyValueFactory<>("typeMontage"));

        columnTypeMontage.setCellFactory(ComboBoxTableCell.forTableColumn());
        columnTypeMontage.setOnEditCommit(
                (TableColumn.CellEditEvent<System, String> t) -> {
                    (t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                    ).setTypeMontage(t.getNewValue());
                });

        columnSubType.setCellValueFactory(
                new PropertyValueFactory<>("subType"));

        columnSubType.setCellFactory(ComboBoxTableCell.forTableColumn());
        columnSubType.setOnEditCommit(
                (TableColumn.CellEditEvent<System, String> t) -> {
                    (t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                    ).setSubType(t.getNewValue());
                });

        tableInputData.setItems(inputData);
    }
}
