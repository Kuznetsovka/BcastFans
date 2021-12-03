module com.systemair.bcastfans {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires lombok;
    requires org.seleniumhq.selenium.api;
    requires org.seleniumhq.selenium.chrome_driver;
    requires org.jetbrains.annotations;
    requires org.seleniumhq.selenium.http;
    requires org.seleniumhq.selenium.support;
    requires poi;
    requires poi.ooxml;

    opens com.systemair.bcastfans to javafx.fxml;
    exports com.systemair.bcastfans;
    exports com.systemair.bcastfans.domain;
    opens com.systemair.bcastfans.domain to javafx.fxml;
    exports com.systemair.bcastfans.controller;
    opens com.systemair.bcastfans.controller to javafx.fxml;
    exports com.systemair.bcastfans.intarface;
    opens com.systemair.bcastfans.intarface to javafx.fxml;

    exports com.systemair.bcastfans.demo;
    opens com.systemair.bcastfans.demo to javafx.fxml;
}