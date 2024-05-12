package com.transfree.utils;

import com.transfree.utils.CommonValues;
import com.transfree.utils.MessageDecoder;

import java.io.InputStream;
import java.util.Arrays;

import static java.lang.Math.min;

import android.util.Log;

public class SocketRead {
    private static final String TAG = "SOCKETIO";
    private static final int MAX_PAYLOAD_SIZE = CommonValues.MAX_PAYLOAD_SIZE;
    public static MessageDecoder readSocket (InputStream inStream){
        if (inStream != null){
            try{
                byte[] buf = new byte[MAX_PAYLOAD_SIZE];
                int len = inStream.read(buf, 0, 7);

                //Read until 7 bytes
                while (len < 7) {
                    int read = inStream.read(buf, len, 7 - len);
                    if (read > 0) len += read;
                }
                // Read until message is complete
                while (!MessageDecoder.isComplete(Arrays.copyOfRange(buf, 0, len))) {
//                    logger.debug("Data incomplete, current buffer: {}. Actual data: {}. Expected: {}", len, len-7,MessageDecoder.getDataLength(buf));
                    // Read the rest of data or until MAX_PAYLOAD_SIZE to avoid overflow into buffer
//                    logger.debug("Try to read {} bytes more", min(MessageDecoder.getDataLength(buf) - len + 7, MAX_PAYLOAD_SIZE - len));
                    int read = inStream.read(buf, len, min(MessageDecoder.getDataLength(buf) - len + 7, MAX_PAYLOAD_SIZE - len));
                    if (read > 0) len += read;
//                    if (read>0) logger.debug("Read {} bytes more", read);
                }
                Log.d(TAG, new String(Arrays.copyOfRange(buf, 0, 3)));
                return new MessageDecoder(Arrays.copyOfRange(buf, 0, len));
            }
            catch (Exception e){
                Log.e(TAG, "ReadSocket went wrong", e);
            }
        }
        return null;
    }
}
