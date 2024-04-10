package com.transfree.client;

import java.util.ArrayList;
import java.util.List;

public class ClientThread implements Runnable{
    Client client;
    String ip;
    int port;
    List<String> fileList = new ArrayList<String>();
    public ClientThread(String ip, int port){
        this.client = new Client();
        this.ip = ip;
        this.port = port;
    }
    public void addFile(String path){
        this.fileList.add(path);
    }
    public void addFiles(List<String> pathList){
        this.fileList.addAll(pathList);
    }
    @Override
    public void run() {
        client.connect(this.ip, this.port);
        client.handshake();
        for (String path: this.fileList){
            client.sendFile(path);
        }
        client.close();
    }
}
