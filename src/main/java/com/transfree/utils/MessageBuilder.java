package com.transfree.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;

public class MessageBuilder {

    private static final Logger logger = LogManager.getLogger("Socket");

    MessageType.MESSAGE type;
    HashMap<String, String> header;
    boolean haveHeader = false;
    boolean haveRaw = false;
    byte[] rawBytes;
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

    public byte[] build(){
        ByteArrayOutputStream message = new ByteArrayOutputStream();
        try{
            message.write(MessageType.toString(type).getBytes());
            if (this.haveHeader && this.type != MessageType.MESSAGE.BIN) {
                StringBuilder headerString = new StringBuilder();
                for (String key: header.keySet()){
                    headerString.append(key).append(":").append(header.get(key)).append("\n");
                }
                message.write(Base64.getEncoder().encode(headerString.toString().getBytes()));
            }

            if (this.type == MessageType.MESSAGE.BIN && this.haveRaw){
                message.write(rawBytes);
            }
        }
        catch (IOException ioe){
            logger.error(ioe);
        }
        return message.toByteArray();
    }
}
