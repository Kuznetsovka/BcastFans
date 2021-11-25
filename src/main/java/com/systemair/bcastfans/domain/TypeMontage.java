package com.systemair.bcastfans.domain;

import com.systemair.bcastfans.intarface.EnumInterface;

public enum TypeMontage implements EnumInterface {
    ROUND("Круглый"),RECTANGLE("Прямоугольный"),ROUND_AND_RECTANGLE("Круг & Прямог."),ROOF("Крышный");
    private final String description;

    TypeMontage(String desc){
        this.description = desc;
    }
    @Override
    public String getDescription() {
        return this.description;
    }

    static TypeMontage getByDescription(String description) {
        for (TypeMontage desc : TypeMontage.values()) {
            if (desc.getDescription().equals(description)) {
                return desc;
            }
        }
        //TODO Exception
        return null;
    }

    @Override
    public String toString() {
        return this.description;
    }
}
