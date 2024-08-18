package com.transfree;

import com.transfree.views.deprecated.ReceiveView;
import com.transfree.views.deprecated.RequestView;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;

public class NotificationPopup extends Stage {
    private static final Logger logger = LogManager.getLogger("RECEIVE");
    RequestView requestView;
    ReceiveView receiveView;
    Scene scene;
    public NotificationPopup(RequestView requestView, ReceiveView receiveView){
        this.requestView = requestView;
        this.receiveView = receiveView;
        this.scene = new Scene(requestView, 200, 80);
        this.setScene(scene);
    }

    public void switchToRequest(){
        scene.setRoot(requestView);
    }

    public void switchToReceive(){
        scene.setRoot(receiveView);
    }

    public void setPosBottomRight(){
        Rectangle effectiveScreenSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        logger.debug("Screen size: {} - {}", effectiveScreenSize.width, effectiveScreenSize.height);
        int height = (int) this.getHeight();
        int width = (int) this.getWidth();
        logger.debug("Window size: {} - {}", width, height);
        this.setX(effectiveScreenSize.width - width);
        this.setY(effectiveScreenSize.height - height);
    }
}
