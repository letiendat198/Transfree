package com.transfree.ui;

import com.transfree.client.ClientThread;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class SendView extends VBox {
    private static final Logger logger = LogManager.getLogger("SENDVIEW");

    String targetIP;
    Label defaultLabel;
    List<FileBox> fileBoxList = new ArrayList<>();
    List<File> fileList = new ArrayList<>();
    public SendView(){
        this.defaultLabel = new Label("Select a target to send file!");
        this.getChildren().add(defaultLabel);
    }

    public void populate(String targetIP){
        this.targetIP = targetIP;
        this.getChildren().clear();
        this.fileBoxList.clear();
        this.fileList.clear();

        HBox buttonRow = new HBox();

        Button fileChooseButton = new Button("Choose a file");
        fileChooseButton.setOnAction(this::onFileChooseButton);

        Button sendButton = new Button("Send");
        sendButton.setOnAction(this::onSendButton);

        buttonRow.getChildren().add(fileChooseButton);
        buttonRow.getChildren().add(sendButton);
        this.getChildren().add(buttonRow);
    }

    private void onFileChooseButton(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        List<File> fileChoosedList = fileChooser.showOpenMultipleDialog((Stage)((Node)event.getSource()).getScene().getWindow());

        if (fileChoosedList == null) return;
        for (File fileObj: fileChoosedList ){
            logger.debug(fileObj.toString());
            this.fileList.add(fileObj);

            FileBox fileBox = new FileBox(fileObj);
            fileBox.setStatus("WAITING...");
            this.fileBoxList.add(fileBox);
            this.getChildren().add(fileBox);
        }
    }

    private void onSendButton(ActionEvent event){
        logger.debug("Send button clicked!");
        ClientThread clientThread = new ClientThread(this.targetIP, 8000);
        clientThread.addFiles(this.fileList);
        clientThread.addCallback(this::onFileSent);
        Thread sendThread = new Thread(clientThread);
        sendThread.start();
    }

    private void onFileSent(File sentFile){
        logger.info("File {} has been sent", sentFile.getName());
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Iterator<FileBox> iterator = fileBoxList.iterator();
                while (iterator.hasNext()){
                    FileBox fileBox = iterator.next();
                    if (fileBox.file == sentFile){
                        fileBox.setStatus("SENT");
                        iterator.remove();
                        fileList.remove(sentFile);
                    }
                }
            }
        });
    }
}
