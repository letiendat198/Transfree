package com.transfree.ui;

import javafx.scene.layout.VBox;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ReceiveView extends VBox {
    List<FileReceiveBox> fileReceiveBoxList = new ArrayList<>();

    public void onFileReceive(String name, long size){
        FileReceiveBox fileBox = new FileReceiveBox(name, size);
        this.fileReceiveBoxList.add(fileBox);
        this.getChildren().add(fileBox);
    }
    public void onFileProgress(double prog){
        FileReceiveBox fileBox = fileReceiveBoxList.get(fileReceiveBoxList.size()-1);
        fileBox.setProgress(prog);
    }
}
