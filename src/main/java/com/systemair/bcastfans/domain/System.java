package com.systemair.bcastfans.domain;

public class System {
    private String name;
    private Integer airFlow;
    private Integer airDrop;
    private TypeMontage typeMontage;
    private SubType subType;

    public System(String name, Integer airFlow, Integer airDrop, TypeMontage typeMontage, SubType subType) {
        this.name = name;
        this.airFlow = airFlow;
        this.airDrop = airDrop;
        this.typeMontage = typeMontage;
        this.subType = subType;
    }
}
