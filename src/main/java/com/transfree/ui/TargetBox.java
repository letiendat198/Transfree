package com.transfree.ui;

import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Function;

public class TargetBox extends GridPane {
    private static final Logger logger = LogManager.getLogger("TARGETBOX");

    String targetName;
    String targetOS;
    String targetIP;
    String targetMAC;
    SendView sendView;
    public TargetBox(String name, String os, String ip){
        this.targetName = name;
        this.targetOS = os;
        this.targetIP = ip;
        this.targetMAC = "50:50:50:50";

        for (int i=0; i<2; i++){
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(20);
            if (i>0) {
                cc.setPercentWidth(80);
            }
            cc.setHalignment(HPos.LEFT);
            this.getColumnConstraints().add(cc);
        }

        Label osLabel = new Label(this.targetOS);
        this.add(osLabel,0,0, 1, 3);

        Label deviceLabel = new Label("Device name: " + this.targetName);
        this.add(deviceLabel, 1,0);

        Label ipLabel = new Label("IP address: " + this.targetIP);
        this.add(ipLabel, 1, 1);

        Label statusLabel = new Label("Status:");
        this.add(statusLabel, 1,2);

        this.setOnMouseClicked(this::onClick);
    }

    public void addSendControl(SendView sendInstance){
        this.sendView = sendInstance;
    }

    private void onClick(MouseEvent event){
        logger.debug("Target box clicked with IP: {}", this.targetIP);
        this.sendView.populate(this.targetIP);
    }
}
