package com.transfree.service_discovery

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log
import android.widget.Toast
import java.lang.Error
import java.net.InetAddress

class ServiceDiscovery(private val context: Context, private val callback: (String, Int, InetAddress) -> Unit)  {
    private val TAG = "DISCOVERY"
    private var name = "TransfreeAndroid"
    private val type = "_transfree._tcp."
    private val nsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager

    fun setName(newName: String){
        name = newName
    }

    fun registerService(port: Int){
        val serviceInfo = NsdServiceInfo().apply {
            serviceName = name
            serviceType = type
            setPort(port)
        }
        nsdManager.apply {
            registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, registerListener)
        }
    }

    private val registerListener = object: NsdManager.RegistrationListener {
        override fun onRegistrationFailed(serviceInfo: NsdServiceInfo?, errorCode: Int) {
            Log.e(TAG, "Service Registeration Failed. Error code: $errorCode")
        }

        override fun onUnregistrationFailed(serviceInfo: NsdServiceInfo?, errorCode: Int) {
            Log.e(TAG, "Service Registeration Failed. Error code: $errorCode")
        }

        override fun onServiceRegistered(serviceInfo: NsdServiceInfo?) {
            if (serviceInfo != null) name = serviceInfo.serviceName
        }

        override fun onServiceUnregistered(serviceInfo: NsdServiceInfo?) {
            Log.d(TAG, "Service Unregistered")
        }

    }

    private val discoveryListener = object: NsdManager.DiscoveryListener {
            override fun onStartDiscoveryFailed(serviceType: String?, errorCode: Int) {
                Log.e(TAG, "Service Discovery Failed. Error code: $errorCode")
                Toast.makeText(context, "Service Discovery Failed", Toast.LENGTH_SHORT).show()
                nsdManager.stopServiceDiscovery(this)
            }

            override fun onStopDiscoveryFailed(serviceType: String?, errorCode: Int) {
                Log.e(TAG, "Stopping Discovery Failed. Error code: $errorCode")
                Toast.makeText(context, "Stopping Service Discovery Failed", Toast.LENGTH_SHORT).show()
                nsdManager.stopServiceDiscovery(this)
            }

            override fun onDiscoveryStarted(p0: String?) {
                Log.d(TAG, "Service Discovery Started")
            }

            override fun onDiscoveryStopped(p0: String?) {
                Log.d(TAG, "Service Discovery Stopped")
            }

            override fun onServiceFound(service: NsdServiceInfo?) {
                Log.d(TAG, "Service Found: $service")
                if(service?.serviceType != type) {
                    Log.d(TAG, "Unknown Service Type: ${service?.serviceType}")
                    return
                }
                if (service.serviceName == name) {
                    Log.d(TAG, "Same Machine, IGNORE")
                }
                nsdManager.resolveService(service, resolveListener)
            }

            override fun onServiceLost(service: NsdServiceInfo?) {
                Log.d(TAG, "Service Lost: $service")
            }
    }

    private val resolveListener = object: NsdManager.ResolveListener{
        override fun onResolveFailed(serviceInfo: NsdServiceInfo?, errorCode: Int) {
            Log.e(TAG, "Service Resolve Failed. Error code: $errorCode")
        }

        override fun onServiceResolved(serviceInfo: NsdServiceInfo?) {
            Log.d(TAG, "Resolve Success: $serviceInfo")

            if (serviceInfo?.serviceName == name){
                Log.d(TAG, "Same machine. RETURNING")
//                return
            }
            if (serviceInfo == null) return
            val port: Int = serviceInfo.port
            val host: InetAddress = serviceInfo.host
            val name: String = serviceInfo.serviceName
            callback(name, port, host)
        }
    }

    fun discoverService(){
        nsdManager.discoverServices(type, NsdManager.PROTOCOL_DNS_SD, discoveryListener)
    }

    fun unregisterService(){
        nsdManager.apply {
            unregisterService(registerListener)
            stopServiceDiscovery(discoveryListener)
        }
    }
}