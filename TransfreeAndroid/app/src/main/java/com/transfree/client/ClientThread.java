package com.transfree.client;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.transfree.utils.FileStatus.STATUS;

public class ClientThread implements Runnable{
    private static final String TAG = "CLIENTTHREAD";
    Client client;
    String ip;
    int port;
    List<Uri> fileList = new ArrayList<>();
    BiConsumer<Uri, STATUS> callback;

    public ClientThread(Context context, String ip, int port){
        this.client = new Client(context);
        this.ip = ip;
        this.port = port;
    }
    public void addFile(Uri file){
        this.fileList.add(file);
    }
    public void addFiles(List<Uri> fileList){
        this.fileList.addAll(fileList);
    }

    public void addCallback(BiConsumer<Uri, STATUS> callback){
        this.callback = callback;
    }

    @Override
    public void run() {
        client.connect(this.ip, this.port);
        client.handshake();
        for (Uri uri: this.fileList){
            callback.accept(uri, STATUS.START);
            boolean status = client.sendFile(uri);
            Log.d(TAG, "File sent successfully: " + status);
            if (status) callback.accept(uri, STATUS.SENT);
            else callback.accept(uri, STATUS.FAILED);
        }
        client.close();
    }
}
