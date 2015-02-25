package com.ar;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.net.URISyntaxException;

/**
 * Hello world!
 *
 */
public class App extends Application
{
    static String appRoot;

    public final static String screen1ID = "connect";
    public final static String screen1File = "connect.fxml";
    public final static String screen2ID = "main";
    public final static String screen2File = "main.fxml";

    @Override
    public void start(Stage primaryStage) throws Exception{

        ScreensController mainContainer = new ScreensController();
        mainContainer.loadScreen(App.screen1ID, App.screen1File);
        mainContainer.loadScreen(App.screen2ID, App.screen2File);

        mainContainer.setScreen(App.screen1ID);

        Group root = new Group(); //FXMLLoader.load(getClass().getResource("main.fxml"));
        root.getChildren().addAll(mainContainer);

//        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root/*, 600, 200*/));
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
}
