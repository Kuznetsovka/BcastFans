package com.systemair.bcastfans;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class BCastFanApplication extends Application {
    @Override
    public void start(Stage stage) {
            FXMLLoader fxmlLoader = new FXMLLoader(BCastFanApplication.class.getResource("main-view.fxml"));
            Scene scene = null;
            try {
                scene = new Scene(fxmlLoader.load(), 880, 500);
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.setTitle("Считать");
            stage.setScene(scene);
            stage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        SingletonBrowserClass sbc = SingletonBrowserClass.getInstanceOfSingletonBrowserClass();
        sbc.getDriver().close();

    }

    public static void main(String[] args) {
        launch();
    }
}