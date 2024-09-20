package com.transfree.views;

import com.transfree.client.ClientThread;
import com.transfree.utils.FileStatus;
import com.transfree.views.components.Device;
import com.transfree.views.components.DeviceBox;
import com.transfree.views.components.FileSendBox;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class SendView extends VBox {
    private static final Logger logger = LogManager.getLogger("SEND_VIEW");

    Device device;
    Runnable returnToHome;

    List<FileSendBox> fileBoxList = new ArrayList<>();
    List<File> fileList = new ArrayList<>();

    VBox fileListView = new VBox();

    public SendView(Device device, Runnable returnToHome){
        this.device = device;
        this.returnToHome = returnToHome;

        HBox header = new HBox();
        Button backButton = new Button("Back");
        Region stretch = new Region();
        HBox.setHgrow(stretch, Priority.ALWAYS);
        DeviceBox currentDevice = new DeviceBox(device);
        header.getChildren().addAll(currentDevice, stretch, backButton);

        backButton.setOnAction(event -> this.returnToHome.run());

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(fileListView);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        HBox footer = new HBox();
        Button addButton = new Button("Add");
        Button sendButton = new Button("Send");
        footer.getChildren().addAll(addButton, sendButton);
        footer.setSpacing(10);

        addButton.setOnAction(this::onFileChooseButton);
        sendButton.setOnAction(this::onSendButton);

        this.getChildren().addAll(header, scrollPane, footer);
        Insets margin = new Insets(5,5,5,5);
        VBox.setMargin(header, margin);
        VBox.setMargin(scrollPane, margin);
        VBox.setMargin(footer, margin);
    }

    private void onFileChooseButton(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        List<File> fileChoosedList = fileChooser.showOpenMultipleDialog((Stage)((Node)event.getSource()).getScene().getWindow());

        if (fileChoosedList == null) return;
        for (File fileObj: fileChoosedList ){
            logger.debug(fileObj.toString());
            this.fileList.add(fileObj);

            FileSendBox fileBox = new FileSendBox(fileObj);
            fileBox.setStatus("WAITING...");
            this.fileBoxList.add(fileBox);
            this.fileListView.getChildren().add(fileBox);
        }
    }

    private void onSendButton(ActionEvent event){
        logger.debug("Send button clicked!");
        ClientThread clientThread = new ClientThread(device.getIp(), device.getPort());
        clientThread.addFiles(this.fileList);
        clientThread.addCallback(this::onFileUpdate);
        Thread sendThread = new Thread(clientThread);
        sendThread.start();
    }

    private void onFileUpdate(File sentFile, FileStatus.STATUS status){
        logger.info("File {} status: {}", sentFile.getName(), status);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Iterator<FileSendBox> iterator = fileBoxList.iterator();
                while (iterator.hasNext()){
                    FileSendBox fileBox = iterator.next();
                    if (fileBox.file == sentFile){
                        if (status == FileStatus.STATUS.SENT){
                            fileBox.setStatus("SENT");
                            iterator.remove();
                            fileList.remove(sentFile);
                        }
                        else if (status == FileStatus.STATUS.FAILED){
                            fileBox.setStatus("FAILED");
                            iterator.remove();
                            fileList.remove(sentFile);
                        }
                        else if (status == FileStatus.STATUS.START){
                            fileBox.setStatus("SENDING...");
                        }
                    }
                }
            }
        });
    }
}
