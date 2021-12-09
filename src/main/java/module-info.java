module com.systemair.bcastfans {
    requires javafx.fxml;
    requires javafx.controls;
    requires org.seleniumhq.selenium.chrome_driver;
    requires org.seleniumhq.selenium.support;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires log4j;

    opens com.systemair.bcastfans to javafx.fxml;
    exports com.systemair.bcastfans;
    exports com.systemair.bcastfans.domain;
    opens com.systemair.bcastfans.domain to javafx.fxml;
    exports com.systemair.bcastfans.controller;
    opens com.systemair.bcastfans.controller to javafx.fxml;
    exports com.systemair.bcastfans.intarface;
    opens com.systemair.bcastfans.intarface to javafx.fxml;
}