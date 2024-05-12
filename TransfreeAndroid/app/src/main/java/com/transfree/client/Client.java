package com.transfree.client;

import com.transfree.utils.SocketRead;
import com.transfree.utils.CommonValues;
import com.transfree.file_io.FileHelper;
import com.transfree.utils.MessageBuilder;
import com.transfree.utils.MessageDecoder;
import com.transfree.utils.MessageType.MESSAGE;

import android.content.Context;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;

import static java.lang.Math.min;
import static java.lang.Thread.sleep;

public class Client {
    private static final String TAG = "CLIENT";
    private String deviceName = "Unknown";
    private InputStream inStream;
    private OutputStream outStream;
    private String sessionID;
    private int MAX_PAYLOAD_SIZE = CommonValues.MAX_PAYLOAD_SIZE;
    private Socket socket;

    private Context context;

    public Client(Context context) {
        this.context = context;
        try {
            this.deviceName = Settings.Global.getString(context.getContentResolver(), Settings.Global.DEVICE_NAME);
            Log.d(TAG, deviceName);
        } catch (Exception e) {
            Log.e(TAG,e.toString());
        }

    }

    public void connect(String ip, int port) {
        try {
            this.socket = new Socket();
            this.socket.connect(new InetSocketAddress(ip, port), 1000);
            this.inStream = socket.getInputStream();
            this.outStream = socket.getOutputStream();
        } catch (Exception e) {
            Log.e(TAG, "Connect went wrong", e);
        }
    }

    public void handshake() {
        if (this.inStream != null && this.outStream != null) {
            try {
                outStream.write(new MessageBuilder().addType(MESSAGE.REQ).addHeader("name", this.deviceName).build());
                MessageDecoder mess = SocketRead.readSocket(inStream);
                if (mess != null && mess.getType() == MESSAGE.ATH) {
                    this.sessionID = mess.getHeader("sessionID");
                }

            } catch (Exception e) {
                Log.e(TAG, "Handshake went wrong", e);
            }
        }
    }

    public boolean sendFile(Uri uri) {
        if (this.inStream == null || this.outStream == null) {
            return false;
        }
        try {
            FileHelper fileDetails = new FileHelper(context, uri);
            String fileName = fileDetails.getName();
            long size = fileDetails.getSize();
            outStream.write(new MessageBuilder().addType(MESSAGE.BGN)
                    .addHeader("sessionID", this.sessionID)
                    .addHeader("fileName", fileName)
                    .addHeader("size", Long.toString(size))
                    .build());
            MessageDecoder mess = SocketRead.readSocket(inStream);
            Log.d(TAG,new String(mess.getRawMessage()));
            if (mess != null && mess.getType() == MESSAGE.ACK) {
                InputStream fileStream = context.getContentResolver().openInputStream(uri);
                long sent = 0;
                while (true) {  //Read until EOF is sent => Guarantee server will stop
                    byte[] buf = new byte[MAX_PAYLOAD_SIZE - 7];
                    int len = fileStream.read(buf);
                    if (len > 0) {
                        byte[] message = new MessageBuilder().addType(MESSAGE.BIN).addRawBytes(Arrays.copyOfRange(buf, 0, len)).build();
                        outStream.write(message);
                        sent += len;
                    } else {
                        outStream.write(new MessageBuilder().addType(MESSAGE.EOF).build());
                        break;
                    }
                }
                // If server exit on its own => COM then RFS (in response to EOF)
                // Else, server exit via EOF => ACK
                MessageDecoder conf = SocketRead.readSocket(inStream);
                if (conf == null || conf.getType() != MESSAGE.COM) return false;
                SocketRead.readSocket(inStream); // Exhaust RFS
                fileStream.close();
                return true;
            }

        }
        catch (Exception e) {
            Log.e(TAG, "sendFile went wrong", e);
        }
        return false;
    }

    public void close() {
        try {
            if (this.outStream != null) {
                this.outStream.write(new MessageBuilder().addType(MESSAGE.END).build());
                MessageDecoder mess = SocketRead.readSocket(inStream);
                if (mess.getType()==MESSAGE.ACK){
                    socket.close();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "close went wrong", e);
        }
    }
}
