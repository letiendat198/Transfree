package com.transfree.utils;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;

public class MessageBuilder {

    private static final String TAG = "BUILDER";

    MessageType.MESSAGE type;
    HashMap<String, String> header = new HashMap<>();
    boolean haveHeader = false;
    boolean haveRaw = false;
    byte[] rawBytes;
    private boolean isPretty = false;

    public MessageBuilder addType(MessageType.MESSAGE messageType){
        this.type = messageType;
        return this;
    }
    public MessageBuilder addHeader(String key, String value){
        this.header.put(key, value);
        this.haveHeader = true;
        return this;
    }

    public MessageBuilder addRawBytes(byte[] bytes){
        this.rawBytes = bytes;
        this.haveRaw = true;
        return this;
    }

    public MessageBuilder pretty(){
        this.isPretty = true;
        return this;
    }

    public byte[] build(){
        ByteArrayOutputStream message = new ByteArrayOutputStream();
        try{
            message.write(MessageType.toString(type).getBytes());
            if (this.haveHeader && this.type != MessageType.MESSAGE.BIN) {
                StringBuilder headerString = new StringBuilder();
                for (String key: header.keySet()){
                    headerString.append(key).append(":").append(header.get(key)).append("\n");
                }
                byte[] base64Header = Base64.getEncoder().encode(headerString.toString().getBytes());
                String hexLength = Integer.toHexString(base64Header.length);
                StringBuilder dataLength = new StringBuilder();
                for (int i=0;i<4-hexLength.length();i++){
                    dataLength.append("0");
                }
                dataLength.append(hexLength);
                message.write(dataLength.toString().getBytes());
                message.write(base64Header);
            }
            else if (this.type == MessageType.MESSAGE.BIN && this.haveRaw){
                String hexLength = Integer.toHexString(rawBytes.length);
                StringBuilder dataLength = new StringBuilder();
                for (int i=0;i<4-hexLength.length();i++){
                    dataLength.append("0");
                }
                dataLength.append(hexLength);
                message.write(dataLength.toString().getBytes());
                message.write(rawBytes);
            }
            else{
                message.write("0000".getBytes());
            }
            if (this.isPretty) message.write("\n\r".getBytes());
        }
        catch (IOException ioe){
            Log.e(TAG, "MessageBuilder went wrong", ioe);
        }
        return message.toByteArray();
    }
}
