package com.systemair.bcastfans.domain;

public class Fan {
    private String model = "";
    private String article = "";
    private Double power = 0.0;
    private String phase = "";
    private Double price = 0.0;
    private String fullLink = "";
    private String shortLink = "";

    public Fan(String model, String article, Double power, String phase, Double price, String fullLink, String shortLink) {
        this.model = model;
        this.article = article;
        this.power = power;
        this.phase = phase;
        this.price = price;
        this.fullLink = fullLink;
        this.shortLink = shortLink;
    }

    public Fan() {}

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public Double getPower() {
        return power;
    }

    public void setPower(Double power) {
        this.power = power;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getFullLink() {
        return fullLink;
    }

    public void setFullLink(String fullLink) {
        this.fullLink = fullLink;
    }

    public String getShortLink() {
        return shortLink;
    }

    public void setShortLink(String shortLink) {
        this.shortLink = shortLink;
    }
}
