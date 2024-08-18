package com.transfree.views.components;

public class Device {
    String name;
    String os;
    String ip;
    String mac;
    int port;
    boolean isNotTarget = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isNotTarget(){return isNotTarget;}

    public void setNotTarget(boolean isNotTarget) {this.isNotTarget = isNotTarget;}
}
