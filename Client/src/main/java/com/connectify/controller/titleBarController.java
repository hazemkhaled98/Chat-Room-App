package com.connectify.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;



public class titleBarController implements Initializable{

    @FXML
    private ImageView closeButton;

    @FXML
    private ImageView minimizeButton;

    @FXML
    private HBox titleBarHBox;

    private static double xOffset = 0;
    private static double yOffset = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        titleBarHBox.setOnMousePressed(event -> {
//            xOffset = event.getSceneX();
//            yOffset = event.getSceneY();
//        });
//        titleBarHBox.setOnMouseDragged(event -> {
//            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
//            stage.setX(event.getScreenX() - xOffset);
//            stage.setY(event.getScreenY() - yOffset);
//        });
    }

    @FXML
    void closeButtonHandler(MouseEvent event) {
//        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to exit?", ButtonType.YES, ButtonType.NO);
//        alert.setHeaderText("Exit ?");
//        alert.showAndWait();
//        if (alert.getResult() == ButtonType.YES) {
            System.exit(0);
//        }
    }

    @FXML
    void minimizeButtonHandler(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

}
