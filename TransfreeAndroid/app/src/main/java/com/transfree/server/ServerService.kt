package com.transfree.server

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.transfree.notification.PersistenceNotification
import com.transfree.server.Server
import com.transfree.service_discovery.ServiceDiscovery
import com.transfree.ui.components.DeviceBoxComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class ServerService: Service() {
    private val TAG = "SERVER_SERVICE"
    private var thread:  Thread? = null
    private var server: Server? = null
    private var isRunning = false
    private var port = 0
    private var serviceDiscovery: ServiceDiscovery? = null
    companion object{
        val deviceLiveData: MutableLiveData<HashMap<String, String>> = MutableLiveData<HashMap<String, String>>()
    }

    override fun onCreate() {
        super.onCreate()
        server = Server(this, 0)
        port = server!!.serverSocket.localPort
        thread = Thread(server)

        // Callback when a service is discovered
        serviceDiscovery = ServiceDiscovery(this) {name, port, host ->
            Log.d(TAG, "A new device found!")
            // Dispatch to IO thread
            CoroutineScope(Dispatchers.IO).launch {
                var ip: String
                // Block until ip is resolved
                runBlocking {
                    withContext(Dispatchers.IO){
                        ip = host.hostName
                    }
                }
                // Post data to be observed in MainActivity
                deviceLiveData.postValue(
                    HashMap<String, String>().apply {
                        set("name", name)
                        set("port", port.toString())
                        set("host", ip)
                    }
                )
            }
        }.apply {
            registerService(port)
            discoverService()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        val type = intent?.getStringExtra("Type")
        val confirmation = intent?.getBooleanExtra("Confirm", false)

        if (!isRunning){
            thread?.start()
            isRunning = true
            PersistenceNotification(this).issue()
            Toast.makeText(this, "Transfree server started", Toast.LENGTH_SHORT).show()
        }
        if (type=="CONFIRM"){
            if (confirmation != null){
                Log.d("SERVER_SERVICE", confirmation.toString())
                server?.onConfirmCallback(confirmation)
            }
        }


        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()

        isRunning = false
        Toast.makeText(this, "Transfree Server stopped", Toast.LENGTH_SHORT).show()
        with (NotificationManagerCompat.from(applicationContext)){
            cancel(-1)
        }

        serviceDiscovery?.unregisterService()
    }
}