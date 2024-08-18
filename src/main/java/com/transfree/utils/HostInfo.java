package com.transfree.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class HostInfo {
    public interface Callback{
        public default void call(String ip) {}
    }
    private static final Logger logger = LogManager.getLogger("HOST_INFO");
    public static void getHostIP(Callback callback){
            Thread socketThread = new Thread(() -> {
                try(final DatagramSocket socket = new DatagramSocket()){
                    socket.connect(new InetSocketAddress("google.com", 80));
                    callback.call(socket.getLocalAddress().getHostAddress());
                }
                catch(Exception e){
                    logger.error(e);
                }
            });
            socketThread.start();
    }
}
