package com.systemair.bcastfans.demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

//sample class that extends application base class
public class JavaFXProgressBarExample extends Application {
    //application starts here
    public void start(Stage s) throws Exception {
// set title
        s.setTitle("JavaFX Progress Bar example");
//create progress bar
        ProgressBar p = new ProgressBar(0);
//set a value for progress bar
        p.setProgress(0.75);
// tile pane
        TilePane tp = new TilePane();
// label
        Label l = new Label(" Showing a progress of 75% !!!!");
        tp.getChildren().add(p);
        tp.getChildren().add(l);
        Scene sc = new Scene(tp, 200, 200);
        s.setScene(sc);
        s.show();
    }

    public static void main(String[] args){
        launch(args);
    }
}