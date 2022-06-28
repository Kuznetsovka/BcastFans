package com.systemair.bcastfans.domain;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.CheckBox;

import java.util.*;

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
    private final SimpleStringProperty dimension;
    private final CheckBox check = new CheckBox();
    private Fan fan;
    private List<Element> elements = new ArrayList<>();
    private Map<Integer, String> row = new HashMap<>();

    public FanUnit(ArrayList<String> columns) {
        check.setSelected(columns.get(0).equals("Да"));
        this.name = new SimpleStringProperty(columns.get(1));
        this.airFlow = new SimpleStringProperty(columns.get(2));
        this.airDrop = new SimpleStringProperty(columns.get(7));
        this.typeMontage = TypeMontage.getByDescription(columns.get(4));
        if (columns.size() > 5)
            this.subType = SubType.getByDescription(columns.get(5));
        else
            this.subType = SubType.NONE;
        if (columns.size() < 7) {
            this.dimension = new SimpleStringProperty("");
        } else if (typeMontage.equals(TypeMontage.ROUND)) {
            this.dimension = new SimpleStringProperty(columns.get(6));
        } else if (typeMontage.equals(TypeMontage.RECTANGLE)) {
            this.dimension = new SimpleStringProperty(columns.get(6));
        } else {
            this.dimension = new SimpleStringProperty("");
        }
        elements.add(new Element("Клапан",columns.get(12),columns.get(13)));
        elements.add(new Element("Фильтр1",columns.get(16),columns.get(17)));
        elements.add(new Element("Фильтр2",columns.get(20),columns.get(21)));
        elements.add(new Element("Шумоглушитель",columns.get(24),columns.get(25)));
        elements.add(new Element("Эл. нагреватель",columns.get(30),columns.get(31)));
        elements.add(new Element("Нагреватель",columns.get(40),columns.get(44)));
        elements.add(new Element("Охладитель",columns.get(53),columns.get(57)));
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
        //createMapFan();
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

    public String getDimension() {
        return dimension == null ? "" : dimension.get();
    }

    public SimpleStringProperty dimensionProperty() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension.set(dimension);
    }

    public Map<Integer, String> getRow() {
        createMapFan();
        return row;
    }

    private void createMapFan() {
        row.clear();
        row.put(0, check.isSelected() ? "Да" : "Нет");
        row.put(1, name.getValue());
        row.put(2, airFlow.getValue());
        row.put(3, airDrop.getValue());
        row.put(4, typeMontage.getDescription());
        row.put(5, subType.getDescription());
        row.put(6, dimension.getValue());
        if (fan != null) {
            row.put(7, fan.getModel());
            row.put(8, fan.getArticle());
            row.put(9, String.valueOf(fan.getPower()));
            row.put(10, fan.getPhase());
            row.put(11, String.valueOf(fan.getPrice()));
        } else {
            row.put(7, "");
            row.put(8, "");
            row.put(9, "");
            row.put(10, "");
            row.put(11, "");
        }
        int count = 0;
        for (int i = 12; i < 25; i += 2) {
            row.put(i,elements.get(count).getModel());
            row.put(i + 1,elements.get(count++).getPressureDrop());
        }
    }

    public void setRow(Map<Integer, String> row) {
        this.row = row;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FanUnit fanUnit = (FanUnit) o;
        return Double.valueOf(airFlow.getValue()).intValue() == Double.valueOf(fanUnit.airFlow.getValue()).intValue() &&
                Double.valueOf(airDrop.getValue()).intValue() == Double.valueOf(fanUnit.airDrop.getValue()).intValue() &&
                typeMontage == fanUnit.typeMontage &&
                subType == fanUnit.subType &&
                dimension.getValue().equals(fanUnit.dimension.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(Double.valueOf(airFlow.getValue()).intValue(), Double.valueOf(airDrop.getValue()).intValue(), typeMontage, subType, dimension.getValue());
    }
}
