package com.systemair.bcastfans.service;

import com.systemair.bcastfans.domain.SubType;
import com.systemair.bcastfans.domain.FanUnit;
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
    public void fillInputData(ObservableList<FanUnit> inputData, TableView<FanUnit> tableInputData, TableColumn<FanUnit, String> columnNumberSystem, TableColumn<FanUnit, String> columnAirFlow, TableColumn<FanUnit, String> columnAirDrop, TableColumn<FanUnit, TypeMontage> columnTypeMontage, TableColumn<FanUnit, SubType> columnSubType) {
        tableInputData.setEditable(true);

        // ==== Name ====

        columnNumberSystem.setCellValueFactory(
                new PropertyValueFactory<>("name"));
        columnNumberSystem.setCellFactory(TextFieldTableCell.forTableColumn());
        columnNumberSystem.setOnEditCommit(
                (TableColumn.CellEditEvent<FanUnit, String> t) -> (
                        t.getTableView().getItems().get(t.getTablePosition().getRow())
                ).setName(t.getNewValue()));

        // ==== airFlow ====

        columnAirFlow.setCellValueFactory(
                new PropertyValueFactory<>("airFlow"));
        columnAirFlow.setCellFactory(TextFieldTableCell.forTableColumn());
        columnAirFlow.setOnEditCommit(
                (TableColumn.CellEditEvent<FanUnit, String> t) -> (
                        t.getTableView().getItems().get(t.getTablePosition().getRow())
                ).setAirFlow(t.getNewValue()));

        // ==== airDrop ====

        columnAirDrop.setCellValueFactory(
                new PropertyValueFactory<>("airDrop"));
        columnAirDrop.setCellFactory(TextFieldTableCell.forTableColumn());
        columnAirDrop.setOnEditCommit(
                (TableColumn.CellEditEvent<FanUnit, String> t) -> (
                        t.getTableView().getItems().get(t.getTablePosition().getRow())
                ).setAirDrop(t.getNewValue()));

        // ==== TypeMontage ====

        ObservableList<TypeMontage> typeMontages = FXCollections.observableArrayList(
                TypeMontage.values());

        columnTypeMontage.setCellValueFactory(param -> {
            FanUnit system = param.getValue();
            TypeMontage typeMontage = system.getTypeMontage();
            return new SimpleObjectProperty<>(typeMontage);
        });

        columnTypeMontage.setCellFactory(ComboBoxTableCell.forTableColumn(typeMontages));

        columnTypeMontage.setOnEditCommit((TableColumn.CellEditEvent<FanUnit, TypeMontage> event) -> {
            TablePosition<FanUnit, TypeMontage> pos = event.getTablePosition();

            TypeMontage newTypeMontage = event.getNewValue();

            int row = pos.getRow();
            FanUnit system = event.getTableView().getItems().get(row);

            system.setTypeMontage(newTypeMontage);
        });

        // ==== SubType ====

        ObservableList<SubType> subTypes = FXCollections.observableArrayList(
                SubType.values());

        columnSubType.setCellValueFactory(param -> {
            FanUnit system = param.getValue();
            SubType subType = system.getSubType();
            return new SimpleObjectProperty<>(subType);
        });

        columnSubType.setCellFactory(ComboBoxTableCell.forTableColumn(subTypes));

        columnSubType.setOnEditCommit((TableColumn.CellEditEvent<FanUnit, SubType> event) -> {
            TablePosition<FanUnit, SubType> pos = event.getTablePosition();

            SubType newSubType = event.getNewValue();

            int row = pos.getRow();
            FanUnit system = event.getTableView().getItems().get(row);

            system.setSubType(newSubType);
        });

        tableInputData.setItems(inputData);
    }
}
