package com.systemair.bcastfans.service;

import com.systemair.bcastfans.domain.FanUnit;
import com.systemair.bcastfans.domain.SubType;
import com.systemair.bcastfans.domain.TypeMontage;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public interface TableService {
    void fillInputData(ObservableList<FanUnit> inputData, TableView<FanUnit> tableInputData, TableColumn<FanUnit, String> columnNumberSystem, TableColumn<FanUnit, String> columnAirFlow, TableColumn<FanUnit, String> columnAirDrop, TableColumn<FanUnit, TypeMontage> columnTypeMontage, TableColumn<FanUnit, SubType> columnSubType, TableColumn<FanUnit, String> columnDimension);

    void setCellFactoryForColumn(TableColumn<FanUnit, String> column, String name);

    void fillResultData(ObservableList<FanUnit> inputData, TableView<FanUnit> tableInputData, TableColumn<FanUnit, String> columnModel, TableColumn<FanUnit, String> columnArticle, TableColumn<FanUnit, String> columnPower, TableColumn<FanUnit, String> columnPhase, TableColumn<FanUnit, String> columnPrice);
}
