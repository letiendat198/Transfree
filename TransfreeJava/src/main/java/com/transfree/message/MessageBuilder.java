package com.transfree.message;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;

public class MessageBuilder {

    private static final Logger logger = LogManager.getLogger("BUILDER");

    private Message message = new Message();
    private boolean isPretty = false;
    private HashMap<String, String> header = new HashMap<>();

    public MessageBuilder addType(MessageType.MESSAGE messageType){
        message.setMessageType(messageType);
        return this;
    }
    public MessageBuilder addHeader(String key, String value){
        header.put(key, value);
        message.setMessageHeader(header);
        return this;
    }

    public MessageBuilder addRawBytes(byte[] bytes){
        message.setRawData(bytes);
        return this;
    }

    public MessageBuilder pretty(boolean isPretty){
        this.isPretty = isPretty;
        return this;
    }

    public byte[] build(){
        logger.debug(new String(message.toByteArray(false)));
        return message.toByteArray(isPretty);
    }


}
