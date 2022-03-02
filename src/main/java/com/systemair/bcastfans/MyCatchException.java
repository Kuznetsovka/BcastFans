package com.systemair.bcastfans;

import javafx.scene.control.Alert;

import static com.systemair.bcastfans.staticClasses.UtilClass.showAlert;

public class MyCatchException extends Exception {
    private Alert.AlertType errorType;

    public MyCatchException(String message, Alert.AlertType errorType){
        super(message);
        showAlert(message,errorType);
        this.errorType = errorType;
    }

    public Alert.AlertType getErrorType(){
        return this.errorType;
    }
}
