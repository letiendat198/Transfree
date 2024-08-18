package com.transfree.views.deprecated;

import com.transfree.views.components.FileReceiveBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

@Deprecated
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
