package com.systemair.bcastfans.domain;

import com.systemair.bcastfans.myInterface.Describable;

public class RoundDimension {

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
