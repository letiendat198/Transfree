package com.transfree.server;

import com.transfree.socketio.SocketRead;
import com.transfree.ui.RequestPopup;
import com.transfree.utils.CommonValues;
import com.transfree.utils.MessageBuilder;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.transfree.utils.MessageDecoder;
import com.transfree.utils.MessageType.MESSAGE;
import org.apache.logging.log4j.core.util.ArrayUtils;

import static java.lang.Math.min;

public class Server implements Runnable {
    private static final Logger logger = LogManager.getLogger("SERVER");

    private int MAX_PAYLOAD_SIZE = CommonValues.MAX_PAYLOAD_SIZE;

    private ServerSocket server;
    private Socket socket;
    private InputStream inStream;
    private OutputStream outStream;
    private boolean isAccepted = false;
    private boolean isConfirmed = false;
    private BiConsumer<String, Long> fileReceiveAdd;
    private Consumer<Double> fileProgressUpdate;

    public Server(int port){
        try {
            server = new ServerSocket(port);
            logger.info("Socket listening on localhost:8000");
        }
        catch (IOException ioe){
            logger.error(ioe);
        }
    }

    public void addCallbacks(BiConsumer<String, Long> fileReceiveAdd, Consumer<Double> fileProgressUpdate){
        this.fileReceiveAdd = fileReceiveAdd;
        this.fileProgressUpdate = fileProgressUpdate;
    }

    @Override
    public void run() {
        while (true) {
            try {
                socket = server.accept();
                inStream = socket.getInputStream();
                outStream = socket.getOutputStream();
                String sessionID = UUID.randomUUID().toString();
                logger.debug("SessionID: {}", sessionID);
                while (true) {  //Main session loop
                    if (socket.isClosed()) break;

                    boolean terminated = false;

                    MessageDecoder mess = SocketRead.readSocket(inStream);
                    logger.debug("{}", new String(mess.getRawMessage()));
                    switch (mess.getType()) {
                        case ACK:
                            logger.info("ACK request received");
                            break;
                        case REQ:
                            logger.info("REQ request received");
                            if (!mess.isHaveHeaders()) {
                                outStream.write(new MessageBuilder().addType(MESSAGE.RFS).build());
                                logger.info("REQ request without headers is unacceptable");
                                break;
                            }
                            String deviceName = mess.getHeader("name");
                            logger.info("File upload request from client {}", deviceName);
                            requestConfirmation(deviceName);
                            if (isAccepted) outStream.write(new MessageBuilder().addType(MESSAGE.ATH).addHeader("sessionID", sessionID).build());
                            else outStream.write(new MessageBuilder().addType(MESSAGE.RFS).build());

                            break;
                        case BGN:
                            logger.info("BGN request received");
                            if (!mess.isHaveHeaders() || mess.getHeader("sessionID") == null || !sessionID.equals(mess.getHeader("sessionID"))) {
                                outStream.write(new MessageBuilder().addType(MESSAGE.RFS).build());
                                logger.debug("sessionID received: {}", mess.getHeader("sessionID"));
                                logger.info("BGN request: No header or invalid sessionID");
                                break;
                            }
                            String fileName = mess.getHeader("fileName");
                            long size = Long.parseLong(mess.getHeader("size"));
                            this.addReceiveFile(fileName, size);  // Add receive entry to GUI
                            outStream.write(new MessageBuilder().addType(MESSAGE.ACK).build());

                            long written = 0;
                            FileOutputStream fileOut = new FileOutputStream(fileName);
                            logger.info("Enter file transfer mode");
                            while (written < size) {
                                MessageDecoder dataDecoder = SocketRead.readSocket(inStream);

                                if (dataDecoder.getType() == MESSAGE.EOF) {
                                    outStream.write(new MessageBuilder().addType(MESSAGE.ACK).build());
                                    logger.info("EOF sent by client");
                                    break;
                                } else if (dataDecoder.getType() == MESSAGE.BIN) {
                                    if (dataDecoder.isHaveData()){
                                        byte[] fileData = dataDecoder.getRawData();
                                        fileOut.write(fileData);
                                        written += dataDecoder.getDataLength();
                                    }
                                } else {
                                    outStream.write(new MessageBuilder().addType(MESSAGE.RFS).build());
                                    logger.info("{} request won't be handled in file transfer mode", dataDecoder.getType());
                                }
                                logger.debug("Written: {}. Expected: {}", written, size);
                                this.setFileProgress((double)written / (double)size);
                            }
                            logger.info("Done writing to file, written {}", written);
                            outStream.write(new MessageBuilder().addType(MESSAGE.COM).build());
                            fileOut.close();
                            break;
                        case BIN:
                            outStream.write(new MessageBuilder().addType(MESSAGE.RFS).build());
                            logger.info("BIN request outside of file transfer mode");
                            break;
                        case EOF:
                            outStream.write(new MessageBuilder().addType(MESSAGE.RFS).build());
                            logger.info("EOF request outside of file transfer mode");
                            break;
                        case END:
                            logger.info("END request received");
                            outStream.write(new MessageBuilder().addType(MESSAGE.ACK).build());
                            int len = inStream.read();
                            while (len!=-1){
                                logger.info("Waiting for close on client side");
                                len = inStream.read();
                            }
                            terminated = true;
                            logger.info("Closing socket");
                            break;
                        default:
                            outStream.write(new MessageBuilder().addType(MESSAGE.NSM).build());
                            logger.info("No such message");
                            break;
                    }
                    if (terminated) break;
                }
                socket.close();
            } catch (IOException ioe) {
                logger.error(ioe);
            }
        }
    }

    private void requestConfirmation(String deviceName){
        Platform.runLater(() -> {
            Stage requestPopup = new RequestPopup(deviceName, this::onConfirmCallback);
        });
        int waitTime = 10;
        int count = 0;
        while (!this.isConfirmed){
            count++;
            try{
                Thread.sleep(1000);
            }
            catch (Exception e){
                logger.error(e);
            }
            if (count==waitTime) break;
        }
        this.isConfirmed = false;
    }

    public void onConfirmCallback(Boolean isAccepted){
        logger.debug("Request confirm result: {}", isAccepted);
        this.isAccepted = isAccepted;
        this.isConfirmed = true;
    }

    private void addReceiveFile(String name, long size){
        if (this.fileReceiveAdd == null) return;
        try{
            Platform.runLater(() -> {
                this.fileReceiveAdd.accept(name, size);
            });
        }
        catch (Exception e){
            logger.error(e);
        }
    }

    private void setFileProgress(double prog){
        if (this.fileProgressUpdate == null) return;
        try{
            Platform.runLater(() -> {
                this.fileProgressUpdate.accept(prog);
            });
        }
        catch (Exception e){
            logger.error(e);
        }
    }
}
