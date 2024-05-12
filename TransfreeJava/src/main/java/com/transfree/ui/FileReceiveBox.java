package com.transfree.ui;

import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.io.File;

public class FileReceiveBox extends GridPane {
    private ProgressBar progressBar = new ProgressBar();
    private Label progressPercentage = new Label("0%");
    public FileReceiveBox(String name, long size){
        String fileName = name;
        long fileSize = size;

        int dotIndex = name.lastIndexOf(".");
        String fileExt = "";
        if (dotIndex > 0){
            fileExt = name.substring(dotIndex+1);
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

        HBox progressRow = new HBox();
        progressRow.getChildren().add(this.progressBar);
        progressRow.getChildren().add(this.progressPercentage);
        this.add(progressRow, 1, 2);
    }

    public void setProgress(double prog){
        this.progressBar.setProgress(prog);
        this.progressPercentage.setText(Long.toString(Math.round(prog*100)) + "%");
    }
}
