package com.connectify;

import com.connectify.Interfaces.ConnectedUser;
import com.connectify.Interfaces.ServerAPI;
import com.connectify.controller.ServerController;
import com.connectify.loaders.ViewLoader;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.util.HashMap;
import java.util.Map;

public class Server extends Application {

    private static Map<String, ConnectedUser> connectedUsers;

    @Override
    public void init() throws Exception {
        super.init();
        System.out.println("Server is running...");
        var registry = LocateRegistry.createRegistry(1099);
        ServerAPI server = new ServerController();
        registry.rebind("server", server);
        connectedUsers = new HashMap<>();
    }

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = ViewLoader.getInstance().getMainPane();
        Scene scene = new Scene(root);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setMinHeight(750);
        stage.setMinWidth(1300);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }


    public static Map<String, ConnectedUser> getConnectedUsers(){
        return connectedUsers;
    }
}