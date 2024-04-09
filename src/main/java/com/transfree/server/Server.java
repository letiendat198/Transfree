package com.transfree.server;

import com.transfree.utils.MessageBuilder;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
import java.util.UUID;

import com.transfree.utils.MessageDecoder;
import com.transfree.utils.MessageType.MESSAGE;

public class Server implements Runnable {
    private static final Logger logger = LogManager.getLogger("Socket");

    private ServerSocket server;
    private Socket socket;
    private InputStream inStream;
    private OutputStream outStream;

    public Server(int port){
        try {
            server = new ServerSocket(port);
            logger.info("Socket listening on localhost:8000");
        }
        catch (IOException ioe){
            logger.error(ioe);
        }
    }

    @Override
    public void run(){
        try{
            socket = server.accept();
            inStream = socket.getInputStream();
            outStream = socket.getOutputStream();

            outStream.write("Welcome to Transfree\n\r".getBytes());
            while (true){  //Main session loop
                if (socket.isClosed()) break;
                String sessionID = UUID.randomUUID().toString();

                boolean terminated = false;
                byte[] buf = new byte[1024];
                int len = inStream.read(buf);
                logger.debug(buf);

                if (len>=3){
                    MessageDecoder mess = new MessageDecoder(buf);
                    logger.debug(mess.getRawData());
                    switch (mess.getType()) {
                        case ACK:
                            logger.info("ACK request received");
                            break;
                        case INT:  //INITIATE
                            logger.info("Initiate request received");
                            if (mess.isHaveHeaders()){
                                String deviceName = mess.getHeader("name");
                                String mac = mess.getHeader("mac");
                                logger.info("Initiate request from client {} with MAC: {}", deviceName, mac);
                                MessageBuilder resp = new MessageBuilder().addType(MESSAGE.ATH).addHeader("sessionID",sessionID);
                                outStream.write(resp.build());
                            }
                            else{
                                outStream.write(new MessageBuilder().addType(MESSAGE.RFS).build());
                                logger.info("Initiate request without headers is unacceptable");
                            }
                            break;
                        case REQ:  //File upload request
                            logger.info("REQ request received");
                            if (mess.isHaveHeaders()){
                                String id = mess.getHeader("sessionID");
                                logger.info("File upload requested with sessionID: {}", id);
                                if (Objects.equals(sessionID, id)){  //Potential vulnerability. Look into null safe comparison
                                    outStream.write(new MessageBuilder().addType(MESSAGE.ACK).build());
                                }
                                else{
                                    outStream.write(new MessageBuilder().addType(MESSAGE.RFS).build());
                                    logger.info("Invalid sessionID");
                                }
                            }
                            else{
                                outStream.write(new MessageBuilder().addType(MESSAGE.RFS).build());
                                logger.info("REQ request without headers is unacceptable");
                            }
                            break;
                        case INF:
                            logger.info("Info request received");
                            if (mess.isHaveHeaders()){
                                String id = mess.getHeader("sessionID");
                                String fileName = mess.getHeader("fileName");
                                int size = Integer.parseInt(mess.getHeader("size"));
                                if (Objects.equals(sessionID, id)){  //Potential vulnerability. Look into null safe comparison
                                    outStream.write(new MessageBuilder().addType(MESSAGE.ACK).build());
                                }
                                else{
                                    outStream.write(new MessageBuilder().addType(MESSAGE.RFS).build());
                                    logger.info("Invalid sessionID");
                                }
                            }
                            else{
                                outStream.write(new MessageBuilder().addType(MESSAGE.RFS).build());
                                logger.info("INF request without headers is unacceptable");
                            }
                            break;
                        case END:
                            logger.info("END request received");
                            terminated = true;
                            break;
                    }
                }
                if (terminated) break;
            }
        }
        catch (IOException ioe){
            logger.error(ioe);
        }
    }
}
