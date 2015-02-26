package com.ar;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.net.URISyntaxException;

/**
 * Hello world!
 *
 */
public class App extends Application
{
    static String appRoot;
    public static Stage main = null;

    @Override
    public void start(Stage primaryStage) throws Exception{
        main = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("connect.fxml"));

        Scene scene = new Scene(root);
//        primaryStage.setTitle("Hello World");

        final DoubleProperty opacity = primaryStage.opacityProperty();
        primaryStage.setOpacity(0.0);
        primaryStage.setScene(scene);
        Timeline fadeIn = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(opacity, 0.0)),
                new KeyFrame(new Duration(1200), new KeyValue(opacity, 1.0)));
        fadeIn.play();

        primaryStage.show();
    }

    public static void main( String[] args ) throws URISyntaxException {
        setup();
        launch(args);
    }

    public static void setup() {
        try {
            File f = new File(App.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            appRoot = (new File(f.getParent())).getParent();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public static void resize(Double w, Double h){
        if(main != null){
            main.setWidth(w);
            main.setHeight(h);
        }
    }
}
