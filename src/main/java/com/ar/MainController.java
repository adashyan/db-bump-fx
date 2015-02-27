package com.ar;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;


/**
 * Created by ar on 2/19/15.
 */
public class MainController implements Initializable {

    private Connector conn = null;

    @FXML
    private ComboBox from;
    @FXML
    private ComboBox to;


    @FXML
    private CheckBox dbbackup;

    @FXML
    private Button cancel;

    @FXML
    private TextArea querys;

    @Override
    public void initialize(URL location, ResourceBundle resources) { }

    @FXML
    private void cancelButtonAction(ActionEvent event) {
        // get a handle to the stage
        Stage stage = (Stage) cancel.getScene().getWindow();
        // do what you have to do
        stage.close();
    }

    @FXML
    private void runButtonAction(ActionEvent event) {
        Database database = new Database(conn);

        database.execute(from.getValue().toString(), to.getValue().toString(), dbbackup.isSelected(), querys.getText());
    }

    public void setDefaults(){
        if("checked".equals(GetPropertyValues.getProperty("dbbackup"))){
            dbbackup.setSelected(true);
        }
        addDbListToElement(to);
        addDbListToElement(from);
    }

    public void setConn(Connector conn) {
        this.conn = conn;
    }

    private void addDbListToElement(ComboBox comboBox) {

        Connection connection = null;

        switch (comboBox.getId()) {
            case "from":
                connection = conn.connFrom;
                break;
            case "to":
                connection = conn.connTo;
                break;
        }

        try {
            ResultSet rs = connection.getMetaData().getCatalogs();
            while (rs.next()) {
                if("information_schema".equals(rs.getString("TABLE_CAT")))
                    continue;
                if("mysql".equals(rs.getString("TABLE_CAT")))
                    continue;
                if("performance_schema".equals(rs.getString("TABLE_CAT")))
                    continue;
                comboBox.getItems().addAll(rs.getString("TABLE_CAT"));
            }
            comboBox.getSelectionModel().select(0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
