package com.transfree.ui;

import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.function.Consumer;

public class RequestPopup extends Stage {
    Consumer<Boolean> callback;
    public RequestPopup(String name, Consumer<Boolean> callback){
        this.callback = callback;

        this.setTitle("File transfer request");

        VBox view = new VBox();
        Label l = new Label(name + " is requesting permission to transfer file to this device");
        HBox buttonRow = new HBox();
        Button acceptButton = new Button("Accept");
        acceptButton.setOnAction(this::onAccept);
        Button refuseButton = new Button("Refuse");
        refuseButton.setOnAction(this::onRefuse);

        buttonRow.getChildren().add(acceptButton);
        buttonRow.getChildren().add(refuseButton);
        buttonRow.setAlignment(Pos.CENTER);
        view.getChildren().add(l);
        view.getChildren().add(buttonRow);
        view.setAlignment(Pos.TOP_CENTER);

            Scene scene = new Scene(view, 450, 65);
        this.setScene(scene);
        this.show();
    }

    private void onAccept(ActionEvent event){
        this.callback.accept(true);
        this.close();
    }

    private void onRefuse(ActionEvent event){
        this.callback.accept(false);
        this.close();
    }
}
