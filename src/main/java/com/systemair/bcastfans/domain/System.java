package com.systemair.bcastfans.domain;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.CheckBox;

public class System {
    private SimpleStringProperty name;
    private SimpleStringProperty airFlow;
    private SimpleStringProperty airDrop;
    private SimpleStringProperty typeMontage;
    private SimpleStringProperty subType;
    private CheckBox check;
    private Fan fan;

    public System(String name, String airFlow, String airDrop, String typeMontage, String subType) {
        this.name = new SimpleStringProperty(name);
        this.airFlow = new SimpleStringProperty(airFlow);
        this.airDrop = new SimpleStringProperty(airDrop);
        this.typeMontage = new SimpleStringProperty(typeMontage);
        this.subType = new SimpleStringProperty(subType);
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
}
