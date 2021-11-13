package com.systemair.bcastfans.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Fan {
    private String model;
    private String article;
    private Double power;
    private String phase;
    private Double price;
}
