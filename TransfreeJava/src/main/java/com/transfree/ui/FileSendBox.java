package com.transfree.ui;

import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

import java.io.File;

public class FileSendBox extends GridPane {
    public File file;
    private Label status = new Label("Status: ");
    public FileSendBox(File file){
        this.file = file;
        String fileName = file.getName();
        long fileSize = file.length();

        int dotIndex = file.getName().lastIndexOf(".");
        String fileExt = "";
        if (dotIndex > 0){
            fileExt = file.getName().substring(dotIndex+1);
        }

        for (int i=0; i<2; i++){
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(20);
            if (i>0) {
                cc.setPercentWidth(80);
            }
            cc.setHalignment(HPos.LEFT);
            this.getColumnConstraints().add(cc);
        }

        Label osLabel = new Label(fileExt);
        this.add(osLabel,0,0, 1, 3);

        Label deviceLabel = new Label("File name: " + fileName);
        this.add(deviceLabel, 1,0);

        Label ipLabel = new Label("Size: " + (double)Math.round((double)fileSize / (1024d*1024d) * 100d) / 100d + "MB");
        this.add(ipLabel, 1, 1);

        this.add(this.status, 1, 2);
    }

    public void setStatus(String stat){
        this.status.setText("Status: " + stat);
    }
}
