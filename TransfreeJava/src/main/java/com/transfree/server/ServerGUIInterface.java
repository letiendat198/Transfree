package com.transfree.server;

public interface ServerGUIInterface {
    public boolean requestConfirmation(String deviceName);
    public Integer receiveFile(String fileName, Long fileSize);
    public void updateProgress(Integer id, Double progress);
}
