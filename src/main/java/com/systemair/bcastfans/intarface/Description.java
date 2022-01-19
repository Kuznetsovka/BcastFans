package com.systemair.bcastfans.intarface;


public class Description implements Describable {
    protected String description;

    Description(String desc) {
        description = desc;
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
