module com.systemair.bcastfans {
    requires javafx.fxml;
    requires javafx.controls;
    requires org.seleniumhq.selenium.chrome_driver;
    requires org.seleniumhq.selenium.support;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires log4j;
    requires java.desktop;
    requires awaitility;

    opens com.systemair.bcastfans to javafx.fxml;
    exports com.systemair.bcastfans;
    exports com.systemair.bcastfans.domain;
    opens com.systemair.bcastfans.domain to javafx.fxml;
    exports com.systemair.bcastfans.controller;
    opens com.systemair.bcastfans.controller to javafx.fxml;
    exports com.systemair.bcastfans.myInterface;
    opens com.systemair.bcastfans.myInterface to javafx.fxml;
    exports com.systemair.bcastfans.service;
    opens com.systemair.bcastfans.service to javafx.fxml;
    exports com.systemair.bcastfans.staticClasses;
    opens com.systemair.bcastfans.staticClasses to javafx.fxml;
}