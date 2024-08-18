package com.transfree;

import atlantafx.base.theme.PrimerLight;
import com.transfree.gui_interface.ServerGUIBridge;
import com.transfree.utils.HostInfo;
import com.transfree.views.SendView;
import com.transfree.views.components.Device;
import com.transfree.views.components.DeviceComboBox;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import com.transfree.server.Server;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ApplicationMain extends Application{
    private static final Logger logger = LogManager.getLogger("MAIN");

    Scene scene;

    @Override
    public void start(Stage stage){
        setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());

        Server server = new Server();
        server.setGuiInterface(new ServerGUIBridge());
        Thread serverThread = new Thread(server);
        serverThread.start();

//        ServiceDiscovery serviceDiscovery = new ServiceDiscovery();
//        serviceDiscovery.registerService("TransfreeWindows", server.getPort());

        stage.setTitle("Transfree");

        VBox view = new VBox();
        view.setAlignment(Pos.CENTER);
        view.setSpacing(10);

        VBox sendBox = new VBox();
        sendBox.setAlignment(Pos.TOP_CENTER);
        sendBox.setSpacing(10);
        Label sendLabel = new Label("Send file to another device");
        DeviceComboBox deviceComboBox = new DeviceComboBox();
        Device testDevice = new Device();
        testDevice.setIp("127.0.0.1");
        testDevice.setName("This device");
        testDevice.setOs("Windows");
        testDevice.setPort(12345);
        deviceComboBox.addDevice(testDevice);
        Button sendButton = new Button("Send");
        sendButton.setOnAction(event -> onSend(deviceComboBox.getValue()));
        sendBox.getChildren().addAll(sendLabel, deviceComboBox, sendButton);

        Label separator = new Label("- OR -");

        VBox receiveBox = new VBox();
        receiveBox.setAlignment(Pos.TOP_CENTER);
        Label connectInfo = new Label("Connect to this device at");
        receiveBox.getChildren().addAll(connectInfo);

        HostInfo.getHostIP(new HostInfo.Callback() {
            @Override
            public void call(String ip) {
                Platform.runLater(() -> {
                    TextFlow connectionDetails = new TextFlow();
                    connectionDetails.setTextAlignment(TextAlignment.CENTER);
                    Text ipText = new Text("IP: ");
                    ipText.getStyleClass().add("bold");
                    Text ipAddr = new Text(ip);
                    Text portText = new Text(" Port: ");
                    portText.getStyleClass().add("bold");
                    Text port = new Text(Integer.toString(server.getPort()));
                    connectionDetails.getChildren().addAll(ipText, ipAddr, portText, port);

                    receiveBox.getChildren().add(connectionDetails);
                });
            }
        });

        view.getChildren().addAll(sendBox, separator, receiveBox);

        scene = new Scene(view, 400, 250);
        scene.getStylesheets().add(ApplicationMain.class.getResource("/index.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    private void onSend(Device currentDevice){
        if (scene != null){
            SendView sendView = new SendView(currentDevice);
            scene.setRoot(sendView);
        }
    }

    public static void run() {
        launch();
    }
}
