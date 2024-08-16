package com.transfree.ui.components;

import com.transfree.ui.views.SendView;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;

public class TargetBox extends GridPane {
    private static final Logger logger = LogManager.getLogger("TARGETBOX");

    String targetName;
    String targetOS;
    String targetIP;
    String targetMAC;
    int targetPort;
    SendView sendView;
    public TargetBox(String name, String os, String ip, int port){
        this.targetName = name;
        this.targetOS = os;
        this.targetIP = ip;
        this.targetPort = port;
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

        if (os.equals("Windows")){
            InputStream imageStream = getClass().getResourceAsStream("/icons/windows.png");
            Image image = new Image(imageStream);
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(50);
            imageView.setFitWidth(50);
            imageView.setPreserveRatio(true);
            this.add(imageView,0,0, 1, 3);
        }

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
        this.sendView.populate(this.targetIP, this.targetPort);
    }
}
