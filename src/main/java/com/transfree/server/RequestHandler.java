package com.transfree.server;

import com.transfree.gui_interface.ServerGUIInterface;
import com.transfree.message.Message;
import com.transfree.message.MessageBuilder;
import com.transfree.message.MessageType;
import com.transfree.utils.SocketRead;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class RequestHandler implements TransfreeProtocol {
    private static final Logger logger = LogManager.getLogger("REQUEST_HANDLER");

    private InputStream inStream;
    private OutputStream outStream;
    private String sessionId;
    private boolean terminated=false;
    private ServerGUIInterface guiInterface;

    public RequestHandler(InputStream inStream, OutputStream outStream, String sessionId, ServerGUIInterface guiInterface){
        this.inStream = inStream;
        this.outStream = outStream;
        this.sessionId = sessionId;
        this.guiInterface = guiInterface;
    }

    public boolean isTerminated() {
        return terminated;
    }

    @Override
    public void onAcknowledge() {
        logger.info("ACK request received");
    }

    @Override
    public void onRefuse() {

    }

    @Override
    public void onRequest(Message mess) throws IOException {
        logger.info("REQ request received");
        if (!mess.isHaveHeaders()) {
            outStream.write(new MessageBuilder().addType(MessageType.MESSAGE.RFS).build());
            logger.info("REQ request without headers is unacceptable");
            return;
        }
        String deviceName = mess.getMessageHeader().get("name");
        logger.info("File upload request from client {}", deviceName);
        boolean isConfirmed = guiInterface.requestConfirmation(deviceName);
        if (isConfirmed) outStream.write(new MessageBuilder().addType(MessageType.MESSAGE.ATH).addHeader("sessionID", sessionId).build());
        else outStream.write(new MessageBuilder().addType(MessageType.MESSAGE.RFS).build());
    }

    @Override
    public void onBegin(Message mess) throws IOException {
        logger.info("BGN request received");
        if (!mess.isHaveHeaders() || mess.getMessageHeader().get("sessionID") == null || !sessionId.equals(mess.getMessageHeader().get("sessionID"))) {
            outStream.write(new MessageBuilder().addType(MessageType.MESSAGE.RFS).build());
            logger.debug("sessionID received: {}", mess.getMessageHeader().get("sessionID"));
            logger.info("BGN request: No header or invalid sessionID");
            return;
        }
        String fileName = mess.getMessageHeader().get("fileName");
        long size = Long.parseLong(mess.getMessageHeader().get("size"));
        int fileId = guiInterface.receiveFile(fileName, size);  // Add receive entry to GUI
        outStream.write(new MessageBuilder().addType(MessageType.MESSAGE.ACK).build());

        long written = 0;
        FileOutputStream fileOut = new FileOutputStream(fileName);
        logger.info("Enter file transfer mode");
        while (written < size) {
            Message message = SocketRead.readMessage(inStream);

            if (message.getMessageType() == MessageType.MESSAGE.EOF) {
                outStream.write(new MessageBuilder().addType(MessageType.MESSAGE.ACK).build());
                logger.info("EOF sent by client");
                break;
            } else if (message.getMessageType() == MessageType.MESSAGE.BIN) {
                if (message.isHaveData()){
                    byte[] fileData = message.getRawData();
                    fileOut.write(fileData);
                    written += message.getMessageLength();
                }
            } else {
                outStream.write(new MessageBuilder().addType(MessageType.MESSAGE.RFS).build());
                logger.info("{} request won't be handled in file transfer mode", message.getMessageType());
            }
            logger.debug("Written: {}. Expected: {}", written, size);
            guiInterface.updateProgress(fileId, (double)written / (double)size);
        }
        logger.info("Done writing to file, written {}", written);
        outStream.write(new MessageBuilder().addType(MessageType.MESSAGE.COM).build());
        fileOut.close();
    }

    @Override
    public void onBinary() throws IOException {
        outStream.write(new MessageBuilder().addType(MessageType.MESSAGE.RFS).build());
        logger.info("BIN request outside of file transfer mode");
    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onEnd() throws IOException {
        logger.info("END request received");
        outStream.write(new MessageBuilder().addType(MessageType.MESSAGE.ACK).build());
        int len = inStream.read();
        while (len!=-1){
            logger.info("Waiting for close on client side");
            len = inStream.read();
        }
        terminated = true;
        logger.info("Closing socket");
    }

    @Override
    public void onAuth() {

    }

    @Override
    public void onEOF() throws IOException {
        outStream.write(new MessageBuilder().addType(MessageType.MESSAGE.RFS).build());
        logger.info("EOF request outside of file transfer mode");
    }

    @Override
    public void onNSM() {

    }
}
