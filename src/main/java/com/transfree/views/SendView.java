package com.transfree.views;

import com.transfree.views.components.Device;
import com.transfree.views.components.DeviceBox;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SendView extends VBox {
    Device device;

    public SendView(Device device){
        this.device = device;

        HBox header = new HBox();
        Button backButton = new Button("Back");
        DeviceBox currentDevice = new DeviceBox(device);
        header.getChildren().addAll(backButton, currentDevice);

        this.getChildren().addAll(header);
    }
}
