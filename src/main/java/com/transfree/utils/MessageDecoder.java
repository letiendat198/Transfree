package com.transfree.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.ArrayUtils;

import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;

public class MessageDecoder {

    private static final Logger logger = LogManager.getLogger("Socket");

    byte[] rawMessage;
    MessageType.MESSAGE type;
    byte[] rawData;
    String decodedData;
    boolean haveData = false;
    boolean haveHeaders = false;
    HashMap<String, String> headers = new HashMap<>();

    public MessageDecoder(byte[] data) {
        rawMessage = data;

        String mess = new String(Arrays.copyOfRange(rawMessage, 0, 3));
        type = MessageType.parseType(mess);

        rawData = Arrays.copyOfRange(rawMessage,3, rawMessage.length);
        if (!ArrayUtils.isEmpty(rawData)) haveData=true;

        if (type != MessageType.MESSAGE.BIN && !ArrayUtils.isEmpty(rawData)){
            try {
                decodedData = new String(Base64.getDecoder().decode(data)).trim();
                String[] parts = decodedData.split("\n");
                for (String part: parts){
                    String[] splitted = part.split(":");
                    String key = splitted[0].trim();
                    String value = splitted[1].trim();
                    headers.put(key, value);
                    haveHeaders = true;
                }
            }
            catch (Exception e){
                logger.error(e);
            }
        }
    }

    public MessageType.MESSAGE getType() {
        return type;
    }

    public byte[] getRawData() {
        return rawData;
    }

    public String getDecodedData() {
        return decodedData;
    }

    public boolean isHaveData() {
        return haveData;
    }

    public boolean isHaveHeaders() {
        return haveHeaders;
    }

    public String getHeader(String key){
        return headers.get(key);
    }
}
