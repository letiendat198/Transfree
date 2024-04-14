package com.transfree.socketio;

import com.transfree.utils.MessageDecoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.util.Arrays;

import static com.transfree.utils.CommonValues.MAX_PAYLOAD_SIZE;
import static java.lang.Math.min;

public class SocketRead {
    private static final Logger logger = LogManager.getLogger("SOCKETIO");
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
//                logger.debug(new String(Arrays.copyOfRange(buf, 0, len)));
                return new MessageDecoder(Arrays.copyOfRange(buf, 0, len));
            }
            catch (Exception e){
                logger.error(e);
            }
        }
        return null;
    }
}
