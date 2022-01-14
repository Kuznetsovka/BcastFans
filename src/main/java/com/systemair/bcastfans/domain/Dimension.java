package com.systemair.bcastfans.domain;

import java.util.List;

public class Dimension <T> {
    public List<T> values;
    private String dimension;
    private TypeMontage type;

    public Dimension(String dimension, TypeMontage type) {
        this.dimension = dimension;
        this.type = type;
        if (type.equals(TypeMontage.ROUND)) {
            values = List.of((T) RoundDimension.RoundDimensionType.values());
        } else {
            values = List.of((T) RectangleDimension.RectangleDimensionType.values());
        }
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public TypeMontage getType() {
        return type;
    }

    public void setType(TypeMontage type) {
        this.type = type;
    }
}
