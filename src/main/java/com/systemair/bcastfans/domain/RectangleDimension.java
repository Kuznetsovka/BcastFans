package com.systemair.bcastfans.domain;

import com.systemair.bcastfans.intarface.Describable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RectangleDimension extends Dimension<RectangleDimension> {
    public static List<RectangleDimensionType> values = List.of(RectangleDimension.RectangleDimensionType.values());
    public static List<String> stringValues = Arrays.stream(RectangleDimensionType.values()).map(RectangleDimension.RectangleDimensionType::getDescription).collect(Collectors.toList());
    public RectangleDimension(String dimension) {
        super(dimension,TypeMontage.RECTANGLE);
    }
    public enum RectangleDimensionType implements Describable {
        NONE(""),
        RECT_40_20("40-20"),
        RECT_50_25("50-25"),
        RECT_50_30("50-30"),
        RECT_60_30("60-30"),
        RECT_60_35("60-35"),
        RECT_70_40("70-40"),
        RECT_80_50("80-50"),
        RECT_100_50("100-50");
        private final String description;

        RectangleDimensionType(String desc) {
            this.description = desc;
        }

        @Override
        public String getDescription() {
            return this.description;
        }

        static RectangleDimensionType getByDescription(String description) {
            for (RectangleDimensionType desc : RectangleDimensionType.values()) {
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
}
