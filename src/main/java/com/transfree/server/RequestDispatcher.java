package com.transfree.server;

import com.transfree.message.Message;

public class RequestDispatcher {
    RequestHandler handler;
    public RequestDispatcher(RequestHandler handler){
        this.handler = handler;
    }

    public void dispatch(Message message) throws Exception{
        switch (message.getMessageType()){
            case ACK:
                handler.onAcknowledge();
                break;
            case RFS:
                handler.onRefuse();
                break;
            case END:
                handler.onEnd();
                break;
            case ATH:
                handler.onAuth();
                break;
            case BGN:
                handler.onBegin(message);
                break;
            case BIN:
                handler.onBinary();
                break;
            case COM:
                handler.onComplete();
                break;
            case EOF:
                handler.onEOF();
                break;
            case NSM:
                handler.onNSM();
                break;
            case REQ:
                handler.onRequest(message);
                break;
        }
    }
}
