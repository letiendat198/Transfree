package com.transfree.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ClientThread implements Runnable{
    private static final Logger logger = LogManager.getLogger("CLIENT-THREAD");
    Client client;
    String ip;
    int port;
    List<File> fileList = new ArrayList<>();
    Consumer<File> callback;

    public ClientThread(String ip, int port){
        this.client = new Client();
        this.ip = ip;
        this.port = port;
    }
    public void addFile(File file){
        this.fileList.add(file);
    }
    public void addFiles(List<File> fileList){
        this.fileList.addAll(fileList);
    }
    public void addCallback(Consumer<File> callback){
        this.callback = callback;
    }

    @Override
    public void run() {
        client.connect(this.ip, this.port);
        client.handshake();
        for (File file: this.fileList){
            boolean status = client.sendFile(file.getPath());
            logger.debug("File sent successfully: {}", status);
            callback.accept(file);
        }
        client.close();
    }
}
