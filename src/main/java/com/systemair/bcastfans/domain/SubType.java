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
    KITCHEN_AND_EC("Кухонный & EC"),
    DIMENSION_100("⌀100"),
    DIMENSION_125("⌀125"),
    DIMENSION_160("⌀160"),
    DIMENSION_200("⌀200"),
    DIMENSION_250("⌀250"),
    DIMENSION_315("⌀315"),
    DIMENSION_40_20("40-20"),
    DIMENSION_50_25("50-25"),
    DIMENSION_60_30("60-30"),
    DIMENSION_60_35("60-35"),
    DIMENSION_70_40("70-40"),
    DIMENSION_80_50("80-50"),
    DIMENSION_100_50("100-50");
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
