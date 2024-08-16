package com.transfree;

import atlantafx.base.theme.PrimerLight;
import com.transfree.server.ServerGUIInterface;
import com.transfree.service_discovery.ServiceDiscovery;
import com.transfree.ui.RequestPopup;
import com.transfree.ui.views.DeviceView;
import com.transfree.ui.views.SendView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import com.transfree.server.Server;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ApplicationMain extends Application{
    private static final Logger logger = LogManager.getLogger("MAIN");
    @Override
    public void start(Stage stage){
        setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());

        Server server = new Server();
        server.setGuiInterface(new GUIInterface());
        Thread serverThread = new Thread(server);
        serverThread.start();

//        ServiceDiscovery serviceDiscovery = new ServiceDiscovery();
//        serviceDiscovery.registerService("TransfreeWindows", server.getPort());

        stage.setTitle("Transfree");

        DeviceView deviceView = new DeviceView();
        deviceView.getStyleClass().add("devices-view");
        SendView sendView = new SendView();
        sendView.getStyleClass().add("send-view");
//        ReceiveView recvView = new ReceiveView();
//        recvView.getStyleClass().add("recv-view");

        deviceView.addSendControl(sendView);
        deviceView.addTarget("Self", "Windows", "localhost", server.getPort());

        GridPane view = new GridPane();
        view.getStyleClass().add("view");
        for (int i=0; i<2; i++){
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(60);
            if (i>0) {
                cc.setPercentWidth(40);
            }
            cc.setHalignment(HPos.CENTER);
            view.getColumnConstraints().add(cc);
        }
        RowConstraints rc = new RowConstraints();
        rc.setPercentHeight(100);
        rc.setValignment(VPos.CENTER);
        view.getRowConstraints().add(rc);

        view.add(deviceView,0,0);
        view.add(sendView,1 ,0);
//        view.add(recvView, 2, 0);
        Scene scene = new Scene(view, 550, 400);
        scene.getStylesheets().add(ApplicationMain.class.getResource("/MainView.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    private class GUIInterface implements ServerGUIInterface {
        private static final Logger logger = LogManager.getLogger("SERVER-GUI INTERFACE");
        private RequestPopup popup = new RequestPopup(this::onConfirmCallback);
        boolean isConfirmed = false;
        boolean isAccepted = false;

        @Override
        public boolean requestConfirmation(String deviceName) {
            logger.debug("Requesting User Confirmation");
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    popup.show(deviceName);
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
            return 0;
        }

        @Override
        public void updateProgress(Integer id, Double progress) {

        }

        public void onConfirmCallback(Boolean isAccepted){
            logger.debug("Request confirm result: {}", isAccepted);
            this.isAccepted = isAccepted;
            this.isConfirmed = true;
        }
    }

    public static void run() {
        launch();
    }
}
