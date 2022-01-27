package com.systemair.bcastfans.domain;

import com.systemair.bcastfans.intarface.Describable;

public enum SubType implements Describable {
    NONE(""),
    KITCHEN("Кухонный"),
    SILENT("Шумоизолированный"),
    EC("EC"),
    ON_ROOF("На крыше"),
    SMOKE_EXTRACT("Дымоудаление"),
    SILENT_AND_EC("Шумоизолированный & EC"),
    KITCHEN_AND_EC("Кухонный & EC");
    private final String description;

    SubType(String desc) {
        this.description = desc;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    static SubType getByDescription(String description) {
        for (SubType desc : SubType.values()) {
            if (desc.getDescription().equals(description)) {
                return desc;
            }
        }
        return NONE;
    }

    @Override
    public String toString() {
        return this.description;
    }
}
