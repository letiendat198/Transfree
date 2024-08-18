package com.transfree.gui_interface;

import com.transfree.NotificationPopup;
import com.transfree.views.deprecated.ReceiveView;
import com.transfree.views.deprecated.RequestView;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerGUIBridge implements ServerGUIInterface {
    private static final Logger logger = LogManager.getLogger("SERVER-GUI INTERFACE");

    private RequestView requestView = new RequestView(this::onConfirmCallback);
    private ReceiveView receiveView = new ReceiveView();
    private NotificationPopup popup = new NotificationPopup(requestView, receiveView);

    boolean isConfirmed = false;
    boolean isAccepted = false;

    @Override
    public boolean requestConfirmation(String deviceName) {
        logger.debug("Requesting User Confirmation");
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                requestView.updateLabel(deviceName);
                popup.show();
                popup.switchToRequest();
                popup.setPosBottomRight();
            }
        });
        this.isConfirmed = false;
        int waitTime = 10;
        int count = 0;
        logger.debug("Confirm: {}, Accept: {}", isConfirmed, isAccepted);
        while (!this.isConfirmed){
            count++;
            try{
                Thread.sleep(1000);
            }
            catch (Exception e){
                logger.error(e);
            }
            if (count==waitTime) {
                logger.info("Confirmation expired");
                return false;
            }
        }
        return this.isAccepted;
    }

    @Override
    public Integer receiveFile(String fileName, Long fileSize) {
        Platform.runLater(() -> {
            receiveView.onFileReceive(fileName, fileSize);
        });
        return 0;
    }

    @Override
    public void updateProgress(Integer id, Double progress) {
        Platform.runLater(() -> {
            receiveView.onFileProgress(progress);
        });
    }

    public void onConfirmCallback(Boolean isAccepted){
        logger.debug("Request confirm result: {}", isAccepted);
        this.isAccepted = isAccepted;
        this.isConfirmed = true;
        Platform.runLater(() -> {
            popup.switchToReceive();
        });

    }
}
