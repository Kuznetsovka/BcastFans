module com.systemair.bcastfans {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires lombok;
    requires gembox.spreadsheet;

    opens com.systemair.bcastfans to javafx.fxml;
    exports com.systemair.bcastfans;
    exports com.systemair.bcastfans.domain;
    opens com.systemair.bcastfans.domain to javafx.fxml;
    exports com.systemair.bcastfans.controller;
    opens com.systemair.bcastfans.controller to javafx.fxml;
}