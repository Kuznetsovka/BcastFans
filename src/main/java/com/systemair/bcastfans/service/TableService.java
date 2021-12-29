package com.systemair.bcastfans.service;

import com.systemair.bcastfans.domain.*;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

public class TableService {
    public void fillInputData(ObservableList<FanUnit> inputData, TableView<FanUnit> tableInputData, TableColumn<FanUnit, String> columnNumberSystem, TableColumn<FanUnit, String> columnAirFlow, TableColumn<FanUnit, String> columnAirDrop, TableColumn<FanUnit, TypeMontage> columnTypeMontage, TableColumn<FanUnit, SubType> columnSubType, TableColumn<FanUnit, Dimension> columnDimension) {
        tableInputData.setEditable(true);

        // ==== Name ====

        setCellFactoryForColumn(columnNumberSystem, "name");
        columnNumberSystem.setOnEditCommit(
                (TableColumn.CellEditEvent<FanUnit, String> t) -> (
                        t.getTableView().getItems().get(t.getTablePosition().getRow())
                ).setName(t.getNewValue()));

        // ==== airFlow ====

        setCellFactoryForColumn(columnAirFlow, "airFlow");
        columnAirFlow.setOnEditCommit(
                (TableColumn.CellEditEvent<FanUnit, String> t) -> (
                        t.getTableView().getItems().get(t.getTablePosition().getRow())
                ).setAirFlow(t.getNewValue()));

        // ==== airDrop ====

        setCellFactoryForColumn(columnAirDrop, "airDrop");
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

        // ==== Dimension ====

        ObservableList<RoundDimension.RoundDimensionType> roundDimensions = FXCollections.observableArrayList(
                RoundDimension.values);
        ObservableList<RectangleDimension.RectangleDimensionType> rectangleDimensions = FXCollections.observableArrayList(
                RectangleDimension.values);
        columnDimension.setCellValueFactory(param -> {
            FanUnit system = param.getValue();
            Dimension dimension = system.getDimension();
            return new SimpleObjectProperty<>(dimension);
        });
        columnDimension.setOnEditCommit((TableColumn.CellEditEvent<FanUnit, Dimension> event) -> {
            TablePosition<FanUnit, Dimension> pos = event.getTablePosition();
            Dimension newValue = event.getNewValue();
            int row = pos.getRow();
            FanUnit system = event.getTableView().getItems().get(row);
            system.setDimension(newValue);
        });

        tableInputData.setItems(inputData);
    }

    private void setCellFactoryForColumn (TableColumn<FanUnit, String> column, String name)  {
        column.setCellValueFactory(
                new PropertyValueFactory<>(name));
        column.setCellFactory(TextFieldTableCell.forTableColumn());
    }

    public void fillResultData(ObservableList<FanUnit> inputData, TableView<FanUnit> tableInputData, TableColumn<FanUnit, String> columnModel, TableColumn<FanUnit, String> columnArticle, TableColumn<FanUnit, String> columnPower, TableColumn<FanUnit, String> columnPhase, TableColumn<FanUnit, String> columnPrice) {
        tableInputData.setEditable(true);

        // ==== Model ====

        setCellFactoryForColumn(columnModel, "model");
        columnModel.setOnEditCommit(
                (TableColumn.CellEditEvent<FanUnit, String> t) -> (
                        t.getTableView().getItems().get(t.getTablePosition().getRow())
                ).setModel(t.getNewValue()));

        // ==== Article ====

        setCellFactoryForColumn(columnArticle, "article");
        columnArticle.setOnEditCommit(
                (TableColumn.CellEditEvent<FanUnit, String> t) -> (
                        t.getTableView().getItems().get(t.getTablePosition().getRow())
                ).setArticle(t.getNewValue()));

        // ==== Power ====

        setCellFactoryForColumn(columnPower, "power");
        columnPower.setOnEditCommit(
                (TableColumn.CellEditEvent<FanUnit, String> t) -> (
                        t.getTableView().getItems().get(t.getTablePosition().getRow())
                ).setPower(t.getNewValue()));

        // ==== Phase ====

        setCellFactoryForColumn(columnPhase, "phase");
        columnPhase.setOnEditCommit(
                (TableColumn.CellEditEvent<FanUnit, String> t) -> (
                        t.getTableView().getItems().get(t.getTablePosition().getRow())
                ).setPhase(t.getNewValue()));

        // ==== Price ====

        setCellFactoryForColumn(columnPrice, "price");
        columnPrice.setOnEditCommit(
                (TableColumn.CellEditEvent<FanUnit, String> t) -> (
                        t.getTableView().getItems().get(t.getTablePosition().getRow())
                ).setPrice(t.getNewValue()));

        tableInputData.setItems(inputData);
    }
}
