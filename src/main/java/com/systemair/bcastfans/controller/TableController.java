package com.systemair.bcastfans.controller;

import com.systemair.bcastfans.domain.Fan;
import com.systemair.bcastfans.domain.System;
import com.systemair.bcastfans.service.TableService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.Getter;
import lombok.Setter;

import java.net.URL;
import java.util.ResourceBundle;

@Getter
@Setter
public class TableController implements Initializable {
    private TableService tableService = new TableService();
    @FXML
    TableView<System> tableInputData;
    @FXML
    private CheckBox checkBox;
    @FXML
    TableColumn<System, Boolean> columnChoose;
    @FXML
    TableColumn<System,String> columnNumberSystem;
    @FXML
    TableColumn<System,Integer> columnAirFlow;
    @FXML
    TableColumn<System,Integer> columnAirDrop;
    @FXML
    TableColumn<System,String> columnTypeMontage;
    @FXML
    TableColumn<System,String> columnSubType;
    @FXML
    TableColumn<Fan,String> columnModel;
    @FXML
    TableColumn<Fan,String> columnArticle;
    @FXML
    TableColumn<Fan,Double> columnPower;
    @FXML
    TableColumn<Fan,String> columnPhase;
    @FXML
    TableColumn<Fan,Double> columnPrice;

    private final ObservableList<System> inputData =
            FXCollections.observableArrayList(
                    new System("P2", "2000", "300", "RECTANGLE", "EC"),
                    new System("P3", "3000", "400", "ROUND", "SILENT"),
                    new System("P1", "1000", "200", "ROUND", "KITCHEN"),
                    new System("P4", "4000", "500", "RECTANGLE", "KITCHEN"),
                    new System("P5", "5000", "100", "ROUND", "EC")
            );
    @FXML
    protected void onHelloButtonClick() {
        tableService.fillData(inputData,tableInputData,columnNumberSystem,columnAirFlow,columnAirDrop,columnTypeMontage,columnSubType);
    }

    @FXML
    public void checkBoxInitialize(ActionEvent actionEvent) {
        boolean checkBtn = checkBox.isSelected();
        for (System n : inputData) {
            n.setCheck(checkBtn);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        columnChoose.setCellValueFactory(new PropertyValueFactory<>("check"));
    }
}