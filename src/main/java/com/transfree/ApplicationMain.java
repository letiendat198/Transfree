package com.transfree;

import com.transfree.client.Client;
import com.transfree.ui.DeviceView;
import com.transfree.ui.SendView;
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

        DeviceView deviceView = new DeviceView();
        deviceView.getStyleClass().add("devices-view");
        SendView sendView = new SendView();
        sendView.getStyleClass().add("send-view");
        VBox recvView = new VBox();
        recvView.getStyleClass().add("recv-view");
        recvView.getChildren().add(recvDefault);

        deviceView.addSendControl(sendView);
        deviceView.addTarget();

        GridPane view = new GridPane();
        view.getStyleClass().add("view");
        for (int i=0; i<3; i++){
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(30);
            if (i>0) {
                cc.setPercentWidth(35);
            }
            cc.setHalignment(HPos.CENTER);
            view.getColumnConstraints().add(cc);
        }
        RowConstraints rc = new RowConstraints();
        rc.setPercentHeight(100);
        rc.setValignment(VPos.CENTER);
        view.getRowConstraints().add(rc);

        view.add(deviceView,0,0);
        view.add(sendView,1 ,0);
        view.add(recvView, 2, 0);
        Scene scene = new Scene(view, 920, 680);
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
