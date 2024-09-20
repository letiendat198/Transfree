package com.transfree.views.components;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.io.InputStream;
import java.util.Arrays;

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

        String[] imageExt = {"jpg", "jpeg", "png", "gif", "svg", "bmf", "raw"};

        String imagePath = "/icons/document.png";
        if (Arrays.asList(imageExt).contains(fileExt)){
            imagePath = "/icons/image.png";
        }

        InputStream imageStream = getClass().getResourceAsStream(imagePath);
        Image image = new Image(imageStream);
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(30);
        imageView.setFitWidth(30);
        imageView.setPreserveRatio(true);
        this.add(imageView,0,0, 1, 3);

        Label fileLabel = new Label("File name: " + fileName);
        this.add(fileLabel, 1,0);

        Label sizeLabel = new Label("Size: " + (double)Math.round((double)fileSize / (1024d*1024d) * 100d) / 100d + "MB");
        this.add(sizeLabel, 1, 1);

        HBox progressRow = new HBox();
        progressRow.getChildren().add(this.progressBar);
        progressRow.getChildren().add(this.progressPercentage);
        this.add(progressRow, 1, 2);
        this.setPadding(new Insets(5,5,5,10));
    }

    public void setProgress(double prog){
        this.progressBar.setProgress(prog);
        this.progressPercentage.setText(Long.toString(Math.round(prog*100)) + "%");
    }
}
