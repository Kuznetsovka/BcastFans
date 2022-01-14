package com.systemair.bcastfans.domain;

import com.systemair.bcastfans.intarface.Describable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RoundDimension extends Dimension<RoundDimension> {
    public static List<RoundDimension.RoundDimensionType> values = List.of(RoundDimension.RoundDimensionType.values());
    public static List<String> stringValues = Arrays.stream(RoundDimensionType.values()).map(RoundDimensionType::getDescription).collect(Collectors.toList());

    public RoundDimension(String dimension) {
        super(dimension,TypeMontage.ROUND);
    }

    public enum RoundDimensionType implements Describable {
        NONE(""),
        ROUND_100("100"),
        ROUND_125("125"),
        ROUND_160("160"),
        ROUND_200("200"),
        ROUND_250("250"),
        ROUND_315("315"),
        ROUND_400("400");

        private final String description;

        RoundDimensionType(String desc) {
            this.description = desc;
        }

        static RoundDimensionType getByDescription(String description) {
            for (RoundDimensionType desc : RoundDimensionType.values()) {
                if (desc.getDescription().equals(description)) {
                    return desc;
                }
            }
            return NONE;
        }

        @Override
        public String getDescription() {
            return this.description;
        }


        @Override
        public String toString() {
            return this.description;
        }
    }

}
