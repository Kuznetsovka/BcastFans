package com.systemair.bcastfans.domain;

import com.systemair.bcastfans.myInterface.Describable;

public enum TypeMontage implements Describable {
    ROUND("Круглый"),
    RECTANGLE("Прямоугольный"),
    ROUND_AND_RECTANGLE("Круг & Прямог."),
    ROOF("Крышный");
    private final String description;

    TypeMontage(String desc) {
        this.description = desc;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    public static TypeMontage getByDescription(String description) {
        for (TypeMontage desc : TypeMontage.values()) {
            if (desc.getDescription().equals(description)) {
                return desc;
            }
        }
        throw new IllegalArgumentException("Тип монтажа не соответствует доступным значениям!");
    }

    @Override
    public String toString() {
        return this.description;
    }
}
