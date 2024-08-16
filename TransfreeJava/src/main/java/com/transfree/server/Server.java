package com.transfree.server;

import com.transfree.message.Message;
import com.transfree.utils.SocketRead;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;

import static java.lang.Math.min;

public class Server implements Runnable {
    private static final Logger logger = LogManager.getLogger("SERVER");

    private ServerSocket server;
    private Socket socket;
    private InputStream inStream;
    private OutputStream outStream;
    private ServerGUIInterface guiInterface;

    public Server(){
        try {
            server = new ServerSocket(0);
            logger.info("Socket listening on localhost: {}", server.getLocalPort());
        }
        catch (IOException ioe){
            logger.error(ioe);
        }
    }

    public void setGuiInterface(ServerGUIInterface guiInterface){
        this.guiInterface = guiInterface;
    }

    public int getPort(){
        return server.getLocalPort();
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
                RequestHandler sessionHandler = new RequestHandler(inStream, outStream, sessionID, guiInterface);
                RequestDispatcher dispatcher = new RequestDispatcher(sessionHandler);
                while (true) {  //Main session loop
                    if (socket.isClosed()) break;

                    Message message = SocketRead.readMessage(inStream);

                    dispatcher.dispatch(message);

                    if (sessionHandler.isTerminated()) break;
                }
                socket.close();
            } catch (Exception ioe) {
                logger.error("{} Caused by: {}", ioe,ioe.getCause());
            }
        }
    }
}
