package com.systemair.bcastfans.domain;

import com.systemair.bcastfans.intarface.EnumInterface;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.CheckBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FanUnit {
    private SimpleStringProperty name;
    private SimpleStringProperty airFlow;
    private SimpleStringProperty airDrop;
    private TypeMontage typeMontage;
    private SubType subType;
    private CheckBox check = new CheckBox();
    private Fan fan;
    private Map<Integer,String> row = new HashMap<>();

    public FanUnit(String name, String airFlow, String airDrop, String typeMontage, String subType) {
        this.name = new SimpleStringProperty(name);
        this.airFlow = new SimpleStringProperty(airFlow);
        this.airDrop = new SimpleStringProperty(airDrop);
        this.typeMontage = TypeMontage.valueOf(typeMontage);
        this.subType = SubType.valueOf(subType);
        check = new CheckBox();
    }

    public FanUnit(ArrayList<String> row) {
        check.setSelected(row.get(0).equals("Да"));
        this.name = new SimpleStringProperty(row.get(1));
        this.airFlow = new SimpleStringProperty(row.get(2));
        this.airDrop = new SimpleStringProperty(row.get(3));
        this.typeMontage = TypeMontage.getByDescription(row.get(4));
        if (row.size() > 5)
            this.subType = SubType.getByDescription(row.get(5));
        else
            this.subType = SubType.NONE;

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

    public TypeMontage getTypeMontage() {
        return typeMontage;
    }

    public void setTypeMontage(TypeMontage typeMontage) {
        this.typeMontage = typeMontage;
    }

    public SubType getSubType() {
        return subType;
    }

    public void setSubType(SubType subType) {
        this.subType = subType;
    }

    public Map<Integer, String> getRow() {
        row.clear();
        row.put(0,check.isSelected()?"Да":"Нет");
        row.put(1, name.getValue());
        row.put(2, airFlow.getValue());
        row.put(3, airDrop.getValue());
        row.put(4, typeMontage.getDescription());
        row.put(5, subType.getDescription());
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
