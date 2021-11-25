package com.systemair.bcastfans.service;

import com.systemair.bcastfans.domain.SubType;
import com.systemair.bcastfans.domain.System;
import com.systemair.bcastfans.domain.TypeMontage;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;

public class TableService {
    public void fillInputData(ObservableList<System> inputData, TableView<System> tableInputData, TableColumn<System, String> columnNumberSystem, TableColumn<System, String> columnAirFlow, TableColumn<System, String> columnAirDrop, TableColumn<System, TypeMontage> columnTypeMontage, TableColumn<System, SubType> columnSubType) {
        tableInputData.setEditable(true);

        columnNumberSystem.setCellValueFactory(
                new PropertyValueFactory<>("name"));
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

        // ==== TypeMontage ====

        ObservableList<TypeMontage> typeMontages = FXCollections.observableArrayList(
                TypeMontage.values());

        columnTypeMontage.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<System, TypeMontage>, ObservableValue<TypeMontage>>() {

            @Override
            public ObservableValue<TypeMontage> call(TableColumn.CellDataFeatures<System, TypeMontage> param) {
                System system = param.getValue();
                // F,M
                TypeMontage typeMontage = system.getTypeMontage();
                return new SimpleObjectProperty<TypeMontage>(typeMontage);
            }
        });

        columnTypeMontage.setCellFactory(ComboBoxTableCell.forTableColumn(typeMontages));

        columnTypeMontage.setOnEditCommit((TableColumn.CellEditEvent<System, TypeMontage> event) -> {
            TablePosition<System, TypeMontage> pos = event.getTablePosition();

            TypeMontage newTypeMontage = event.getNewValue();

            int row = pos.getRow();
            System system = event.getTableView().getItems().get(row);

            system.setTypeMontage(newTypeMontage);
        });

        // ==== SubType ====

        ObservableList<SubType> subTypes = FXCollections.observableArrayList(
                SubType.values());

        columnSubType.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<System, SubType>, ObservableValue<SubType>>() {

            @Override
            public ObservableValue<SubType> call(TableColumn.CellDataFeatures<System, SubType> param) {
                System system = param.getValue();
                // F,M
                SubType subType = system.getSubType();
                return new SimpleObjectProperty<SubType>(subType);
            }
        });

        columnSubType.setCellFactory(ComboBoxTableCell.forTableColumn(subTypes));

        columnSubType.setOnEditCommit((TableColumn.CellEditEvent<System, SubType> event) -> {
            TablePosition<System, SubType> pos = event.getTablePosition();

            SubType newSubType = event.getNewValue();

            int row = pos.getRow();
            System system = event.getTableView().getItems().get(row);

            system.setSubType(newSubType);
        });

        tableInputData.setItems(inputData);
    }
}
