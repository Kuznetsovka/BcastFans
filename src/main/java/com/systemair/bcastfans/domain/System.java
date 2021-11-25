package com.systemair.bcastfans.domain;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class System {
    private SimpleStringProperty name;
    private SimpleStringProperty airFlow;
    private SimpleStringProperty airDrop;
    private SimpleStringProperty typeMontage;
    private SimpleStringProperty subType;
    private CheckBox check;
    private Fan fan;
    private Map<Integer,String> row = new HashMap<>();

    public System(String name, String airFlow, String airDrop, String typeMontage, String subType) {
        this.name = new SimpleStringProperty(name);
        this.airFlow = new SimpleStringProperty(airFlow);
        this.airDrop = new SimpleStringProperty(airDrop);
        this.typeMontage = new SimpleStringProperty(typeMontage);
        this.subType = new SimpleStringProperty(subType);
        check = new CheckBox();
    }

    public System(ArrayList<String> row) {
        this.name = new SimpleStringProperty(row.get(0));
        this.airFlow = new SimpleStringProperty(row.get(1));
        this.airDrop = new SimpleStringProperty(row.get(2));
        this.typeMontage = new SimpleStringProperty(row.get(3));
        if (row.size() > 4)
            this.subType = new SimpleStringProperty(row.get(4));
        else
            this.subType = new SimpleStringProperty();
        check = new CheckBox();
    }

    public Fan getFan() {
        return fan;
    }

    public void setFan(Fan fan) {
        this.fan = fan;
    }

    public CheckBox getCheck() {
        return check;
    }

    public void setCheck(Boolean value) {
        check.setSelected(value);
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getAirFlow() {
        return airFlow.get();
    }

    public SimpleStringProperty airFlowProperty() {
        return airFlow;
    }

    public void setAirFlow(String airFlow) {
        this.airFlow.set(airFlow);
    }

    public String getAirDrop() {
        return airDrop.get();
    }

    public SimpleStringProperty airDropProperty() {
        return airDrop;
    }

    public void setAirDrop(String airDrop) {
        this.airDrop.set(airDrop);
    }

    public String getTypeMontage() {
        return typeMontage.get();
    }

    public SimpleStringProperty typeMontageProperty() {
        return typeMontage;
    }

    public void setTypeMontage(String typeMontage) {
        this.typeMontage.set(typeMontage);
    }

    public String getSubType() {
        return subType.get();
    }

    public SimpleStringProperty subTypeProperty() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType.set(subType);
    }

    public Map<Integer, String> getRow() {
        row.clear();
        row.put(0,check.isSelected()?"Да":"Нет");
        row.put(1, name.getValue());
        row.put(2, airFlow.getValue());
        row.put(3, airDrop.getValue());
        row.put(4, typeMontage.getValue());
        row.put(5, subType.getValue());
        if (fan != null) {
            row.put(6, fan.getModel());
            row.put(7,fan.getArticle());
            row.put(8, String.valueOf(fan.getPower()));
            row.put(9,fan.getPhase());
            row.put(10, String.valueOf(fan.getPrice()));
        }
        return row;
    }

    public void setRow(Map<Integer, String> row) {
        this.row = row;
    }
}
