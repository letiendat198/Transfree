package com.transfree.server;

import com.transfree.notification.ConfirmNotification;
import com.transfree.notification.ProgressNotification;
import com.transfree.utils.SocketRead;
import com.transfree.utils.CommonValues;
import com.transfree.file_io.FileHelper;
import com.transfree.utils.MessageBuilder;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.transfree.utils.MessageDecoder;
import com.transfree.utils.MessageType.MESSAGE;
import com.transfree.utils.Settings;

import static java.lang.Math.min;

import android.content.Context;
import android.net.Uri;
import android.os.SystemClock;
import android.util.Log;

import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;

public class Server implements Runnable {
    private static final String TAG = "SERVER";

    private int MAX_PAYLOAD_SIZE = CommonValues.MAX_PAYLOAD_SIZE;

    private ServerSocket server;
    private Socket socket;
    private InputStream inStream;
    private OutputStream outStream;
    private boolean isAccepted = false;
    private boolean isConfirmed = false;
    private BiConsumer<String, Long> fileReceiveAdd;
    private Consumer<Double> fileProgressUpdate;

    private Context context;

    public Server(Context context, int port){
        this.context = context;
        try {
            server = new ServerSocket(port);
            Log.i(TAG, "Socket listening on localhost:" + port);
        }
        catch (IOException ioe){
            Log.e(TAG, "Server init went wrong", ioe);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                socket = server.accept();
                inStream = socket.getInputStream();
                outStream = socket.getOutputStream();
                String sessionID = UUID.randomUUID().toString();
                Log.d(TAG, "SessionID: " + sessionID);
                while (true) {  //Main session loop
                    if (socket.isClosed()) break;

                    boolean terminated = false;

                    MessageDecoder mess = SocketRead.readSocket(inStream);
//                    logger.debug("{}", new String(mess.getRawMessage()));
                    switch (mess.getType()) {
                        case ACK:
                            Log.i(TAG, "ACK request received");
                            break;
                        case REQ:
                            Log.i(TAG, "REQ request received");
                            if (!mess.isHaveHeaders()) {
                                outStream.write(new MessageBuilder().addType(MESSAGE.RFS).build());
                                Log.i(TAG, "REQ request without headers is unacceptable");
                                break;
                            }
                            String deviceName = mess.getHeader("name");
                            Log.i(TAG, "File upload request from client " + deviceName);
                            requestConfirmation(deviceName);
                            if (isAccepted) outStream.write(new MessageBuilder().addType(MESSAGE.ATH).addHeader("sessionID", sessionID).build());
                            else outStream.write(new MessageBuilder().addType(MESSAGE.RFS).build());

                            break;
                        case BGN:
                            Log.i(TAG, "BGN request received");
                            if (!mess.isHaveHeaders() || mess.getHeader("sessionID") == null || !sessionID.equals(mess.getHeader("sessionID"))) {
                                outStream.write(new MessageBuilder().addType(MESSAGE.RFS).build());
                                Log.d(TAG, "sessionID received: " + mess.getHeader("sessionID"));
                                Log.i(TAG, "BGN request: No header or invalid sessionID");
                                break;
                            }
                            String fileName = mess.getHeader("fileName");
                            long size = Long.parseLong(mess.getHeader("size"));
                            outStream.write(new MessageBuilder().addType(MESSAGE.ACK).build());

                            long written = 0;

                            // ANDROID SPECIFIC FILE CREATION
                            Preferences.Key<String> key = PreferencesKeys.stringKey("savePath");
                            String uri = new Settings(context).blockingReadKey(key);
                            Uri fileUri = FileHelper.Companion.createFileFromTreeUri(context, Uri.parse(uri), fileName);
                            assert fileUri != null;
                            OutputStream fileOut = new FileHelper(context, fileUri).getOutputStream();

                            //CREATE PROGRESS NOTIFICATION
                            ProgressNotification progressNotification = new ProgressNotification(context, fileName);
                            progressNotification.issue();

                            int previousProgress = 0;

                            Log.i(TAG, "Enter file transfer mode");
                            while (written < size) {
                                MessageDecoder dataDecoder = SocketRead.readSocket(inStream);

                                if (dataDecoder.getType() == MESSAGE.EOF) {
                                    outStream.write(new MessageBuilder().addType(MESSAGE.ACK).build());
                                    Log.i(TAG, "EOF sent by client");
                                    break;
                                } else if (dataDecoder.getType() == MESSAGE.BIN) {
                                    if (dataDecoder.isHaveData()){
                                        byte[] fileData = dataDecoder.getRawData();
                                        fileOut.write(fileData);
                                        written += dataDecoder.getDataLength();
                                    }
                                } else {
                                    outStream.write(new MessageBuilder().addType(MESSAGE.RFS).build());
                                    Log.i(TAG, dataDecoder.getType() + " request won't be handled in file transfer mode");
                                }
                                Log.d(TAG, String.format("Written: %s. Expected: %s", written, size));
                                int currentProgress = (int) (written * 100 / size);
                                if (currentProgress - previousProgress > 2){
                                    progressNotification.updateProgress(currentProgress);
                                }
                                previousProgress = currentProgress;

                            }
                            Log.i(TAG, "Done writing to file, written " + written);
                            outStream.write(new MessageBuilder().addType(MESSAGE.COM).build());
                            fileOut.close();
                            progressNotification.onFinished();
                            break;
                        case BIN:
                            outStream.write(new MessageBuilder().addType(MESSAGE.RFS).build());
                            Log.i(TAG, "BIN request outside of file transfer mode");
                            break;
                        case EOF:
                            outStream.write(new MessageBuilder().addType(MESSAGE.RFS).build());
                            Log.i(TAG, "EOF request outside of file transfer mode");
                            break;
                        case END:
                            Log.i(TAG, "END request received");
                            outStream.write(new MessageBuilder().addType(MESSAGE.ACK).build());
                            int len = inStream.read();
                            while (len!=-1){
                                Log.i(TAG, "Waiting for close on client side");
                                len = inStream.read();
                            }
                            terminated = true;
                            Log.i(TAG, "Closing socket");
                            break;
                        default:
                            outStream.write(new MessageBuilder().addType(MESSAGE.NSM).build());
                            Log.i(TAG,"No such message");
                            break;
                    }
                    if (terminated) break;
                }
                socket.close();
            } catch (IOException ioe) {
                Log.e(TAG, "Server run went wrong", ioe);
            }
        }
    }

    private void requestConfirmation(String deviceName){
        new ConfirmNotification(context, deviceName).issue();
        int waitTime = 10;
        int count = 0;
        Log.d(TAG, Boolean.toString(this.isConfirmed));
        while (!this.isConfirmed){
            Log.d(TAG, "Waiting");
            count++;
            try{
                SystemClock.sleep(1000);
            }
            catch (Exception e){
                Log.e(TAG, String.valueOf(e));
            }
            if (count==waitTime) break;
        }
        this.isConfirmed = false;
    }

    public void onConfirmCallback(Boolean isAccepted){
        Log.d(TAG, "Request confirm result: " + isAccepted);
        this.isAccepted = isAccepted;
        this.isConfirmed = true;
    }
}
