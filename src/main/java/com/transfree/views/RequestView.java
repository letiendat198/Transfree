package com.transfree.views;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class RequestView extends VBox {
    Consumer<Boolean> callback;
    Label label = new Label();
    public RequestView(Consumer<Boolean> callback){
        this.callback = callback;

        label.setText("A device is requesting permission to transfer file to this device");
        HBox buttonRow = new HBox();
        Button acceptButton = new Button("Accept");
        acceptButton.setOnAction(this::onAccept);
        Button refuseButton = new Button("Refuse");
        refuseButton.setOnAction(this::onRefuse);

        buttonRow.getChildren().add(acceptButton);
        buttonRow.getChildren().add(refuseButton);
        buttonRow.setAlignment(Pos.CENTER);
        buttonRow.setSpacing(10);
        this.getChildren().addAll(label, buttonRow);
        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(5,5,5,5));
    }

    private void onAccept(ActionEvent event){
        this.callback.accept(true);
    }

    private void onRefuse(ActionEvent event){
        this.callback.accept(false);
    }

    public void updateLabel(String deviceName){
        this.label.setText(deviceName + " is requesting permission to transfer file to this device");
    }
}