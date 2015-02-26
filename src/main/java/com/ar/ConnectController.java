package com.ar;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;


/**
 * Created by ar on 2/19/15.
 */
public class ConnectController implements Initializable {

    @FXML
    private Button cancel;
    @FXML
    private Button connect;

    @FXML
    private Label error;

    @FXML
    private TextField toHost;
    @FXML
    private TextField toPort;
    @FXML
    private TextField toUser;
    @FXML
    private PasswordField toPass;

    @FXML
    private TextField fromHost;
    @FXML
    private TextField fromPort;
    @FXML
    private TextField fromUser;
    @FXML
    private PasswordField fromPass;


    @FXML
    private void connectButtonAction(ActionEvent event) {

        Connector connector = new Connector(getPropsFrom() , getPropsTo());

        if (!connector.open()){
            error.setText("Can not connect to servers");
        } else {
            error.setText("");
            try {
                goToMainScene();
            } catch (IOException e) {
                e.printStackTrace();
                //todo show error message
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setDefaults();
    }

    @FXML
    private void cancelButtonAction(ActionEvent event) {
        // get a handle to the stage
        Stage stage = (Stage) cancel.getScene().getWindow();
        // do what you have to do
        stage.close();
    }

    public void setDefaults(){
        toHost.setText(GetPropertyValues.getProperty(toHost.getId()));
        toPort.setText(GetPropertyValues.getProperty(toPort.getId()));
        toUser.setText(GetPropertyValues.getProperty(toUser.getId()));
        toPass.setText(GetPropertyValues.getProperty(toPass.getId()));

        fromHost.setText(GetPropertyValues.getProperty(fromHost.getId()));
        fromPort.setText(GetPropertyValues.getProperty(fromPort.getId()));
        fromUser.setText(GetPropertyValues.getProperty(fromUser.getId()));
        fromPass.setText(GetPropertyValues.getProperty(fromPass.getId()));
    }


    public Properties getPropsTo(){
        Properties propsFrom = new Properties();
        propsFrom.setProperty("host", toHost.getText());
        propsFrom.setProperty("port", toPort.getText());
        propsFrom.setProperty("user", toUser.getText());
        propsFrom.setProperty("pass", toPass.getText());
        return propsFrom;
    }

    public Properties getPropsFrom(){
        Properties propsTo = new Properties();
        propsTo.setProperty("host", fromHost.getText());
        propsTo.setProperty("port", fromPort.getText());
        propsTo.setProperty("user", fromUser.getText());
        propsTo.setProperty("pass", fromPass.getText());
        return propsTo;
    }

    private void goToMainScene() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        Scene mainScene = new Scene((Pane) loader.load());
        App.main.setScene(mainScene);
        MainController main = loader.getController();
        App.main.show();
    }
}
