package com.transfree.server;

import com.transfree.message.Message;

import java.io.IOException;

public interface TransfreeProtocol {
    public void onAcknowledge();
    public void onRefuse();
    public void onRequest(Message message) throws IOException;
    public void onBegin(Message message) throws IOException;
    public void onBinary() throws IOException;
    public void onComplete();
    public void onEnd() throws IOException;
    public void onAuth();
    public void onEOF() throws IOException;
    public void onNSM();
}
