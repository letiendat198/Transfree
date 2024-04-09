package com.transfree;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;

import com.transfree.server.Server;

public class ApplicationMain extends Application{

    @Override
    public void start(Stage stage){
        stage.setTitle("Transfree");

        Label l = new Label("No device found");
        Label sendDefault = new Label("No file sent");
        Label recvDefault = new Label("No file received");

        VBox devicesView = new VBox();
        devicesView.getStyleClass().add("devices-view");
        VBox sendView = new VBox();
        sendView.getStyleClass().add("send-view");
        VBox recvView = new VBox();
        recvView.getStyleClass().add("recv-view");

        devicesView.getChildren().add(l);
        sendView.getChildren().add(sendDefault);
        recvView.getChildren().add(recvDefault);

        GridPane view = new GridPane();
        view.getStyleClass().add("view");
        for (int i=0; i<3; i++){
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(20);
            if (i>0) {
                cc.setPercentWidth(40);
            }
            cc.setHalignment(HPos.CENTER);
            view.getColumnConstraints().add(cc);
        }
        RowConstraints rc = new RowConstraints();
        rc.setPercentHeight(100);
        rc.setValignment(VPos.CENTER);
        view.getRowConstraints().add(rc);

        view.add(devicesView,0,0);
        view.add(sendView,1 ,0);
        view.add(recvView, 2, 0);
        Scene scene = new Scene(view, 640, 480);
        scene.getStylesheets().add(ApplicationMain.class.getResource("/MainView.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    public static void run() {
        Server sv = new Server(8000);
        Thread serverThread = new Thread(sv);
        serverThread.start();
        launch();
    }
}
