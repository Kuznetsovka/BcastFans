package com.systemair.bcastfans.domain;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.CheckBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FanUnit {
    private final SimpleStringProperty name;
    private final SimpleStringProperty airFlow;
    private final SimpleStringProperty airDrop;
    private SimpleStringProperty model;
    private SimpleStringProperty article;
    private SimpleStringProperty power;
    private SimpleStringProperty phase;
    private SimpleStringProperty price;
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
        this.model = new SimpleStringProperty(fan.getModel());
        this.article = new SimpleStringProperty(fan.getArticle());
        this.power = new SimpleStringProperty(fan.getPower().toString());
        this.phase = new SimpleStringProperty(fan.getPhase());
        this.price = new SimpleStringProperty(fan.getPrice().toString());
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

    public String getModel() {
        return fan.getModel();
    }

    public SimpleStringProperty modelProperty() {
        return model;
    }

    public void setModel(String model) {
        fan.setModel(model);
        this.model.set(model);
    }

    public String getArticle() {
        return fan.getArticle();
    }

    public SimpleStringProperty articleProperty() {
        return article;
    }

    public void setArticle(String article) {
        fan.setArticle(article);
        this.article.set(article);
    }

    public String getPower() {
        return fan.getPower().toString();
    }

    public SimpleStringProperty powerProperty() {
        return power;
    }

    public void setPower(String power) {
        fan.setPower(Double.valueOf(power));
        this.power.set(power);
    }

    public String getPhase() {
        return fan.getPhase();
    }

    public SimpleStringProperty phaseProperty() {
        return phase;
    }

    public void setPhase(String phase) {
        fan.setPhase(phase);
        this.phase.set(phase);
    }

    public String getPrice() {
        return fan.getPrice().toString();
    }

    public SimpleStringProperty priceProperty() {
        return price;
    }

    public void setPrice(String price) {
        fan.setPrice(Double.valueOf(price));
        this.price.set(price);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FanUnit fanUnit = (FanUnit) o;
        return Objects.equals(airFlow, fanUnit.airFlow) && Objects.equals(airDrop, fanUnit.airDrop) && typeMontage == fanUnit.typeMontage && subType == fanUnit.subType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, airFlow, airDrop, model, article, power, phase, price, typeMontage, subType, check, fan, row);
    }
}
