package com.transfree.ui.views;

import com.transfree.ui.components.TargetBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;


public class DeviceView extends VBox {
    List<TargetBox> targetList= new ArrayList<>();
    SendView sendView;

    public void addSendControl(SendView sendInstance){
        this.sendView = sendInstance;
    }

    public void addTarget(String name, String os, String ip, int port){
        TargetBox target = new TargetBox(name, os, ip, port);
        target.addSendControl(this.sendView);
        targetList.add(target);
        this.getChildren().add(target);
    }


}
