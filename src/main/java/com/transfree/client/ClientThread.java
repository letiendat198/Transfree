package com.transfree.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.text.StyledEditorKit;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.transfree.utils.FileStatus.STATUS;

public class ClientThread implements Runnable{
    private static final Logger logger = LogManager.getLogger("CLIENT-THREAD");
    Client client;
    String ip;
    int port;
    List<File> fileList = new ArrayList<>();
    BiConsumer<File, STATUS> callback;

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

    public void addCallback(BiConsumer<File, STATUS> callback){
        this.callback = callback;
    }

    @Override
    public void run() {
        client.connect(this.ip, this.port);
        client.handshake();
        for (File file: this.fileList){
            callback.accept(file, STATUS.START);
            boolean status = client.sendFile(file.getPath());
            logger.debug("File sent successfully: {}", status);
            if (status) callback.accept(file, STATUS.SENT);
            else callback.accept(file, STATUS.FAILED);
        }
        client.close();
    }
}
