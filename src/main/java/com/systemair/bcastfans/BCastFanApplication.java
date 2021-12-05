package com.systemair.bcastfans;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class BCastFanApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
            FXMLLoader fxmlLoader = new FXMLLoader(BCastFanApplication.class.getResource("main-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Считать");
            stage.setScene(scene);
            stage.show();
    }

    @Override
    public void stop() throws Exception {
        SingletonBrowserClass sbc = SingletonBrowserClass.getInstanceOfSingletonBrowserClass();
        System.out.println("Закрытие сессии!");
        sbc.getDriver().quit();
        super.stop();
    }

    public static void main(String[] args) {
        launch();
    }
}