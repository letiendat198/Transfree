package com.transfree.service_discovery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Provider;

public class ServiceDiscovery{
    private static final Logger logger = LogManager.getLogger("SERVICE_DISCOVERY");

    private JmDNS jmDNS;

    public ServiceDiscovery(){
        try {
            jmDNS = JmDNS.create();
            jmDNS.addServiceListener("_transfree._tcp.", new Listener());
        }
        catch (Exception e){
            logger.error(e);
        }
    }

    public void registerService(String name, int port){
        try {
            ServiceInfo serviceInfo = ServiceInfo.create("_transfree._tcp.", name, port, "");
            jmDNS.registerService(serviceInfo);
        }
        catch (Exception e){
            logger.error(e);
        }
    }

    private static class Listener implements ServiceListener {

        @Override
        public void serviceAdded(ServiceEvent serviceEvent) {
            logger.info("Service found: {}", serviceEvent.getName());
        }

        @Override
        public void serviceRemoved(ServiceEvent serviceEvent) {
            logger.info("Service removed: {}", serviceEvent.getName());
        }

        @Override
        public void serviceResolved(ServiceEvent serviceEvent) {
            logger.info("Resolve service: {}", serviceEvent.getName());
        }
    }
}
