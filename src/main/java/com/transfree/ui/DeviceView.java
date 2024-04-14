package com.transfree.ui;

import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;


public class DeviceView extends VBox {
    List<TargetBox> targetList= new ArrayList<>();
    SendView sendView;

    public void addSendControl(SendView sendInstance){
        this.sendView = sendInstance;
    }

    public void addTarget(){
        TargetBox target = new TargetBox("T Dat", "Windows", "localhost");
        target.addSendControl(this.sendView);
        targetList.add(target);
        this.getChildren().add(target);
    }


}
