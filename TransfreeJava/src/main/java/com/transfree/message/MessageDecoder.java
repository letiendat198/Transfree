package com.transfree.message;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;

public class MessageDecoder {

    private static final Logger logger = LogManager.getLogger("DECODER");

    public static Message decode(byte[] data) {
        Message message = new Message();

        String messType = new String(Arrays.copyOfRange(data, 0, 3));
        message.setMessageType(MessageType.parseType(messType));

        String dataLenHex = new String(Arrays.copyOfRange(data, 3, 7));
        int dataLen = (int) Long.parseLong(dataLenHex, 16);
        message.setMessageLength(dataLen);

        byte[] rawData = Arrays.copyOfRange(data,7, data.length);
        message.setRawData(rawData);

        if (message.getMessageType() != MessageType.MESSAGE.BIN && dataLen>0){
            try {
                String trimmedData = new String(rawData).trim();
                String decodedData = new String(Base64.getDecoder().decode(trimmedData.getBytes())).trim();
                message.setDecodedData(decodedData);
                String[] parts = decodedData.split("\n");
                HashMap<String, String> headers = new HashMap<>();
                for (String part: parts){
                    String[] splitted = part.split(":");
                    String key = splitted[0].trim();
                    String value = splitted[1].trim();
                    headers.put(key, value);
                }
                message.setMessageHeader(headers);
                logger.debug(message.getMessageHeader());
            }
            catch (Exception e){
                logger.error(e);
            }
        }
        logger.debug(new String(message.toByteArray(false)));
        return message;
    }
}
