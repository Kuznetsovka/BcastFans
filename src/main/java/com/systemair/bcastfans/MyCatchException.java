package com.systemair.bcastfans;

import javafx.scene.control.Alert;
import org.apache.log4j.Logger;

import static com.systemair.bcastfans.staticClasses.UtilClass.showAlert;

public class MyCatchException extends Exception {
    private static final Logger LOGGER = Logger.getLogger(MyCatchException.class.getName());
    private final Alert.AlertType errorType;

    public MyCatchException(String message, Alert.AlertType errorType) {
        super(message);
        LOGGER.error(message);
        showAlert(message, errorType);
        this.errorType = errorType;
    }

    public Alert.AlertType getErrorType() {
        return this.errorType;
    }
}
