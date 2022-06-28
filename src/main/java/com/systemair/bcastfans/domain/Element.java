package com.systemair.bcastfans.domain;

public class Element {
    private String name;
    private String model;
    private String pressureDrop;

    public Element(String name, String model, String pressureDrop) {
        this.name = name;
        this.model = model;
        this.pressureDrop = pressureDrop;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getPressureDrop() {
        return pressureDrop;
    }

    public void setPressureDrop(String pressureDrop) {
        this.pressureDrop = pressureDrop;
    }
}
