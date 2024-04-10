package com.transfree.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.ArrayUtils;

import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;

public class MessageDecoder {

    private static final Logger logger = LogManager.getLogger("DECODER");

    byte[] rawMessage;
    MessageType.MESSAGE type;
    byte[] rawData;
    String decodedData;
    boolean haveData = false;
    boolean haveHeaders = false;
    HashMap<String, String> headers = new HashMap<>();
    int dataLen;

    public MessageDecoder(byte[] data) {
        this.rawMessage = data;

        String messType = new String(Arrays.copyOfRange(rawMessage, 0, 3));
        this.type = MessageType.parseType(messType);

        String dataLenHex = new String(Arrays.copyOfRange(rawMessage, 3, 7));
        dataLen = (int) Long.parseLong(dataLenHex, 16);

        rawData = Arrays.copyOfRange(rawMessage,7, rawMessage.length);
        if (dataLen >0) haveData=true;

        if (type != MessageType.MESSAGE.BIN && dataLen>0){
            try {
                String trimmedData = new String(rawData).trim();
                decodedData = new String(Base64.getDecoder().decode(trimmedData.getBytes())).trim();
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

    public static boolean isComplete (byte[] raw){
        if (raw.length < 7) return false;
        byte[] dataLength = Arrays.copyOfRange(raw, 3 , 7);
        int len = (int)Long.parseLong(new String(dataLength), 16);
        return raw.length - 7 == len;
    }

    public static int getDataLength (byte[] raw){
        byte[] dataLength = Arrays.copyOfRange(raw, 3 , 7);
        return (int)Long.parseLong(new String(dataLength), 16);
    }

    public int getDataLength(){
        return dataLen;
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

    public byte[] getRawMessage() {
        return rawMessage;
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
