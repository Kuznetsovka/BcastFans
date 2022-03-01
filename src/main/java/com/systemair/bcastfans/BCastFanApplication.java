package com.systemair.bcastfans;

import com.systemair.bcastfans.controller.TableController;
import com.systemair.bcastfans.staticClasses.SingletonBrowserClass;
import com.systemair.bcastfans.staticClasses.UtilClass;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.io.IOException;

public class BCastFanApplication extends Application {
    private static final Logger LOGGER = Logger.getLogger(BCastFanApplication.class.getName());

    @Override
    public void start(Stage stage) throws IOException {
        JFrame loadingWindow = new LoadingWindow().getInitWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(BCastFanApplication.class.getResource("main-view.fxml"));
        UtilClass.initProperties();
        Scene scene = new Scene(fxmlLoader.load());
        stage.getIcons().add(new Image("/logo.ico"));
        stage.setTitle("Подбор вентиляторов");
        stage.setMinWidth(1100.0);
        stage.setMinHeight(530.0);
        stage.setScene(scene);
        stage.show();
        loadingWindow.dispose();
    }

    @Override
    public void stop() throws Exception {
        SingletonBrowserClass sbc = SingletonBrowserClass.getInstanceOfSingletonBrowserClass();
        LOGGER.info("Закрытие сессии!");
        sbc.getDriver().quit();
        super.stop();
    }

    public static void main(String[] args) {
        launch();
    }
}