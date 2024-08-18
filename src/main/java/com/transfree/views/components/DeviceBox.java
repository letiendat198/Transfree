package com.transfree.views.components;

import com.transfree.views.deprecated.SendView;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;

public class DeviceBox extends GridPane {
    private static final Logger logger = LogManager.getLogger("TARGETBOX");

    public DeviceBox(Device device) {
        for (int i = 0; i < 2; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(20);
            if (i > 0) {
                cc.setPercentWidth(80);
            }
            cc.setHalignment(HPos.LEFT);
            this.getColumnConstraints().add(cc);
        }

        if (device.getOs() != null && device.getOs().equals("Windows")) {
            InputStream imageStream = getClass().getResourceAsStream("/icons/windows.png");
            Image image = new Image(imageStream);
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(50);
            imageView.setFitWidth(50);
            imageView.setPreserveRatio(true);
            this.add(imageView, 0, 0, 1, 2);
        }

        // TextFlow causes buggy ComboBox expansion
//        TextFlow deviceLabel = new TextFlow();
//        Text device = new Text("Device Name: ");
//        device.getStyleClass().add("bold");
//        Text deviceName = new Text(this.targetName);
//        deviceLabel.getChildren().addAll(device, deviceName);
        HBox deviceLabel = new HBox();
        Label deviceText = new Label("Device Name: ");
        deviceText.getStyleClass().add("bold");
        Label deviceName = new Label(device.getName());
        deviceLabel.getChildren().addAll(deviceText, deviceName);
        this.add(deviceLabel, 1, 0);

//        TextFlow ipLabel = new TextFlow();
//        Text ipText = new Text("IP: ");
//        ipText.getStyleClass().add("bold");
//        Text deviceIp = new Text(this.targetIP);
//        ipLabel.getChildren().addAll(ipText, deviceIp);
        HBox ipLabel = new HBox();
        Label ipText = new Label("IP: ");
        ipText.getStyleClass().add("bold");
        Label deviceIp = new Label(device.ip);
        ipLabel.getChildren().addAll(ipText, deviceIp);
        this.add(ipLabel, 1, 1);
    }
}
